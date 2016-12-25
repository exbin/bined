#!/bin/bash

# 64-bit
#ARCH=x86_64-linux

BUILD_ROOT=/tmp/build-root/deltahex-editor
mkdir -p $BUILD_ROOT
cp -r -f ../../../deltahex-editor-java/. $BUILD_ROOT
cp -r -f ../../../deltahex-editor-offline/. $BUILD_ROOT
cp -r -f ../../../deltahex-editor-java/src/debian $BUILD_ROOT
cd $BUILD_ROOT
dpkg-buildpackage -b -rfakeroot -us -uc
