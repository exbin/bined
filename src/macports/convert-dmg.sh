#!/bin/sh
hdiutil convert bined-0.2.4.dmg -format UDTO -o bined-0.2.4.cdr
hdiutil convert bined-0.2.4.cdr -format UDTO -o bined-0.2.4.files.dmg
rm bined-0.2.4.cdr