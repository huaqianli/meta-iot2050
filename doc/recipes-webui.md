# Web UI recipe implementation notes

This document is a developer-oriented map of the Web UI recipes in
`meta-example/recipes-webui`. User-facing instructions belong in the product
pages, not in this implementation overview.

## Package boundaries

| Recipe | Responsibility |
| --- | --- |
| `iot2050-firstboot-onboarding` | First-boot account and hostname setup |
| `iot2050-web-gateway-nginx` | Public HTTPS gateway and mode selection |
| `iot2050-cockpit-firmware` | Firmware Center Cockpit page |
| `iot2050-cockpit-device-admin` | Device administration Cockpit page |

SM-specific EIO configuration is provided by
`meta-sm/recipes-app/iot2050-cockpit-eio-config`.

Each feature has an independent package directory, manifest identity, and
privileged backend boundary. Image recipes select packages explicitly for the
supported image variants.

## Gateway model

nginx is the only public web entrypoint. HTTP redirects to HTTPS. Cockpit and
the onboarding backend listen on loopback interfaces, and the gateway selects
which backend is active through a mode symlink.

The gateway package owns the TLS listener, local certificate preparation,
proxy headers, websocket forwarding, and the image-bundled `/oss` download.
The certificate helper uses the existing OpenSSL system interface. The
onboarding service owns setup state and switches the gateway to runtime mode
only after Cockpit is ready. If the onboarding service is not installed, the
gateway selects the Cockpit runtime directly.

## Privilege model

Cockpit pages use fixed commands or root-only local services. Browser input is
validated at the backend boundary; arbitrary command names, filesystem paths,
and systemd unit names must not be passed to root helpers.

Long-running firmware writes use the fwmgr task and systemd worker model.
Firmware transport details are documented in
[Firmware Center implementation notes](firmware-center.md).

## Adding a feature

1. Create a dedicated recipe and package boundary.
2. Give the Cockpit manifest a stable, unique identity.
3. Keep privileged work in a fixed helper or root-only local service.
4. Add availability checks for variant-specific hardware.
5. Put concise user guidance and risk warnings in the page UI.
6. Update image package selection only for supported variants.

Keep this file focused on package boundaries and stable integration rules.
