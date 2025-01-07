# cp ../icons/icon.icns .
# cp ../../LICENSE.txt .
jpackage --type app-image --input src --name BinEd --app-version 1000.2.4 --icon icon.icns --main-jar bined.jar --main-class org.exbin.bined.editor.BinedEditor --mac-package-identifier org.exbin.bined --dest output
# Create version without included Java runtime
rm -r output/BinEd.app/Contents/runtime
rm output/BinEd.app/Contents/MacOS/BinEd
cp bined.sh output/BinEd.app/Contents/MacOS/BinEd
chmod +x output/BinEd.app/Contents/MacOS/BinEd
# --app-image-64 --app-image-arm
jpackage --app-image output/BinEd.app --app-version 1000.2.4 --icon icon.icns --license-file LICENSE.txt --about-url "https://bined.exbin.org/editor" --dest output
