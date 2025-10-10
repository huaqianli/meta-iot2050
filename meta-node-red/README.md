# meta-node-red - IoT2050 Node-RED Layer

## Overview

The `meta-node-red` layer provides Node-RED support for the IoT2050 platform. Node-RED is a flow-based development tool for visual programming of IoT applications.

## Pre-installed Nodes

| Node Package | Purpose |
|--------------|---------|
| node-red | Core Node-RED runtime |
| node-red-contrib-opcua | OPC-UA communication |
| node-red-contrib-modbus | Modbus protocol support |
| node-red-contrib-s7 | Siemens S7 PLC integration |
| node-red-dashboard | Web dashboard creation |
| node-red-gpio | Hardware GPIO control |
| node-red-node-serialport | Serial port communication |
| node-red-node-sqlite | SQLite database nodes |
| node-red-node-random | Random number generation |
| mindconnect-node-red-contrib-mindconnect | MindConnect IoT platform |

## Getting Started

### Build
```bash
./kas-container build kas/iot2050-example.yml  # Includes Node-RED
```
### Access Node-RED
1. Browse to `http://<IOT2050_IP>:1880` (default Node-RED port 1880)
2. Start creating flows by dragging nodes
3. Deploy flows to activate them


## Maintainers

See top-level `MAINTAINERS` file in the repository root.

## License

MIT License - See COPYING.MIT in repository root.