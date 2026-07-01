BinEd - Binary/Hex Editor
=========================

Editor for binary data (hex viewer/editor) written in Java.

Homepage: https://bined.exbin.org/editor  

Features
--------

- Visualize data as numerical (hexadecimal) codes and text representation
- Codes can be also binary, octal or decimal
- Support for Unicode, UTF-8 and other charsets
- Insert and overwrite edit modes
- Searching for text / hexadecimal code with matching highlighting
- Support for undo/redo
- Support for files with size up to exabytes

Run
---

Java (JRE or JDK) version 8 or later is required to run this project.

To start the application directly, there are multiple options.

If you installed version for Windows via installer, you can start the application via Start menu in applications list.

Alternativelly, go to the directory where your application is installed.

You can start "bined.exe" on MS Windows systems or on systems with Windows emulation installed - for example using Wine.

"bined.sh" is a simple script which should work on Linux or macOS systems. It might be necessary to assign execution bit on the file (for example via "chmod +x bined.sh" command).

On some systems it might be also possible to execute "bined.jar" depending on how and which variant of the Java is installed.

Application can be also started via command line, but this might also depend on the operating system, Java version and whether are Java tool applications setup for direct execution.

  java -jar bined.jar

Application also accepts few command line parameters, add "--help" parameter to see available options.

License
-------

Apache License, Version 2.0 - see LICENSE.txt  

