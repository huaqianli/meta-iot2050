#
# Common package group class for IoT2050
# Provides standard patterns for component organization
#
# Copyright (c) Siemens AG, 2025
#
# SPDX-License-Identifier: MIT
#

inherit packagegroup

# Standard header template for IoT2050 packagegroups
IOT2050_PACKAGEGROUP_HEADER = "\
#\n\
# ${SUMMARY}\n\
# ${DESCRIPTION}\n\
#\n\
# Copyright (c) Siemens AG, 2025\n\
#\n\
# SPDX-License-Identifier: MIT\n\
#\n\
"

# Common pattern for main packagegroup dependency
def iot2050_packagegroup_rdepends(d, subpackages):
    """
    Generate RDEPENDS for main packagegroup from list of subpackages
    Usage: RDEPENDS:${PN} = "${@iot2050_packagegroup_rdepends(d, ['apps', 'tools'])}"
    """
    pn = d.getVar('PN')
    return ' \\\n    '.join([f"${{{pn}}}-{sub}" for sub in subpackages])

# Common package list generator
def iot2050_packagegroup_packages(d, subpackages):
    """
    Generate PACKAGES variable from list of subpackages
    Usage: PACKAGES = "${@iot2050_packagegroup_packages(d, ['apps', 'tools'])}"
    """
    pn = d.getVar('PN')
    base = [f"${{{pn}}}"]
    subs = [f"${{{pn}}}-{sub}" for sub in subpackages]
    return ' \\\n    '.join(base + subs)