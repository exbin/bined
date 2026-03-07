#!/bin/sh
flatpak-builder --user --install --force-clean build-dir org.exbin.BinEd.json
#flatpak run org.exbin.BinEd
#flatpak remove org.exbin.BinEd
#flatpak run org.freedesktop.appstream-glib validate org.exbin.BinEd.appdata.xml
#desktop-file-validate org.exbin.BinEd.desktop
