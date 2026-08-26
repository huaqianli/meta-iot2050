# IOT2050 Cockpit Integration Architecture

This document defines the architecture and extension rules for IOT2050-specific
Cockpit integrations. It is intentionally smaller and more stable than the
feature-specific documentation. New Cockpit pages and integrations should
follow these boundaries unless a design change is explicitly documented.

## Goals

The IOT2050 Cockpit integration should:

- keep each feature independently installable and reviewable;
- use Cockpit's standard package and manifest model;
- keep privileged operations behind a small, fixed backend boundary;
- avoid adding unnecessary public HTTP or nginx APIs;
- provide a consistent navigation, theme, error, and task experience;
- allow new hardware and application integrations without merging unrelated
  frontends or backends into one package.

## Current integration map

The current self-developed integrations are separate Cockpit packages:

| Feature | Layer and package | Cockpit ID | Navigation | Availability | Backend boundary |
| --- | --- | --- | --- | --- | --- |
| Firmware | `meta-example`, `iot2050-cockpit-firmware` | `iot2050-firmware` | System | All supported images | `iot2050-fwmgr` and its systemd task workers |
| EIO Config | `meta-sm`, `iot2050-cockpit-eio-config` | `iot2050-eio-config` | System | SM board condition | EIO configuration bridge on the loopback interface |
| Device Admin | `meta-example`, `iot2050-cockpit-device-admin` | `iot2050-device-admin` | System | All supported images | Fixed-operation `iot2050-device-admin` helper |

The corresponding implementation and feature documentation are:

- [Firmware Center](firmware-center.md)
- [Device Admin](iot2050-device-admin.md)
- [EIO Config Cockpit README](../meta-sm/recipes-app/iot2050-cockpit-eio-config/README.md)
- [Web UI recipe overview](recipes-webui.md)

The current navigation is intentionally flat within Cockpit's standard
sections:

```text
System
├── Firmware
├── EIO Config       (SM only)
└── Device Admin
```

The plugins use the standard `menu` manifest section. `System` is the default
section for system configuration, hardware management, and device maintenance.
`Tools` is reserved for auxiliary, diagnostic, or application-oriented tools.
A custom Cockpit shell patch is not required for the current number of pages.

## Navigation rules

Use Cockpit's standard manifest sections rather than introducing a custom
sidebar category:

- `menu`: system and hardware administration;
- `tools`: diagnostics, auxiliary utilities, and independent applications;
- `dashboard`: dashboard items that belong on the Cockpit dashboard.

Choose the section based on the operation's purpose, not its recipe layer.
Examples:

- Firmware updates belong in `System` because they change the device boot
  chain and may require a reboot.
- EIO configuration belongs in `System` because it changes hardware/module
  configuration.
- Device Admin belongs in `System` because it manages the HTTPS entrypoint and
  device compliance material.

Use explicit `order` values to keep related IOT2050 entries together. Keep a
small reserved range for project-owned entries in each section and avoid
assuming that a new feature requires a new top-level category. If the number
of IOT2050 entries grows substantially, add a landing page only after
reviewing the navigation cost and Cockpit compatibility impact.

## Package and identity rules

Every integration has four identities that must be considered separately:

1. Debian/Isar package name;
2. Cockpit package directory under `/usr/share/cockpit/`;
3. manifest `name`;
4. user-visible menu label.

The package name, Cockpit directory, and manifest `name` should be stable once
the feature is released. The menu label may be improved independently, but a
rename must update the manifest, page title, documentation, image installation,
and all references together.

Use a feature-specific name for new packages, for example:

```text
iot2050-cockpit-<feature>
iot2050-cockpit-<feature>-integration
```

Do not use a generic package name such as `iot2050-system-webui` for unrelated
features. Do not reuse a Cockpit ID for a different feature.

## Backend and privilege boundaries

A Cockpit page is not a general root shell. Every privileged operation must use
a deliberately limited backend interface.

### Read-only operations

Read-only status and inspection may use an existing authenticated Cockpit
API, a fixed local client, or a narrowly scoped loopback service. The interface
must still validate requests and avoid arbitrary paths or commands.

### Hardware-changing operations

Operations that write hardware or device configuration must use a dedicated
manager or fixed-operation service boundary:

```text
Cockpit page
    -> authenticated Cockpit client
    -> fixed local client or root-only Unix socket
    -> fwmgr/provider
    -> hardware or system service
```

Examples:

- Firmware uses fwmgr and its persistent task model.
- EIO Config uses its existing configuration bridge.
- Device Admin uses fixed certificate-installation operations.
- The Web Gateway exposes the image-bundled OSS Clearing archive through the
  fixed `/oss` URL.

Do not add a new public nginx route merely to connect a Cockpit page to a
privileged operation. Do not pass arbitrary command names, filesystem paths, or
service names from the browser to a root helper.

## Feature integration contract

Each new Cockpit feature should provide the following:

- a dedicated recipe and package boundary;
- a manifest with a unique `name`, clear label, keywords, and explicit
  availability conditions where needed;
- a page directory with local static assets and a clear entrypoint;
- a fixed backend boundary for privileged operations;
- a short feature document describing user-visible behavior and operational
  requirements;
- image integration only in the image variants that support the feature.

SM-specific features must not be installed or shown on non-SM images. Use
manifest conditions and backend capability checks as separate safeguards; a
frontend-only condition is not a sufficient security boundary.

## Common UI rules

Self-developed pages should follow the existing Cockpit shell conventions:

- support light and dark Cockpit themes;
- use a concise, user-facing menu label and page title;
- show loading, empty, success, and error states explicitly;
- show permission and unavailable-feature errors explicitly;
- confirm destructive operations before changing hardware or security state;
- explain reboot, reload, or service-disruption consequences;
- never display private keys, credentials, or other secrets after upload;
- keep long-running hardware operations observable and recoverable after a
  browser reconnects;
- define how page state is refreshed after an operation or reconnect;
- remain usable at narrow viewport sizes;
- avoid duplicating branding owned by the Cockpit shell.

A page may use a different frontend framework when justified, but its package
boundary, navigation contract, privilege model, and operational behavior must
remain consistent with this document.

## Testing and documentation

For each new integration, validate at least:

- manifest JSON and static asset references;
- package installation and image inclusion/exclusion;
- availability conditions on supported and unsupported variants;
- backend request validation and privilege-boundary behavior;
- failure, cancellation, and reconnect behavior for long-running operations;
- user-visible navigation and theme behavior;
- basic page loading and static asset references;
- mock/provider coverage for hardware-facing features.

## Change management

When an integration needs to violate one of these rules, document the reason
and the resulting security, packaging, navigation, and maintenance impact in
its design or feature documentation before implementation.
