BinEd - Binary/Hex Editor
=================================

Editor for binary data (hex viewer/editor) written in Java.

Homepage: https://bined.exbin.org/editor  

Screenshot
----------

![BinEd-Application Screenshot](images/bined_screenshot.png?raw=true)

Features
--------

  * Visualize data as numerical (hexadecimal) codes and text representation
  * Codes can be also binary, octal or decimal
  * Support for Unicode, UTF-8 and other charsets
  * Insert and overwrite edit modes
  * Searching for text / hexadecimal code with matching highlighting
  * Support for undo/redo
  * Support for files with size up to exabytes

Compiling
---------

Build commands: "gradle build" and "gradle distZip"

Java Development Kit (JDK) version 8 or later is required to build this project.

For project compiling Gradle 7.1 build system is used: https://gradle.org

You can either download and install gradle or use gradlew or gradlew.bat scripts to download separate copy of gradle to perform the project build.

On the first build there will be an attempt to download all required dependecy modules and currently it's necessary to execute build twice.

Alternative is to have all dependecy modules stored in local maven repository - Manually download all dependencies from GitHub (clone repositories from github.com/exbin - see. deps directory for names) and run "gradle publish" on each of them.

License
-------

Apache License, Version 2.0 - see LICENSE.txt  

