#
# Node-RED IoT2050 Configuration
# Extends base Node-RED with IoT2050 specific nodes and configuration
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# IoT2050 specific Node-RED nodes
RDEPENDS:${PN} += "\
    node-red-contrib-opcua \
    node-red-contrib-modbus \
    node-red-contrib-s7 \
    node-red-dashboard \
    node-red-gpio \
"

# Configuration files
SRC_URI += "\
    file://settings.js \
    file://flows.json \
"

do_install:append() {
    install -d ${D}${sysconfdir}/node-red
    install -m 0644 ${WORKDIR}/settings.js ${D}${sysconfdir}/node-red/
    install -m 0644 ${WORKDIR}/flows.json ${D}${sysconfdir}/node-red/
}