#!/bin/sh
#
# Copyright (c) Siemens AG, 2023
#
# Authors:
#   Li Hua Qian <huaqian.li@siemens.com>
#

update_json()
{
    sed -i '/"version": ".*"/s/"V.*"/"'$2'"/g' $1
    sed -i '/"description": ".*"/s/V.*["$]/'$2\"'/g' $1
}

generate_fwu_tarball()
{
    echo "Generating the firmware tarball..."

    if [ -e $2/iot2050-pg1-image-boot.bin ]; then
        echo "Error: iot2050-pg1-image-boot.bin doesn't exist!"
        exit 2
    fi

    if [ -e $2/iot2050-pg2-image-boot.bin ]; then
        echo "Error: iot2050-pg2-image-boot.bin doesn't exist!"
        exit 2
    fi

    if [ -e $2/u-boot-initial-env ]; then
        echo "Error: u-boot-initial-env doesn't exist!"
        exit 2
    fi

    mkdir -p $2/.tarball

    cp $1/update.conf.json $2/.tarball/update.conf.json
    update_json $2/.tarball/update.conf.json $3
    cp $2/iot2050-pg1-image-boot.bin $2/.tarball
    cp $2/iot2050-pg2-image-boot.bin $2/.tarball
    cp $2/u-boot-initial-env $2/.tarball

    tar -cJvf $2/IOT2050-FWU-PKG-$3.tar.xz -C $2/.tarball \
                                  iot2050-pg1-image-boot.bin \
                                  iot2050-pg2-image-boot.bin \ 
                                  u-boot-initial-env         \
                                  update.conf.json
    rm -rf $2/.tarball
}

generate_fwu_tarball $*
