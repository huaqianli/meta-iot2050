# Copyright (c) Siemens AG, 2026
# SPDX-License-Identifier: MIT

PR = "1"

DESCRIPTION = "IOT2050 firmware backend"
MAINTAINER = "Siemens AG"

inherit dpkg-raw

DEPENDS = "iot2050-firmware-update"

SRC_URI = " \
    file://iot2050_firmware_manager.py \
    file://iot2050-fwmgr \
    file://iot2050-firmware-task@.service \
    file://iot2050-firmware-staging-gc.service \
    file://iot2050-firmware-staging-gc.timer \
    file://prerm \
    file://postinst \
    "

DEBIAN_DEPENDS = "python3, systemd, iot2050-firmware-update"

do_install() {
    install -v -d ${D}/usr/lib/python3/dist-packages/
    install -v -m 644 ${WORKDIR}/iot2050_firmware_manager.py \
        ${D}/usr/lib/python3/dist-packages/

    install -v -d ${D}/usr/sbin/
    install -v -m 755 ${WORKDIR}/iot2050-fwmgr ${D}/usr/sbin/

    install -v -d ${D}/usr/lib/systemd/system/
    install -v -m 644 ${WORKDIR}/iot2050-firmware-task@.service \
        ${D}/usr/lib/systemd/system/
    install -v -m 644 ${WORKDIR}/iot2050-firmware-staging-gc.service \
        ${D}/usr/lib/systemd/system/
    install -v -m 644 ${WORKDIR}/iot2050-firmware-staging-gc.timer \
        ${D}/usr/lib/systemd/system/

}
