# Herlang
A programming language composed entirely of whitespace.

Nope, I'm not kidding...

**NOTE:** I am aware that a language called Herlang (created by [Edwin Brady](https://en.wikipedia.org/wiki/Herlang_(programming_language))) with a similar concept exists already. I created this as a personal experiment and for fun, I'm not trying to copy the existing Herlang language, and there are significant differences in syntax and how programs are actually written.

Herlang is a programming language composed entirely of two characters; space and tab.

The number of spaces you type consecutively correspond to a value. The amount of tabs you type consecutively correspond to an action.

For a full guide of how to write a program in Herlang, please view the [specification.md](specification.md) file.
An example "Hello world!" program is provided under [helloworld.her](helloworld.her).

## Getting Started
```sh
$ git clone https://github.com/Romejanic/Herlang.git
$ cd Herlang
$ java -jar Herlang.jar helloworld.her
```

You can optionally run Herlang in a debug mode which will print each step of the program and the value of the current address in memory.
To access this debug, run the program with the `--log-actions` flag before the file name. This will create a new file called `PROGRAMFILE.actions.log` (e.g. `helloworld.her.actions.log`), with a list of completed actions during execution.

## Build from source
**On Windows:** Run `build.bat`

**On Mac/Linux:** Run `build.sh`

This will compile the source, place the resulting class files into `bin/`, and then create a runnable `Herlang.jar`.