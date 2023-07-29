#!/bin/sh
flatpak-builder --user --install --force-clean build-dir org.exbin.BinEd.json
#flatpak run org.exbin.BinEd
#flatpak remove org.exbin.BinEd
