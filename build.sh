#/bin/sh
mkdir -p bin
javac -d bin/ src/Herlang.java
jar cfe Herlang.jar Herlang -C bin/ .
