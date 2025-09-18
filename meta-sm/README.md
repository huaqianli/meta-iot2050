# meta-sm - IoT2050-SM Board Variant Layer

## Overview

The `meta-sm` layer provides support for the IoT2050-SM board variant of the IoT2050 platform. This layer contains SM-specific configurations and applications.

## Purpose

- **Board variant support** - IoT2050-SM specific features
- **Specialized configuration** - SM-specific machine config
- **Variant applications** - Applications specific to SM variant

## Contents

### Machine Configuration
- `conf/machine/iot2050-sm.conf` - SM-specific machine definition
- Device tree modifications for SM variant
- Bootloader configuration for SM

### Applications
- `recipes-app/iot2050-event-record/` - Event recording system
- `recipes-app/iot2050-eio-manager/` - EIO management daemon
- `recipes-app/iot2050-conf-webui/` - Configuration web interface
- `recipes-app/iot2050-module-firmware-update/` - Module firmware update tools

### Images
- `iot2050-sm-image` - Image tailored for SM variant

## Key Features

- 🔒 **Enhanced Security** - Additional security module support
- ⚙️ **SM Configuration** - Specialized machine configuration
- 🛠️ **Variant Tools** - SM-specific management utilities
- 🔧 **Hardware Support** - SM-specific hardware enablement

## Hardware Differences

| Feature | Standard IoT2050 | IoT2050-SM |
|---------|------------------|------------|
| Security Module | No | Yes |
| Boot Configuration | Standard | Enhanced |
| Device Tree | Standard | SM-specific |
| Applications | General | SM-optimized |

## SM-Specific Features

1. **Security Module** - Hardware security enhancements
2. **Secure Boot** - Enhanced boot security
3. **Key Management** - Cryptographic key handling
4. **Attestation** - Hardware attestation capabilities

## Machine Configuration

The SM variant uses a specialized machine configuration that:
- Extends the base IoT2050 configuration
- Adds SM-specific device tree
- Configures SM-specific bootloader settings
- Enables SM hardware features

## Included Packages

| Package | Purpose |
|---------|---------|
| iot2050-event-record | System event recording and monitoring |
| iot2050-eio-manager | EIO (External I/O) management daemon |
| iot2050-conf-webui | Web-based configuration interface |
| iot2050-module-firmware-update | Firmware update tools for modules |

### Package Groups

- `packagegroup-iot2050-sm` - Core SM packages (event-record)
- `packagegroup-iot2050-sm-eio` - EIO management (enabled by IOT2050_EIO_SUPPORT=1)
- `packagegroup-iot2050-sm-module-fwu` - Module firmware update (enabled by IOT2050_MODULE_FWU=1)

## Dependencies

- **meta-iot2050-bsp** - Base hardware support (extended)
- **Security libraries** - Cryptographic and security components

## Build

```bash
kas build kas/iot2050-sm.yml
```

## Target Machine

Set machine to IoT2050-SM variant:
```bash
MACHINE = "iot2050-sm"
```

## Configuration Notes

### Device Tree
- Uses SM-specific device tree blob
- Includes security module definitions
- Enhanced hardware configuration

### Bootloader
- SM-specific U-Boot configuration
- Enhanced security features
- Secure boot support

## Development

### SM-Specific Applications
Applications in this layer should:
- Utilize SM security features
- Be optimized for SM variant
- Follow security best practices

### Testing
- Test on actual IoT2050-SM hardware
- Verify security functionality
- Validate SM-specific features

## Security Considerations

- Follow security best practices
- Use secure communication protocols
- Implement proper key management
- Regular security updates

## Maintainers

See MAINTAINERS file in this layer.

## License

MIT License - See COPYING.MIT in repository root.