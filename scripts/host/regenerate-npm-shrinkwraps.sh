#!/bin/bash
#
# Regenerate npm-shrinkwrap.json for npm-based recipes.
#
# This script derives npm package names from recipe directories (no hardcoded
# versions), installs the latest version from the npm registry, detects the
# resolved version, and renames the .bb recipe file to match.
#
# Run this script from the meta-iot2050 repository root:
#   ./scripts/host/regenerate-npm-shrinkwraps.sh
#
# It is intended for dependency refresh work inside the Debian trixie snapshot
# container so the generated shrinkwrap files match the build environment.
# For node-red, it keeps the latest compatible major line for the local Node.js
# runtime. For unchanged package versions, it skips writing npm-shrinkwrap.json.
# When a shrinkwrap file is rewritten, it preserves the existing top-level name
# field and falls back to "root" if no previous file exists.
#
# Supports two workflows:
#
#   1. Node-RED packages (single npm package per recipe):
#        Derives package name from recipe directory / NPMPN in .bb file
#        Installs latest version from npm registry
#        Renames .bb file to match resolved version
#
#   2. iot2050-eio-webui (self-contained project):
#        Dual shrinkwrap files: npm-shrinkwrap.json (with devDeps)
#        and npm-shrinkwrap.json.nodev (without devDeps, for packaging).
#
# Usage: Run from the meta-iot2050 repo root (inside or outside Docker).
#   ./scripts/host/regenerate-npm-shrinkwraps.sh [--workspace PATH] [--author "Name <email>"]
#   ./scripts/host/regenerate-npm-shrinkwraps.sh [/custom/path/to/meta-iot2050]
#
set -euo pipefail

WORKSPACE="/workspace"
AUTHOR_IDENTITY=""
RECIPES_APP="${WORKSPACE}/meta-node-red/recipes-app"
EIO_WEBUI_DIR="${WORKSPACE}/meta-sm/recipes-app/iot2050-eio-webui/files"
EIO_WEBUI_RECIPE_DIR="${WORKSPACE}/meta-sm/recipes-app/iot2050-eio-webui"

TMPDIR=$(mktemp -d)
trap 'rm -rf "$TMPDIR"' EXIT

usage() {
    echo "Usage: $0 [--workspace PATH] [--author \"Name <email>\"] [WORKSPACE]"
    echo
    echo "Options:"
    echo "  --workspace PATH   Path to meta-iot2050 repository root"
    echo "  --author STRING    Author line for recipe headers, e.g. 'Li Hua Qian <huaqian.li@siemens.com>'"
    echo "  -h, --help         Show this help"
}

while [ $# -gt 0 ]; do
    key="$1"
    case "$key" in
    -h|--help)
        usage
        exit 0
        ;;
    --workspace)
        WORKSPACE="$2"
        shift
        ;;
    --author)
        AUTHOR_IDENTITY="$2"
        shift
        ;;
    --*)
        echo "ERROR: unknown option '$key'" >&2
        usage
        exit 2
        ;;
    *)
        WORKSPACE="$key"
        ;;
    esac
    shift
done

RECIPES_APP="${WORKSPACE}/meta-node-red/recipes-app"
EIO_WEBUI_DIR="${WORKSPACE}/meta-sm/recipes-app/iot2050-eio-webui/files"
EIO_WEBUI_RECIPE_DIR="${WORKSPACE}/meta-sm/recipes-app/iot2050-eio-webui"

# ──────────────────────────────────────────────
# Helpers
# ──────────────────────────────────────────────

# Get npm package name from a recipe directory.
# Priority: NPMPN in .bb file → fallback to directory name (= PN)
get_npm_package_name() {
    local recipe_dir="$1"
    local bb_file
    bb_file=$(find "${RECIPES_APP}/${recipe_dir}" -maxdepth 1 -name "${recipe_dir}_*.bb" 2>/dev/null | head -1)
    if [ -z "$bb_file" ]; then
        echo ""
        return
    fi
    local pkg_name
    pkg_name=$(grep -E '^NPMPN[[:space:]]*=' "$bb_file" 2>/dev/null | sed 's/^NPMPN\s*=\s*"\(.*\)"/\1/')
    echo "${pkg_name:-$recipe_dir}"
}

# List recipe directories that use the npm class and have a files/ subdirectory.
# Skips non-npm recipes (e.g. node-red-gpio which uses dpkg-raw + git SRC_URI).
find_npm_recipes() {
    for dir in "${RECIPES_APP}"/*/; do
        [ -d "${dir}/files" ] || continue
        local dirname
        dirname=$(basename "$dir")
        # Check if .bb file inherits npm class
        local bb_file
        bb_file=$(find "$dir" -maxdepth 1 -name "${dirname}_*.bb" 2>/dev/null | head -1)
        if [ -z "$bb_file" ]; then
            continue
        fi
        grep -q 'inherit\s\+npm' "$bb_file" 2>/dev/null || continue
        echo "$dirname"
    done
}

get_shrinkwrap_name() {
    local shrinkwrap_file="$1"
    if [ ! -f "$shrinkwrap_file" ]; then
        echo "root"
        return
    fi
    python3 - "$shrinkwrap_file" <<'PY'
import json
import sys

path = sys.argv[1]
try:
    with open(path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    name = data.get('name')
    if isinstance(name, str) and name:
        print(name)
    else:
        print('root')
except Exception:
    print('root')
PY
}

set_shrinkwrap_name() {
    local shrinkwrap_file="$1"
    local shrinkwrap_name="$2"
    python3 - "$shrinkwrap_file" "$shrinkwrap_name" <<'PY'
import json
import sys

path = sys.argv[1]
name = sys.argv[2]
with open(path, 'r', encoding='utf-8') as f:
    data = json.load(f)
data['name'] = name
with open(path, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2)
    f.write('\n')
PY
}

resolve_author_identity() {
    if [ -n "${AUTHOR_IDENTITY}" ]; then
        echo "${AUTHOR_IDENTITY}"
        return
    fi

    local git_name git_email
    git_name=$(git -C "${WORKSPACE}" config user.name 2>/dev/null || git config user.name 2>/dev/null || true)
    git_email=$(git -C "${WORKSPACE}" config user.email 2>/dev/null || git config user.email 2>/dev/null || true)

    if [ -n "${git_name}" ] && [ -n "${git_email}" ]; then
        echo "${git_name} <${git_email}>"
    else
        echo ""
    fi
}

update_recipe_metadata() {
    local bb_file="$1"
    local author_identity="$2"
    local current_year
    current_year=$(date +%Y)

    python3 - "$bb_file" "$author_identity" "$current_year" <<'PY'
import re
import sys

path = sys.argv[1]
author = sys.argv[2]
current_year = sys.argv[3]

with open(path, 'r', encoding='utf-8') as f:
    lines = f.read().splitlines()

# Update copyright line end-year to current year.
for i, line in enumerate(lines):
    m = re.match(r'^(#\s*Copyright \(c\) Siemens AG,\s*)(\d{4})(?:-(\d{4}))?\s*$', line)
    if not m:
        continue
    prefix, start_year, end_year = m.group(1), m.group(2), m.group(3)
    if end_year is not None:
        lines[i] = f"{prefix}{start_year}-{current_year}"
    else:
        if start_year != current_year:
            lines[i] = f"{prefix}{start_year}-{current_year}"
    break

# Ensure PR resets to 1 for updated recipe versions.
for i, line in enumerate(lines):
    if re.match(r'^\s*PR\s*=\s*"(\d+)"\s*$', line):
        lines[i] = 'PR = "1"'
        break

# Ensure author line is present.
if author:
    author_line = f"#  {author}"
    if author_line not in lines:
        authors_idx = next((idx for idx, line in enumerate(lines) if line.strip() == '# Authors:'), None)
        if authors_idx is not None:
            insert_idx = authors_idx + 1
            while insert_idx < len(lines) and re.match(r'^#\s{2}.+', lines[insert_idx]):
                insert_idx += 1
            lines.insert(insert_idx, author_line)
        else:
            # Fallback: inject a minimal Authors block right after copyright block.
            copyright_idx = next((idx for idx, line in enumerate(lines) if 'Copyright (c) Siemens AG' in line), None)
            if copyright_idx is not None:
                insert_at = min(copyright_idx + 2, len(lines))
                block = ['# Authors:', author_line, '#']
                lines[insert_at:insert_at] = block

with open(path, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines) + '\n')
PY
}

bump_semver_minor() {
    local version="$1"
    python3 - "$version" <<'PY'
import re
import sys

v = sys.argv[1].strip()
m = re.fullmatch(r'(\d+)\.(\d+)\.(\d+)', v)
if not m:
    raise SystemExit(1)
major, minor, patch = map(int, m.groups())
print(f"{major}.{minor + 1}.0")
PY
}

set_json_version() {
    local json_file="$1"
    local version="$2"
    python3 - "$json_file" "$version" <<'PY'
import json
import sys

path = sys.argv[1]
version = sys.argv[2]
with open(path, 'r', encoding='utf-8') as f:
    data = json.load(f)
data['version'] = version
packages = data.get('packages')
if isinstance(packages, dict):
    root_pkg = packages.get('')
    if isinstance(root_pkg, dict):
        root_pkg['version'] = version
with open(path, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2)
    f.write('\n')
PY
}

# ──────────────────────────────────────────────
# Section 1: Node-RED packages
# ──────────────────────────────────────────────

regenerate_node_red() {
    local recipe_dir="$1"
    local npm_pkg="$2"
    local recipe_path="${RECIPES_APP}/${recipe_dir}"
    local target="${recipe_path}/files/npm-shrinkwrap.json"
    local effective_author

    effective_author=$(resolve_author_identity)

    # Find current .bb file and extract old version
    local bb_file bb_file_base old_version
    bb_file=$(find "${recipe_path}" -maxdepth 1 -name "${recipe_dir}_*.bb" | head -1)
    if [ -z "$bb_file" ]; then
        echo "  SKIP ${recipe_dir}: no ${recipe_dir}_*.bb found"
        return
    fi
    bb_file_base=$(basename "$bb_file")
    old_version=$(echo "$bb_file_base" | sed "s/^${recipe_dir}_//; s/\.bb$//")

    echo "=== [${recipe_dir}] npm:${npm_pkg} (recipe: ${old_version}) ==="

    local target_name
    target_name=$(get_shrinkwrap_name "$target")

    rm -rf "${TMPDIR:?}/"*
    cd "$TMPDIR"

    local install_spec="${npm_pkg}"
    if [ "${npm_pkg}" = "node-red" ]; then
        local node_version node_major node_minor
        node_version=$(node -p "process.versions.node" 2>/dev/null || echo "0.0.0")
        node_major=$(echo "$node_version" | cut -d. -f1)
        node_minor=$(echo "$node_version" | cut -d. -f2)
        node_major=${node_major:-0}
        node_minor=${node_minor:-0}
        if [ "$node_major" -lt 22 ] || { [ "$node_major" -eq 22 ] && [ "$node_minor" -lt 9 ]; }; then
            install_spec="${npm_pkg}@4"
            echo "  INFO: Node.js ${node_version} < 22.9, using ${install_spec} for compatibility."
        fi
    fi

    echo "  npm install --install-strategy=shallow --ignore-scripts ${install_spec}..."
    if ! npm install --install-strategy=shallow --ignore-scripts "${install_spec}" 2>&1 | sed 's/^/  /'; then
        echo "  ERROR: npm install failed for ${npm_pkg}. Skipping."
        return 1
    fi

    if [ ! -f package-lock.json ]; then
        echo "  ERROR: package-lock.json not generated. Skipping."
        return 1
    fi

    # Detect resolved version from installed package.json
    local pkg_json_path new_version
    pkg_json_path="node_modules/${npm_pkg}/package.json"
    if [ ! -f "$pkg_json_path" ]; then
        # Scoped packages: try broader search
        pkg_json_path=$(find node_modules -maxdepth 4 -path "*/${npm_pkg##*/}/package.json" 2>/dev/null | head -1)
    fi

    new_version=$(python3 -c "import json; print(json.load(open('${pkg_json_path}'))['version'])" 2>/dev/null) || {
        echo "  ERROR: Could not detect version for ${npm_pkg}"
        return 1
    }

    echo "  Resolved: ${npm_pkg}@${new_version} (was ${old_version})"

    if [ "$new_version" != "$old_version" ]; then
        mkdir -p "$(dirname "$target")"
        cp package-lock.json "$target"
        set_shrinkwrap_name "$target" "$target_name"
        echo "  npm-shrinkwrap.json: $(wc -c < "$target") bytes (name=${target_name})"

        local new_bb="${recipe_path}/${recipe_dir}_${new_version}.bb"
        echo "  Rename: ${recipe_dir}_${old_version}.bb -> ${recipe_dir}_${new_version}.bb"
        git mv "$bb_file" "$new_bb" 2>/dev/null || mv "$bb_file" "$new_bb"
        update_recipe_metadata "$new_bb" "$effective_author"
        if [ -n "$effective_author" ]; then
            echo "  Recipe metadata updated: PR=1, copyright end-year refreshed, author ensured (${effective_author})"
        else
            echo "  Recipe metadata updated: PR=1, copyright end-year refreshed (author unchanged: no --author and no gitconfig)"
        fi
    else
        echo "  Version unchanged, skipping npm-shrinkwrap.json update"
        echo "  Version unchanged, keeping ${recipe_dir}_${old_version}.bb"
    fi
    echo
}

# ──────────────────────────────────────────────
# Section 2: iot2050-eio-webui (dual shrinkwrap)
# ──────────────────────────────────────────────

regenerate_eio_webui() {
    local files_dir="$1"
    local recipe_dir="${EIO_WEBUI_RECIPE_DIR}"
    local effective_author
    local bb_file bb_file_base old_version new_version new_bb

    effective_author=$(resolve_author_identity)

    bb_file=$(find "${recipe_dir}" -maxdepth 1 -name "iot2050-eio-webui_*.bb" | head -1)
    if [ -z "$bb_file" ]; then
        echo "  ERROR: iot2050-eio-webui_*.bb not found in ${recipe_dir}. Skipping."
        return 1
    fi
    bb_file_base=$(basename "$bb_file")
    old_version=$(echo "$bb_file_base" | sed 's/^iot2050-eio-webui_//; s/\.bb$//')
    new_version=$(bump_semver_minor "$old_version") || {
        echo "  ERROR: iot2050-eio-webui recipe version '${old_version}' is not semver (X.Y.Z)."
        return 1
    }

    echo "=== [eio-webui] ${files_dir} ==="
    echo "  Semver bump: ${old_version} -> ${new_version}"

    if [ ! -f "${files_dir}/package.json" ]; then
        echo "  ERROR: package.json not found in ${files_dir}. Skipping."
        return 1
    fi

    cp "${files_dir}/package.json" "${files_dir}/package.json.bak"

    # ── 2a: npm-shrinkwrap.json.nodev (omit devDependencies) ──
    echo "  --- npm-shrinkwrap.json.nodev (omit devDependencies) ---"

    cp "${files_dir}/package.json.bak" "${files_dir}/package.json"
    set_json_version "${files_dir}/package.json" "$new_version"
    jq 'del(.devDependencies)' "${files_dir}/package.json" > "${files_dir}/package.json.tmp"
    mv "${files_dir}/package.json.tmp" "${files_dir}/package.json"

    rm -rf "${files_dir}/node_modules" "${files_dir}/package-lock.json" "${files_dir}/npm-shrinkwrap.json"
    cd "${files_dir}"

    echo "  npm install --omit=dev --install-strategy=shallow..."
    npm install --omit=dev --install-strategy=shallow 2>&1 | sed 's/^/  /'

    echo "  npm shrinkwrap..."
    npm shrinkwrap 2>&1 | sed 's/^/  /'

    mv npm-shrinkwrap.json npm-shrinkwrap.json.nodev
    echo "  OK: npm-shrinkwrap.json.nodev ($(wc -c < npm-shrinkwrap.json.nodev) bytes)"

    # ── 2b: npm-shrinkwrap.json (with devDependencies) ──
    echo "  --- npm-shrinkwrap.json (with devDependencies) ---"

    cp "${files_dir}/package.json.bak" "${files_dir}/package.json"
    set_json_version "${files_dir}/package.json" "$new_version"

    rm -rf "${files_dir}/node_modules" "${files_dir}/package-lock.json" "${files_dir}/npm-shrinkwrap.json"
    cd "${files_dir}"

    echo "  npm install --install-strategy=shallow..."
    npm install --install-strategy=shallow 2>&1 | sed 's/^/  /'

    echo "  npm shrinkwrap..."
    npm shrinkwrap 2>&1 | sed 's/^/  /'

    set_json_version "${files_dir}/npm-shrinkwrap.json" "$new_version"
    set_json_version "${files_dir}/npm-shrinkwrap.json.nodev" "$new_version"

    echo "  OK: npm-shrinkwrap.json ($(wc -c < npm-shrinkwrap.json) bytes)"

    rm -f "${files_dir}/package.json.bak"

    if [ "$new_version" != "$old_version" ]; then
        new_bb="${recipe_dir}/iot2050-eio-webui_${new_version}.bb"
        echo "  Rename: iot2050-eio-webui_${old_version}.bb -> iot2050-eio-webui_${new_version}.bb"
        git mv "$bb_file" "$new_bb" 2>/dev/null || mv "$bb_file" "$new_bb"
        update_recipe_metadata "$new_bb" "$effective_author"
        if [ -n "$effective_author" ]; then
            echo "  Recipe metadata updated: PR=1, copyright end-year refreshed, author ensured (${effective_author})"
        else
            echo "  Recipe metadata updated: PR=1, copyright end-year refreshed (author unchanged: no --author and no gitconfig)"
        fi
    else
        echo "  Version unchanged, keeping iot2050-eio-webui_${old_version}.bb"
    fi

    rm -rf "${files_dir}/node_modules"
    echo
}

# ═══════════════════════════════════════════════
# Main
# ═══════════════════════════════════════════════

echo "============================================"
echo " Regenerating npm-shrinkwrap files"
echo " Workspace: ${WORKSPACE}"
echo "============================================"
echo

# Section 1: Node-RED packages
echo "=== Section 1: Node-RED packages ==="
while IFS= read -r recipe_dir; do
    npm_pkg=$(get_npm_package_name "$recipe_dir")
    if [ -z "$npm_pkg" ]; then
        echo "  SKIP ${recipe_dir}: cannot determine npm package name"
        continue
    fi
    regenerate_node_red "$recipe_dir" "$npm_pkg"
done < <(find_npm_recipes)

# Section 2: iot2050-eio-webui
echo "=== Section 2: iot2050-eio-webui ==="
if [ -d "${EIO_WEBUI_DIR}" ]; then
    regenerate_eio_webui "${EIO_WEBUI_DIR}"
else
    echo "  WARNING: ${EIO_WEBUI_DIR} not found. Skipping."
fi

echo "============================================"
echo " All done"
echo "============================================"
