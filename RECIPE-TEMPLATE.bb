#
# Template for IoT2050 Applications
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Your Name <your.name@siemens.com>
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Brief description of the application"
DESCRIPTION = "Detailed description of what this application does"
HOMEPAGE = "https://siemens.com/iot2050"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=..."

SRC_URI = "file://application-source"

S = "${WORKDIR}/application-source"

inherit systemd

SYSTEMD_SERVICE:${PN} = "your-app.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/your-app ${D}${bindir}/
    
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/your-app.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/your-app.service \
"

RDEPENDS:${PN} += "systemd"