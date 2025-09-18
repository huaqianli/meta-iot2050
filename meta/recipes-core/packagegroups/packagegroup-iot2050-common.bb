#
# Common package definitions for IoT2050
# Base packages that might be useful across multiple layers
#
# Copyright (c) Siemens AG, 2025
#
# SPDX-License-Identifier: MIT
#

SUMMARY = "Common packages for IoT2050"
DESCRIPTION = "Package group containing common packages that may be useful across layers"

inherit packagegroup

PACKAGES = "\
    ${PN} \
    ${PN}-utils \
"

RDEPENDS:${PN} = "\
    ${PN}-utils \
"

# Common system utilities that are typically available
RDEPENDS:${PN}-utils = ""

# This packagegroup is intentionally minimal
# Individual layers should define their own specific packages