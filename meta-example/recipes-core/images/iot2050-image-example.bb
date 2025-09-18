#
# Copyright (c) Siemens AG, 2019-2023
#
# Authors:
#  Su Baocheng <baocheng.su@siemens.com>
#
# This file is subject to the terms and conditions of the MIT License.  See
# COPYING.MIT file in the top-level directory.
#

require meta/recipes-core/images/iot2050-image-base.bb
require recipes-core/images/iot2050-package-selections.inc

DESCRIPTION = "IOT2050 Debian Example Image"

IMAGE_PREINSTALL += " \
    ${IOT2050_DEBIAN_DEBUG_PACKAGES} \
    ${IOT2050_DEBIAN_WIFI_PACKAGES} \
    ${IOT2050_DEBIAN_BT_PACKAGES} \
    ${IOT2050_DEBIAN_ALSA_PACKAGES} \
    ${IOT2050_DEBIAN_MULTIARCH_PACKAGES} \
    "

IOT2050_DOCKER_SUPPORT ?= "0"

IMAGE_PREINSTALL += "${@ ' \
    ${IOT2050_DEBIAN_DOCKER_PACKAGES} \
    ' if d.getVar('IOT2050_DOCKER_SUPPORT') == '1' else ''}"

IMAGE_INSTALL += " \
    expand-on-first-boot \
    sshd-regen-keys \
    regen-rootfs-uuid \
    install-on-emmc \
    iot2050-status-led \
    iot2050-nm-settings \
    nodejs-module-path \
    ssh-root-login \
    change-root-homedir \
    iot2050-switchserialmode \
    iot2050-firmware-update \
    ${@ 'firmware-update-package' if d.getVar('QEMU_IMAGE') != '1' else '' } \
    tcf-agent \
    mraa \
    ${@ 'board-conf-tools' if d.getVar('QEMU_IMAGE') != '1' else '' } \
    packagegroup-iot2050-security \
    linux-headers-${KERNEL_NAME} \
    iot2050-proximity-driver \
    "

IMAGE_INSTALL += "${@ 'packagegroup-iot2050-node-red' if d.getVar('IOT2050_NODE_RED_SUPPORT') == '1' else ''}"

# SM variant packages - include when meta-sm layer is available
IMAGE_INSTALL += "${@ 'packagegroup-iot2050-sm' if bb.utils.contains('BBFILE_COLLECTIONS', 'meta-sm', True, False, d) else ''}"

IOT2050_EIO_SUPPORT ?= "0"

IMAGE_INSTALL += "${@ 'packagegroup-iot2050-sm-eio' if d.getVar('IOT2050_EIO_SUPPORT') == '1' else ''}"

IOT2050_MODULE_FWU ?= "0"
IMAGE_INSTALL += "${@ 'packagegroup-iot2050-sm-module-fwu' if d.getVar('IOT2050_MODULE_FWU') == '1' else '' }"

IOT2050_META_HAILO ?= "0"
IMAGE_INSTALL += "${@ ' \
    hailo-pci-${KERNEL_NAME} \
    hailo-firmware \
    hailortcli \
    libhailort \
    libhailort-dev \
    libgsthailo \
    libgsthailo-dev \
    python3-hailort \
    hailort \
    libgsthailotools \
    tappas-apps \
    hailo-post-processes \
    tappas-tracers \
    ' if d.getVar('IOT2050_META_HAILO') == '1' else '' }"
