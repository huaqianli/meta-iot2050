# SPDX-FileCopyrightText: Copyright 2025 Siemens AG
# SPDX-License-Identifier: MIT

DESCRIPTION = "application examples, pipeline elements and pre-trained AI for Hailo8"
LICENSE = "MIT"

inherit dpkg

SRC_URI = "git://git@github.com/hailo-ai/tappas.git;protocol=https;name=tappas;branch=master;destsuffix=${P}"
SRCREV_tappas = "4327923422ababaf3a9395f86bf39f5b34dcfd83"

SRC_URI += " \
    file://debian/control \
    file://debian/rules \
    file://debian/libgsthailotools.install \
    file://debian/tappas-apps.install \
    file://debian/hailo-post-processes.install \
    file://debian/tappas-tracers.install \
    file://debian/not-installed \
    file://files/patches/0001-tappas-Adapt-tappas-apps-for-compatibility-with-meta.patch \
"

# tappas-app requirements
SRC_URI += " \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/hefs/yolov5m_yuv.hef;md5sum=41eeee848844f65634d0873188b08ce1;subdir=debian/detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/hefs/yolov5m_vehicles_no_ddr_yuy2.hef;md5sum=64a6d26d172f671c2bfd2aa2d1840389;subdir=debian/license_plate_recognition \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/hefs/tiny_yolov4_license_plates_yuy2.hef;md5sum=dfb49b07250f9e2ea03add746faaa35b;subdir=debian/license_plate_recognition \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/lpr.raw;md5sum=5f2217b90b4bb4e405f7323038840786;subdir=debian/license_plate_recognition \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/hefs/lprnet_yuy2.hef;md5sum=409fd97b76d72aed32733090e35d1a4b;subdir=debian/license_plate_recognition \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/general/hefs/yolov5s_personface_nv12.hef;md5sum=0f1718e9d1001a9a3e38729e96937aef;subdir=debian/multistream_detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/multistream_detection_h265/detection0.mp4;md5sum=1bcc3ff8e9340d27be080c56d9baff67;subdir=debian/multistream_detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/multistream_detection_h265/detection1.mp4;md5sum=39bd1a803e3b229593aeec48bede6c89;subdir=debian/multistream_detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/multistream_detection_h265/detection2.mp4;md5sum=23db3d4453746da78ed06e5e285a3398;subdir=debian/multistream_detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/multistream_detection_h265/detection3.mp4;md5sum=d4bdc5b3737776098017edac2834403e;subdir=debian/multistream_detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/multistream_detection_h265/detection4.mp4;md5sum=57234ea865a45b331cbe965e458fe944;subdir=debian/multistream_detection \
    https://hailo-tappas.s3.eu-west-2.amazonaws.com/v3.29/imx8/media/multistream_detection_h265/detection5.mp4;md5sum=155c2d1ce92d0412f977fb0eb7965243;subdir=debian/multistream_detection \
"

DEPENDS += " hailort "
PROVIDES += " libgsthailotools tappas-apps hailo-post-processes tappas-tracers"

S = "${WORKDIR}/${P}"

do_prepare_build[cleandirs] += "${S}/debian"
do_prepare_build() {
    deb_add_changelog
    cp -r ${WORKDIR}/debian/* ${S}/debian/
}
