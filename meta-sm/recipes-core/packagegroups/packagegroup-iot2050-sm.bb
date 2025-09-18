#
# IoT2050-SM Package Group
# SM variant specific packages
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "IoT2050-SM variant specific packages"
DESCRIPTION = "Package group containing SM variant specific components"

inherit packagegroup

PACKAGES = "\
    ${PN} \
    ${PN}-firmware \
    ${PN}-apps \
    ${PN}-eio \
    ${PN}-module-fwu \
"

RDEPENDS:${PN} = "\
    ${PN}-apps \
"

RDEPENDS:${PN}-firmware = "\
    # Add SM-specific firmware packages here \
"

RDEPENDS:${PN}-apps = "\
    iot2050-event-record \
    iot2050-proximity-driver \
"

# EIO management packages - controlled by IOT2050_EIO_SUPPORT
RDEPENDS:${PN}-eio = "\
    iot2050-eio-manager \
    iot2050-conf-webui \
"

# Module firmware update - controlled by IOT2050_MODULE_FWU  
RDEPENDS:${PN}-module-fwu = "\
    iot2050-module-firmware-update \
"