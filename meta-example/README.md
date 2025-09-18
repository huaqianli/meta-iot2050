# meta-example - IoT2050 Demo and Example Layer

## Overview

The `meta-example` layer provides demo applications, examples, and showcase components for the IoT2050 platform. This layer demonstrates the capabilities of the IoT2050 hardware.

## Purpose

- **Demo applications** - showcase IoT2050 platform capabilities
- **Example implementations** - reference code for developers
- **Development tools** - utilities for application development
- **Web interfaces** - configuration and monitoring tools

## Contents

### Applications
- EIO Manager - GPIO and I/O management
- Configuration WebUI - web-based system configuration
- Serial mode switching utilities
- Board configuration tools

### Images
- `iot2050-image-example` - Full-featured demo image with all examples

### Package Groups
- `packagegroup-iot2050-example` - All demo and example packages

## Key Features

- 🎯 **GPIO Control** - Direct hardware I/O management
- 🌐 **Web Interface** - Browser-based configuration
- 🔧 **Development Tools** - Python, Node.js, debugging utilities
- 📊 **Monitoring** - System health and performance tools
- 🔌 **Serial Interface** - RS232/RS485 configuration

## Target Audience

- **Developers** exploring IoT2050 capabilities
- **System integrators** evaluating the platform
- **Educational users** learning industrial IoT concepts
- **Proof-of-concept** implementations

## Dependencies

- **meta-iot2050-bsp** - Base hardware support
- **meta-node-red** - Often used together for complete demos

## Build

```bash
kas build kas/iot2050-example.yml
```

## Examples Included

1. **GPIO Examples** - LED control, digital I/O
2. **Serial Communication** - RS232/RS485 examples
3. **Network Configuration** - Ethernet setup examples
4. **Web Dashboard** - Real-time system monitoring

## Maintainers

See MAINTAINERS file in this layer.

## License

MIT License - See COPYING.MIT in repository root.