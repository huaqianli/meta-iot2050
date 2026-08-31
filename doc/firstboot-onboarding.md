# First-boot onboarding implementation notes

This document records the implementation contract for developers and
maintainers. End-user guidance is shown locally through the onboarding page's
field hints, password rules, and error messages.

## Runtime flow

The Product example image starts the onboarding service only while its
completion marker is absent:

- nginx terminates HTTPS and proxies the onboarding mode to `127.0.0.1:9080`;
- the Node.js service serves the local page and its API;
- the Python helper applies the hostname and creates the first administrator;
- the service starts Cockpit and waits for its loopback login endpoint;
- nginx switches to runtime mode and the completion marker is written.

The marker is `/var/lib/iot2050-firstboot-onboarding/complete`. The systemd
unit uses `ConditionPathExists=!` and `StateDirectory` so the onboarding state
is persistent.

The Dev compatibility fragment does not install this service. In that profile,
the gateway selects the Cockpit runtime directly and the preconfigured
development accounts are used instead.

## API boundary

The onboarding service exposes only the local setup endpoints required by the
page:

- `GET /api/status` returns current setup status and hostname information;
- `POST /api/complete` validates the submitted account and hostname data;
- static assets and localized bundles are served from the package directory.

The service is loopback-only. nginx is the public HTTPS boundary and no
onboarding endpoint is exposed as a separate public port.

## Account and hostname rules

The helper rejects `root` as an onboarding username and validates the username
and hostname before applying changes. It creates a named account and adds only
administrative groups that exist on the image. Password acceptance is finally
decided by the system PAM policy; the page performs matching local checks to
provide immediate feedback.

`useradd`, `chpasswd`, `userdel`, `hostnamectl`, and `hostname` are retained as
system interfaces. They are invoked with argument vectors rather than shell
command strings. The helper does not print passwords or raw password-command
errors, and removes the newly created account if password setup fails.

## Security invariants

- The root account is not provisioned or unlocked by onboarding.
- The completion marker is written only after Cockpit is ready and nginx has
  switched to runtime mode.
- Passwords are accepted only through the protected request path and are not
  persisted in onboarding state.
- Failed account setup returns editable field errors so the operator can retry.
- Runtime Cockpit access remains behind nginx HTTPS and Cockpit authentication.

Keep page-specific instructions and recovery messages in the onboarding UI.
Keep this file limited to stable package, API, and security contracts.
