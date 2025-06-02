@echo off
if not exist "bin" mkdir bin
javac -encoding UTF-8 -d bin/ src/Herlang.java
javac -encoding UTF-8 -d bin/ src/HerlangExt.java
jar cfe Herlang.jar Herlang -C bin/ .
jar cfe HerlangExt.jar HerlangExt -C bin/ .
