#
# Copyright (c) Siemens AG, 2026
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

PR = "1"

inherit dpkg-raw

DESCRIPTION = "IOT2050 PAM configuration"

DEBIAN_DEPENDS = "cracklib-runtime, libpam-runtime, libpam-modules, libpam-pwquality, passwd, python3, systemd, wamerican"

IOT2050_LOCK_ROOT_PASSWORD ?= "1"

SRC_URI = " \
    file://etc/security/faillock.conf \
    file://etc/security/pwquality.conf \
    file://lock-root-password \
    file://pam-configs/iot2050-failed-login-lockout \
    file://pam-configs/iot2050-failed-login-preauth \
    file://pam-configs/iot2050-failed-login-success \
    file://pam-configs/iot2050-password-quality \
    file://postinst \
"

do_install() {
    install -d -m 755 ${D}/etc/security
    install -m 644 ${WORKDIR}/etc/security/faillock.conf ${D}/etc/security/
    install -m 644 ${WORKDIR}/etc/security/pwquality.conf ${D}/etc/security/
    install -d -m 755 ${D}/usr/share/pam-configs
    install -m 644 ${WORKDIR}/pam-configs/iot2050-failed-login-lockout ${D}/usr/share/pam-configs/
    install -m 644 ${WORKDIR}/pam-configs/iot2050-failed-login-preauth ${D}/usr/share/pam-configs/
    install -m 644 ${WORKDIR}/pam-configs/iot2050-failed-login-success ${D}/usr/share/pam-configs/
    install -m 644 ${WORKDIR}/pam-configs/iot2050-password-quality ${D}/usr/share/pam-configs/
    install -d -m 755 ${D}/usr/share/iot2050-pam-conf
    if [ "${IOT2050_LOCK_ROOT_PASSWORD}" = "1" ]; then
        install -m 644 ${WORKDIR}/lock-root-password ${D}/usr/share/iot2050-pam-conf/
    fi
}