#
# IoT2050 Base Image Recipe
# Minimal bootable system with hardware support only
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/isar-image-base.bb

DESCRIPTION = "IoT2050 minimal base image with hardware enablement"

IMAGE_PREINSTALL += " \
    packagegroup-iot2050-base \
"

IMAGE_INSTALL += " \
    openssh-server \
    systemd \
    udev \
"

# Hardware-specific packages
IMAGE_INSTALL += " \
    u-boot-tools \
    firmware-misc-nonfree \
"

IMAGE_FSTYPES = "wic"
WKS_FILE = "iot2050-image.wks"