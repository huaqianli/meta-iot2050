# meta-example – IoT2050 Demo / Example Layer

_Applies to meta-iot2050 v1.6.0+ (modular layer architecture)._  See top-level
`README.md` and `doc/layer-architecture.md` for the broader rationale.

## 1. Overview

`meta-example` provides demonstration / showcase content, helper tools and
integration glue used by the canonical Example images (`kas-iot2050-example.yml`
and the SWUpdate variant). It focuses on discoverability and fast hardware
bring‑up rather than a production-minimal footprint.

## 2. Scope & Purpose

| Category | Purpose |
|----------|---------|
| Demo applications | Illustrate IO, networking, services configuration |
| Example implementations | Reference integration for downstream layers |
| Development helpers | Convenience utilities (debug, scripting) |
| Web interface components | Configuration / monitoring front-ends |
| Serial & board tools | Mode switching, board info, setup helpers |

## 3. Included Components (Illustrative)

The image recipe (`iot2050-image-example.bb`) pulls in a curated, evolving set
of packages via feature flags (see below). Examples:

| Functional Area | Examples (non-exhaustive – consult recipe for current list) |
|------------------|------------------------------------------------------------|
| GPIO / EIO | EIO manager, gpio tools |
| Web UI | Configuration / dashboard interface |
| Serial utilities | RS232/RS485 mode tools |
| Monitoring | Basic system / service status helpers |
| Developer aids | Selected Python / Node.js tools (trimmed for size) |

> Exact package list may change; rely on feature flags & fragments instead of
> hard-coding package names downstream.

## 4. Feature Flags Integration

`conf/include/iot2050-features.inc` centralizes soft switches consumed by image
recipes. Relevant to this layer:

```
# (Excerpt – see file for full list / defaults)
IOT2050_NODE_RED_SUPPORT ?= "0"
IOT2050_SM_SUPPORT       ?= "0"
IOT2050_HAILO_SUPPORT    ?= "0"
```

The example image descriptors (non-minimal) set these to enable bundled
content. When starting from the minimal base descriptor you add the opt
fragments instead:

```
./kas-container build \
	kas/iot2050.yml:kas/opt/example.yml:kas/opt/node-red.yml:kas/opt/sm.yml
```

## 5. How to Use This Layer

Fast path (already includes this layer + demos, Node-RED, SM):
```
./kas-container build kas-iot2050-example.yml
```

SWUpdate A/B variant (dual rootfs + .swu output):
```
./kas-container build kas-iot2050-swupdate.yml
```

From a lean minimal base, opt‑in only to this layer’s demos:
```
./kas-container build kas/iot2050.yml:kas/opt/example.yml
```

Add Node-RED & SM (feature parity with the full example path):
```
./kas-container build \
	kas/iot2050.yml:kas/opt/example.yml:kas/opt/node-red.yml:kas/opt/sm.yml
```

## 6. Customization & Extensibility

Two common paths—start broad to explore, or start lean for control.

Path A: Fast evaluation
1. Build the reference Example image: `kas-iot2050-example.yml` (includes this layer, Node-RED, SM, helper tooling).
2. Optionally test A/B + SWUpdate: `kas-iot2050-swupdate.yml`.
3. Make a list of what you actually used/need.

Path B: Controlled minimal start
1. Build the minimal base: `kas/iot2050.yml`.
2. Add only required opt fragments (temporarily `example.yml` for demos, plus `node-red.yml`, `sm.yml`, etc.).
3. Create a downstream layer (e.g. `meta-yourprod`) with an image recipe deriving from the minimal one.
4. Introduce feature flags following the pattern and collect them in an include:
   ```
   IOT2050_<FEATURE>_SUPPORT ?= "0"
   ```
5. After that: do what you need—prune demos, and add reproducibility / security fragments only when they become relevant.

## 7. Security & Production Note

Demo keys (secure boot) and provisioning logic do NOT live in this layer.
Before productization:
- Replace any demo certificates/keys (see `doc/secure-boot.md`).
- Remove unneeded developer utilities introduced here.
- Rebuild with reproducibility fragments (`package-lock.yml`, optional
	`debian-mirror.yml`) for audit trails.

## 8. Related Documentation

- Top-level composition & fragments: `doc/build-config.md`
- Layer architecture & migration: `doc/layer-architecture.md`
- Secure boot & provisioning: `doc/secure-boot.md`
- SWUpdate flow: `doc/swupdate.md`

## 9. Maintainers

See top-level `MAINTAINERS` file in the repository root.

## 10. License

MIT License – See `COPYING.MIT` in repository root.