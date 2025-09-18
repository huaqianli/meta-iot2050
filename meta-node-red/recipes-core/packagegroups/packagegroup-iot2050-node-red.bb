#
# SPDX-License-Identifier: MIT
# Copyright (c) 2021-2025 Siemens AG
#
# Authors:
#  Jan Kiszka <jan.kiszka@siemens.com>
#

SUMMARY = "Node-RED runtime and modules for IoT2050"
PR = "r1"

inherit packagegroup

RDEPENDS:${PN} = " \
    node-red \
    node-red-gpio \
    node-red-preinstalled-nodes \
    nodejs-module-path \
    "

# Optional packages that can be included based on configuration
RDEPENDS:${PN}-full = " \
    ${RDEPENDS:${PN}} \
    node-red-dashboard \
    node-red-contrib-opcua \
    node-red-contrib-modbus \
    node-red-contrib-s7 \
    node-red-node-serialport \
    node-red-node-sqlite \
    node-red-node-random \
    mindconnect-node-red-contrib-mindconnect \
    "

PACKAGES = "${PN} ${PN}-full"