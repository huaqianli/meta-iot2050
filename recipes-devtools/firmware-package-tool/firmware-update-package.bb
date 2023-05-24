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

do_deploy_deb[nostamp] = "1"

do_deploy_deb() {
    # Generate the firmware update package
    sh ${WORKDIR}/iot2050-fwu-update-json.sh ${WORKDIR} ${DEPLOY_DIR_IMAGE} $(${ISAR_RELEASE_CMD})
}
do_deploy_deb[dirs] = "${DEPLOY_DIR_IMAGE}"
