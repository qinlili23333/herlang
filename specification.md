# Herlang Specification

## Overview
The interpreter stores an array of 4096 signed integers. This acts as the program's 'memory'.
The index of the array is stored in an integer. This index can be changed (see [Syntax](#Syntax)).
When performing most actions (such as addition, subtraction, division, etc.), they will 
be performed on the currently selected address in memory.

The language is composed of simple instructions defined by tabs. The number of tabs
corresponds to the action used (see table in [Syntax](#Syntax) section). An action is entered on a line,
followed by a new line (pressing enter). From here, the data associated with that action is
passed by pressing space to correspond with a numerical value.

For an example of a program written in Herlang, see [helloworld.her](helloworld.her).

## Syntax
Only two valid characters are permitted, single spaces ( ) and TABs (	).

Number of spaces correspond to a numerical value, for example:
```
   (three spaces) corresponds to the number 3.
     (five spaces) corresponds to the number 5.
```
A blank line corresponds to the number 0.
    
Tabs correspond to actions, the number of tabs chooses the action.
Table of valid actions (in this table, 'address' refers to the currently
selected memory address):
|Tab Count|Parameter Count|Action|
|---------|---------------|------|
|1|1|Set value of address.|
|2|1|Change active memory address.|
|3|1|Add value to address.|
|4|1|Subtract value from address.|
|5|1|Multiply value by address.|
|6|1|Divide address by value.|
|7|1|Copy value at address to specified address.|
|8|1|Move value at address to specified address, set current address to 0.|
|9|0|Print the value of the current address to the console.|
|10|0|Prints a line break into the console, moving all text to the next line.|
|11|1|Change print mode of console (0 for decimal/raw, 1 for ASCII).|

The parameter of the action is defined on the following line.
 	 
For example, to change the value of memory address 5 from 0 to 20, you would type:
```
		(2 tabs set the selected memory address to the following)
     (5 spaces sets the memory address to 5)
	(1 tab sets the value of the address to the following)
                    (20 spaces sets the value of memory address 5 to 20)
```
*Note: brackets are used to document the example, comments are 
not possible in Herlang.*

## Errors
Syntax errors may be caused by:
- Both tabs and spaces detected in the same line.
- A space is given without an associated action run before it.
- A character other than a tab or space being present.
- An invalid number of tabs are found.
	
Runtime errors (errors with the code's interpretation) may be caused by:
- A memory address outside the allowed range is given.
- An unidentified or invalid action is given.