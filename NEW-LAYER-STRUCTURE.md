# IoT2050 Layer Organization

This repository contains a modular layer organization for the Siemens IoT2050 platform.

## Layer Structure

### `meta/` - Core BSP Layer
- **Purpose**: Hardware enablement only
- **Contains**: U-Boot, kernel, drivers, minimal system
- **Target**: `iot2050-base-image`

### `meta-example/` - Demo Layer
- **Purpose**: Demo applications and examples
- **Contains**: Example applications, web UI, demo components
- **Target**: `iot2050-image-example`

### `meta-connectivity/` - Industrial Protocols
- **Purpose**: Industrial IoT connectivity
- **Contains**: OPC-UA, Modbus, MQTT, S7 protocols
- **Target**: `iot2050-connectivity-image`

### `meta-node-red/` - Node-RED Support
- **Purpose**: Node-RED ecosystem
- **Contains**: Node-RED runtime, industrial nodes, dashboard

### `meta-hailo/` - AI Accelerator
- **Purpose**: Hailo AI support
- **Contains**: Hailo firmware, drivers, AI examples

### `meta-iot2050-sm/` - Board Variant
- **Purpose**: IoT2050-SM specific features
- **Contains**: SM-specific machine config, applications

## Build Configurations

### Minimal BSP
```bash
kas build kas/iot2050-base.yml
```

### Full Demo
```bash
kas build kas/iot2050-example.yml
```

### Industrial Connectivity
```bash
kas build kas/iot2050-connectivity.yml
```

### AI Development
```bash
kas build kas/iot2050-hailo.yml
```

### Board Variant
```bash
kas build kas/iot2050-sm.yml
```

### Development Tools
```bash
kas build kas/iot2050-development.yml
```

## Migration from Old Structure

The old monolithic structure is preserved during transition. New layer structure provides:

- **Modular design** - pick only needed features
- **Clear separation** - BSP vs applications
- **Scalable architecture** - easy to extend
- **Industry standard** - follows Yocto best practices