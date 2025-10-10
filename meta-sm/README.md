# meta-sm – IoT2050 SM Variant Layer

_Applies to meta-iot2050 v1.6.0+ modular layout._  

This layer supplies the optional SM variant user-space components and device
tree for boards equipped with the additional peripherals.

## 1. Overview

`meta-sm` is additive: it does NOT redefine a separate machine by default.
Instead, the canonical Example and SWUpdate image descriptors include its
content implicitly. When using the minimal base descriptor you opt in via the
`sm.yml` fragment (`IOT2050_SM_SUPPORT=1`).

## 2. Scope & Purpose

| Category | Scope |
|----------|-------|
| Sensors & IO mgmt | EIO manager, proximity / event utilities |
| Variant services | Event recording, configuration web UI integration |
| Firmware helper  | Module firmware update helper (signal module) |
| Device tree      | SM-specific DTB addition(s) |

## 3. Build

Enabled by either:
```
# Example image
./kas-container build kas-iot2050-example.yml

# Minimal path + SM
./kas-container build kas/iot2050.yml:kas/opt/sm.yml
```
The full example & SWUpdate images already include SM support—no fragment is
needed there.

## 4. Packages Provided

Declared in `meta-sm/recipes-core/images/meta-sm-packages.inc`:
```
IOT2050_META_SM_PACKAGES ?= " \
		iot2050-proximity-driver \
		iot2050-eio-manager \
		iot2050-event-record \
		iot2050-conf-webui \
		iot2050-module-firmware-update \
		"
```
Image recipes append this list only when `IOT2050_SM_SUPPORT = "1"`.

## 5. Device Tree Integration

The layer appends the SM-specific DTB(s):
```
ti/k3-am6548-iot2050-advanced-sm.dtb
```
Selection occurs via bootloader logic choosing the correct DTB for the board
revision (no separate MACHINE value required). Downstreams needing overrides
should place bbappend files in a higher-priority layer.

## 6. Related Documentation

- Composition & fragments: `doc/build-config.md`
- Architecture rationale: `doc/layer-architecture.md`
- Secure boot & provisioning: `doc/secure-boot.md`
- Example layer (demos): `meta-example/README.md`

## 7.  Maintainers

See top-level `MAINTAINERS` file in the repository root.

## 8. License

MIT License – See `COPYING.MIT` (repository root).