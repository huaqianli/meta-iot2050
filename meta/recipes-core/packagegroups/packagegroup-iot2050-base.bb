#
# IoT2050 Base Package Group
# Essential packages for hardware enablement
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "IoT2050 base hardware enablement packages"
DESCRIPTION = "Package group containing essential packages for IoT2050 hardware support"

inherit packagegroup

PACKAGES = "\
    ${PN} \
    ${PN}-kernel \
    ${PN}-bootloader \
    ${PN}-firmware \
"

RDEPENDS:${PN} = "\
    ${PN}-kernel \
    ${PN}-bootloader \
    ${PN}-firmware \
"

RDEPENDS:${PN}-kernel = "\
    linux-iot2050 \
"

RDEPENDS:${PN}-bootloader = "\
    u-boot-iot2050 \
    trusted-firmware-a-iot2050 \
"

RDEPENDS:${PN}-firmware = "\
    ti-pruss-firmware \
    k3-rti-wdt \
"