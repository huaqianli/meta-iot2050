#
# Copyright (c) Siemens AG, 2023
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# This file is subject to the terms and conditions of the MIT License.  See
# COPYING.MIT file in the top-level directory.
#
DESCRIPTION = "Generate The Firmware Update Package"
MAINTAINER = "huaqian.li@siemens.com"

SRC_URI = "file://update.conf.json \
		   file://iot2050-fwu-update-json.sh"

inherit dpkg-raw
# inherit image

PROVIDES = "u-boot-iot2050-pg1 u-boot-iot2050-pg2"
# DEPENDS = "u-boot-iot2050-pg1"

do_deploy_deb[nostamp] = "1"

do_deploy_deb() {
    echo "get_build_id or bb.build.exec_func('get_build_id', d)"
    # Generate the firmware update package
	sh ${WORKDIR}/iot2050-fwu-update-json.sh ${WORKDIR} ${DEPLOY_DIR_IMAGE} get_build_id
}
do_deploy_deb[dirs] = "${DEPLOY_DIR_IMAGE}"
