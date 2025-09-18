#
# IoT2050-SM Image Recipe
# Image for IoT2050-SM board variant
#
# Copyright (c) Siemens AG, 2025
#
# Authors:
#  Li Hua Qian <huaqian.li@siemens.com>
#
# SPDX-License-Identifier: MIT
#

require recipes-core/images/iot2050-base-image.bb

DESCRIPTION = "IoT2050-SM variant specific image"

IMAGE_PREINSTALL += " \
    packagegroup-iot2050-sm \
"

# SM-specific packages
IMAGE_INSTALL += " \
    # Add SM-specific packages here \
"