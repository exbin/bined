Delta Hexadecimal Editor
========================

Hexadecimal viewer/editor written in Java / Swing.

Homepage: http://deltahex.exbin.org  

Screenshot
----------

![DeltaHex-Editor Screenshot](images/editor_screenshot.png?raw=true)

Features
--------

- Data as hexadecimal codes and text preview
- Insert and overwrite edit modes
- Support for selection and clipboard actions
- Support for showing unprintable/whitespace characters
- Support for undo/redo
- Support for charset/encoding selection
- Codes can be also binary, octal or decimal

Todo
----

- Searching for text / hexadecimal code with matching highlighting
- Delta mode - Only changes are stored in memory
- Support for huge files

Compiling
---------

Java Development Kit (JDK) version 7 or later is required to build this project.

For project compiling Gradle 2.0 build system is used. You can either download and install gradle and run

  gradle build

command in project folder or gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

There are currently manual dependencies which are expected to be deployed in local Maven repository via "gradle publish" command:

https://github.com/exbin/exbin-utils-java  
https://github.com/exbin/deltahex-java  
https://github.com/exbin/xbup-java  
https://github.com/exbin/exbin-framework-java  

License
-------

Apache License, Version 2.0 - see LICENSE-2.0.txt  
Editor modules: GNU Lesser General Public License v3.0  

