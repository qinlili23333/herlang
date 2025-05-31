# Herlang
一种只由“啊”和空格组成的编程语言，以便于某种性别的人使用。  
项目的原型是[Romejanic/Whitespace](https://github.com/Romejanic/Whitespace)，在此特别感谢[Jack Davenport](https://github.com/Romejanic)为本项目打下的基础。  

Herlang是一种完全由两个字符组成的编程语言：空格和“啊”。  
空格的连续输入对应一个值。连续输入的“啊”对应一个动作。  

完整的Herlang语法规范请查看[specification.md](specification.md)文件。  
一个示例“Hello world!”程序在[helloworld.her](helloworld.her)中提供。  
Herlang的历史可以在[herstory.md](herstory.md)中找到。

本项目会在HerHub上线后迁移到HerHub上以解决GitHub所存在的另一种性别的凝视问题。  

Herlang与来自赛博理塘的[HeLang](https://github.com/SAOKnight/helang)的区别就和巴基斯坦和卡巴斯基的区别一样大，请避免误认。  

## 开始使用Herlang
```sh
$ git clone https://github.com/qinlili23333/herlang.git
$ cd Herlang
$ java -jar Herlang.jar helloworld.her
```

你可以选择性的在调试模式下运行Herlang，这将打印程序的每一步以及当前内存地址的值。  
要访问此调试功能，请在文件名之前使用`--log-actions`标志运行程序。这将创建一个名为`PROGRAMFILE.actions.log`的新文件（例如`helloworld.her.actions.log`），其中包含执行期间完成的操作列表。

## 构建Herlang
要从源代码构建Herlang，你需要安装Java JDK 8或更高版本。

**在Windows上：** 运行 `build.bat`

**在Mac/Linux上：** 运行 `build.sh`

**在HarmonyOS NEXT上：** 由于 HarmonyOS NEXT 不支持Java JDK，你可能需要使用虚拟机来构建Herlang。

这将编译源代码，将生成的类文件放入 `bin/` 目录中，然后创建一个可运行的 `Herlang.jar`。