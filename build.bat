@echo off
if not exist "bin" mkdir bin
javac -d bin/ src/Herlang.java
jar cfe Herlang.jar Herlang -C bin/ .