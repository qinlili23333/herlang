@echo off
if not exist "bin" mkdir bin
javac -d bin/ src/Herlang.java
javac -d bin/ src/HerlangExt.java
jar cfe Herlang.jar Herlang -C bin/ .
jar cfe HerlangExt.jar HerlangExt -C bin/ .
