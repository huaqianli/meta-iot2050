#
# Copyright (c) Siemens AG, 2023
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# This file is subject to the terms and conditions of the MIT License.  See
# COPYING.MIT file in the top-level directory.
#
DESCRIPTION = "Firmware Package Tool"
MAINTAINER = "huaqian.li@siemens.com"

SRC_URI = "file://update.conf.json \
		   file://iot2050-fwu-update-json.sh"

inherit dpkg-raw

DEPENDS = "u-boot-iot2050-pg1"
# DEPENDS = "u-boot-iot2050-pg1 u-boot-iot2050-pg2"

do_deploy_deb[nostamp] = "1"

do_deploy_deb() {
    echo "I am deploying!"
	sh ${WORKDIR}/iot2050-fwu-update-json.sh ${WORKDIR} ${DEPLOY_DIR_IMAGE}
}
do_deploy_deb[dirs] = "${DEPLOY_DIR_IMAGE}"
