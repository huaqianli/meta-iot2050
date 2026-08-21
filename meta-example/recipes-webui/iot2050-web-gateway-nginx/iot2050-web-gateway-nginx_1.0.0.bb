#
# Copyright (c) Siemens AG, 2026
#
# Authors:
#  Zhao Chun Jiao <chunjiao.zhao@siemens.com>
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

PR = "1"

inherit dpkg-raw

def get_oss_archive_name(d):
    import os
    import re

    files_dir = os.path.join(d.getVar("THISDIR"), "files")
    pattern = re.compile(
        r"^V[0-9]+(?:\.[0-9]+)*-ReadmeOSS-All_in_One\.zip$"
    )
    archives = sorted(
        name for name in os.listdir(files_dir)
        if pattern.fullmatch(name)
    )
    if len(archives) != 1:
        bb.fatal(
            "Web Gateway recipe requires exactly one OSS Clearing archive "
            "matching V<version>-ReadmeOSS-All_in_One.zip; found: %s"
            % ", ".join(archives)
        )
    return archives[0]

OSS_ARCHIVE = "${@get_oss_archive_name(d)}"
TEMPLATE_FILES = "iot2050-web-gateway.conf.tmpl"
TEMPLATE_VARS += "OSS_ARCHIVE"

DESCRIPTION = "IOT2050 nginx web gateway for onboarding and Cockpit"

DEBIAN_DEPENDS = "cockpit, nginx, openssl, systemd, iot2050-firewall-default"

# Keep these two Cockpit overrides together: cockpit.service stays on loopback
# plain HTTP for nginx TLS termination, while cockpit-wsinstance-http is put
# into --for-tls-proxy mode so authenticated Cockpit traffic accepts the
# forwarded HTTPS origin. Reference: upstream cockpit-ws(8) and Cockpit
# test/verify/check-connection reverse-proxy tests.
SRC_URI = " \
    file://cockpit.socket.d/loopback.conf \
    file://cockpit.service.d/no-tls.conf \
    file://cockpit-wsinstance-http.service.d/for-tls-proxy.conf \
    file://nginx.service.d/iot2050-web-gateway.conf \
    file://iot2050-web-gateway.conf.tmpl \
    file://${OSS_ARCHIVE};unpack=0 \
    file://iot2050-web-gateway-proxy-common.conf \
    file://iot2050-web-gateway-onboarding.conf \
    file://iot2050-web-gateway-runtime.conf \
    file://iot2050-web-gateway-ensure-cert.sh \
    file://iot2050-web-gateway-prepare.sh \
    file://iot2050-web-gateway-select-mode \
    file://postinst \
    "

do_install() {
    install -d -m 755 ${D}/etc/nginx/conf.d
    install -m 644 ${WORKDIR}/iot2050-web-gateway.conf ${D}/etc/nginx/conf.d/
    install -m 644 ${WORKDIR}/iot2050-web-gateway-proxy-common.conf ${D}/etc/nginx/conf.d/

    install -d -m 755 ${D}/etc/systemd/system/cockpit.socket.d
    install -m 644 ${WORKDIR}/cockpit.socket.d/loopback.conf ${D}/etc/systemd/system/cockpit.socket.d/

    install -d -m 755 ${D}/etc/systemd/system/cockpit.service.d
    install -m 644 ${WORKDIR}/cockpit.service.d/no-tls.conf ${D}/etc/systemd/system/cockpit.service.d/

    install -d -m 755 ${D}/etc/systemd/system/cockpit-wsinstance-http.service.d
    install -m 644 ${WORKDIR}/cockpit-wsinstance-http.service.d/for-tls-proxy.conf ${D}/etc/systemd/system/cockpit-wsinstance-http.service.d/

    install -d -m 755 ${D}/etc/systemd/system/nginx.service.d
    install -m 644 ${WORKDIR}/nginx.service.d/iot2050-web-gateway.conf ${D}/etc/systemd/system/nginx.service.d/

    install -d -m 755 ${D}/usr/lib/iot2050/web-gateway/nginx
    install -m 755 ${WORKDIR}/iot2050-web-gateway-ensure-cert.sh ${D}/usr/lib/iot2050/web-gateway/
    install -m 755 ${WORKDIR}/iot2050-web-gateway-prepare.sh ${D}/usr/lib/iot2050/web-gateway/
    install -m 755 ${WORKDIR}/iot2050-web-gateway-select-mode ${D}/usr/lib/iot2050/web-gateway/
    install -m 644 ${WORKDIR}/iot2050-web-gateway-onboarding.conf ${D}/usr/lib/iot2050/web-gateway/nginx/onboarding.conf
    install -m 644 ${WORKDIR}/iot2050-web-gateway-runtime.conf ${D}/usr/lib/iot2050/web-gateway/nginx/runtime.conf

    install -d -m 755 ${D}/etc/nginx/conf.d/iot2050-web-gateway-mode
    ln -sfn /usr/lib/iot2050/web-gateway/nginx/onboarding.conf ${D}/etc/nginx/conf.d/iot2050-web-gateway-mode/current.conf

    install -d -m 755 ${D}/etc/iot2050/web-gateway

    # The release layer supplies exactly one versioned OSS Clearing ZIP in
    # files/. The stable /oss URL preserves this official filename.
    install -d -m 755 ${D}/usr/share/iot2050/oss-clearing
    install -m 644 ${WORKDIR}/${OSS_ARCHIVE} \
        ${D}/usr/share/iot2050/oss-clearing/
}
