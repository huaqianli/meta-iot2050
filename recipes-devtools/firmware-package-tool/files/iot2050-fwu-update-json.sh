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
    cd ${TMPDIR}/work/ 
    version=$(git describe --abbrev=0 --tags)
    cd -
    mkdir -p $2/.tarball

    cp $1/update.conf.json $2/.tarball/update.conf.json
    update_json $2/.tarball/update.conf.json ${version}

    if [ -e $2/iot2050-pg1-image-boot.bin ]; then
        cp $2/iot2050-pg1-image-boot.bin $2/.tarball
    else
        echo "Warning: iot2050-pg1-image-boot.bin doesn't exist!"
    fi

    if [ -e $2/iot2050-pg2-image-boot.bin ]; then
        cp $2/iot2050-pg2-image-boot.bin $2/.tarball
    else
        echo "Warning: iot2050-pg2-image-boot.bin doesn't exist!"
    fi

    if [ -e $2/u-boot-initial-env ]; then
        cp $2/u-boot-initial-env $2/.tarball
    else
        echo "Warning: u-boot-initial-env doesn't exist!"
    fi

    # tar -cJvf $2/IOT2050-FWU-PKG-${version}.tar.xz -C $2/.tarball/ .
    cd $2/.tarball
    echo $2
    tar -cJvf $2/IOT2050-FWU-PKG-${version}.tar.xz *
    cd -
    rm -rf $2/.tarball
}

generate_fwu_tarball $*
