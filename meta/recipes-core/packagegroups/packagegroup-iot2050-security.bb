#
# SPDX-License-Identifier: MIT
# Copyright (c) 2021-2025 Siemens AG
#
# Authors:
#  Jan Kiszka <jan.kiszka@siemens.com>
#

SUMMARY = "Security components for IoT2050"
PR = "r1"

inherit packagegroup

# Base security components - OP-TEE and Trusted Firmware
RDEPENDS:${PN}-base = " \
    optee-os-iot2050 \
    optee-client-iot2050 \
    trusted-firmware-a-iot2050 \
    "

# Secure boot components - key provisioning and signing
RDEPENDS:${PN}-secureboot = " \
    ${RDEPENDS:${PN}-base} \
    secure-boot-otp-provisioning \
    "

# TPM and fTPM functionality
RDEPENDS:${PN}-tpm = " \
    ${RDEPENDS:${PN}-base} \
    optee-ftpm-iot2050 \
    "

# Complete security stack
RDEPENDS:${PN}-full = " \
    ${RDEPENDS:${PN}-base} \
    ${RDEPENDS:${PN}-secureboot} \
    ${RDEPENDS:${PN}-tpm} \
    "

# Basic security packagegroup (base components only)
RDEPENDS:${PN} = " \
    ${RDEPENDS:${PN}-base} \
    "

PACKAGES = "${PN} ${PN}-base ${PN}-secureboot ${PN}-tpm ${PN}-full"