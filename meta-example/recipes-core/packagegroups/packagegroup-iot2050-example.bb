#
# IoT2050 Example Package Group
# Demo applications and example components
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Example applications package group for IOT2050"
DESCRIPTION = "Package group containing example applications and tools for IOT2050"

inherit packagegroup

PACKAGES = "\
    packagegroup-iot2050-example \
    ${PN}-apps \
    ${PN}-tools \
"

RDEPENDS:packagegroup-iot2050-example = "\
    ${PN}-apps \
    ${PN}-tools \
"

# Example applications
RDEPENDS:${PN}-apps = "\
    board-conf-tools \
    iot2050-efivarfs-helper \
    iot2050-firmware-update \
    mraa \
    tcf-agent \
"

# Development and system tools
RDEPENDS:${PN}-tools = "\
    switchserialmode \
    install-on-emmc \
    iot2050-watchdog \
    firmware-update-package \
    patch-u-boot-env \
"# Remove empty web section - no web packages were actually moved
# RDEPENDS:${PN}-web = "\
#     # Add web interface packages here \
#     # iot2050-conf-webui \
# "