BinEd - Binary/Hex Editor
=========================

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

CURRENTLY BROKEN: On the first build there will be an attempt to download all required dependecy modules and currently it's necessary to execute build twice.

Alternative is to deploy all dependecy modules into local maven repository.

You can try to run following commands. Start at parent directory to "bined" repo directory.

    git clone https://github.com/exbin/exbin-auxiliary-java.git
    cd exbin-auxiliary-java
    gradlew build publish
    cd ..
    git clone https://github.com/exbin/bined-lib-java.git
    cd bined-lib-java
    gradlew build publish
    cd ..
    git clone https://github.com/exbin/exbin-framework-java.git
    cd exbin-framework-java
    gradlew build publish
    cd .. 

License
-------

Apache License, Version 2.0 - see LICENSE.txt  

