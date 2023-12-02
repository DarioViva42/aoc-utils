# Advent of Code Utilities
This project contains some useful code, to help you focus on the problems themselves without thinking about all the additional setup that is needed to solve advent of code.

- [Setup](#setup)
- [Features](#features)
	- [Fetching puzzle inputs](#input)
	- [Sending puzzle solutions](#output)
  - [Log information about speed](#performance)
- [Putting it all together](#conclusion)

<a name="setup"></a>
## Setup
Add the maven dependancy to fetch the library from maven central.
```xml
<dependency>
    <groupId>tk.vivas.adventofcode</groupId>
    <artifactId>aoc-utils</artifactId>
    <version>1.1</version>
</dependency>
```
Before you can start using this library you will need to create a `security.properties` file.
> [!CAUTION]
> Please don’t forget to add the `security.properties` file to your projects `.gitignore` file when pushing to a public remote repository.
> 
There you will need to add a property called `aoc.session`.
After you have logged into your AoC account, you will find your session-id inside a cookie in the networks tab of your browser.

![image](https://github.com/DarioViva42/aoc-utils/assets/45972949/79b7b627-1893-40f3-80d2-51125a9108b2)

The session-id in the image above is blurred out, in the developer tools of your browser the id will be clearly visible.

In the `security.properties` file, replace `<YOUR_SESSION_ID>` with your session-id (without parenthesis).
```properties
aoc.session=<YOUR_SESSION_ID>
```

Using this project requires you to fallow a little bit of ceremony. This ceremony will in return make the usage of this utility much more straightforward.
Your files need to be contained in packages that fallow a certain pattern.
When you want to solve day 7 of the year 2023 for example you will need to create a class with a main method in a package that ends with `year2023.day11`.

```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.aoc
│   │   │       ├── year2022
│   │   │       │   ├── day01
│   │   │       │   │   └── Day1.java
│   │   │       │   ├── day02
│   │   │       │   │   └── Day2.java
│   │   │       │   └── day03
│   │   │       │       └── Day3.java
│   │   │       └── year2023
│   │   │           ├── day01
│   │   │           │   └── Day1.java
│   │   │           ├── day02
│   │   │           │   └── Day2.java
│   │   │           ├── day03
│   │   │           │   └── Day3.java
│   │   │           ├── day04
│   │   │           │   └── Day4.java
│   │   │           ├── day05
│   │   │           │   └── Day5.java
│   │   │           ├── day06
│   │   │           │   └── Day6.java
│   │   │           └── day03
│   │   │               └── Day7.java
│   │   └── resources
│   │       └── security.properties
│   └── test
├── pom.xml 
└── .gitignore
```

<a name="features"></a>
## Features
The main features of this project is the fetching of puzzle inputs and the sending of puzzle solutions.

<a name="input"></a>
### Fetching puzzle inputs
Instead of manually copying the puzzle input every day, you can simply call a helper method from this project.
```java
String input = AocUtils.readPuzzleInput();
```
The Utility will automatically fetch the input for the correct day as long as the calling class is located in the correct package.

<a name="output"></a>
### Sending puzzle solutions
This utility can automatically send your calculated solution to the advent of code server. You will need to provide it with your answer and the part you solved.
```java
sendPuzzleAnswer(1, 456);
```
The prior example demonstrates, what you need to do, when you want to send “456” as the anwer for the first part.

<a name="performance"></a>
### Log information about speed
When you quickly want to demonstrate how fast (or slow) your alghorithm for a particular day runs, you need to record certain timestamps.
1. Timestamp before you parse the input
2. Timestamp after you parsed the input, and before you solved part one
3. Timestamp after you solved part one, and before you solved part two
4. Timestamp after you solved both parts

```java
logDurations(start, parseEnd, partOneEnd, partTwoEnd);
```

<a name="conclusion"></a>
## Putting it all together
The fallowing example shows you how a Class for a certain day will look like.
```java
package com.example.aoc.year2023.day07;

import java.time.Instant;

import static tk.vivas.adventofcode.GeneralUtils.logDurations;
import static tk.vivas.adventofcode.GeneralUtils.readPuzzleInput;
import static tk.vivas.adventofcode.GeneralUtils.sendPuzzleAnswer;

public class Day07 {
    public static void main(String[] args) {
        String input = readPuzzleInput();

        Instant start = Instant.now();

        // parse your input
        Object parsed = Parser.parse(input);

        Instant parseEnd = Instant.now();

        // solve part one
        int partOneAnswer = Alghorithm.solvePartOne(parsed);

        Instant betweenParts = Instant.now();

        // solve part two
        long partTwoAnswer = Alghorithm.solvePartTwo(parsed);

        Instant end = Instant.now();

        sendPuzzleAnswer(1, partOneAnswer);
        sendPuzzleAnswer(2, partTwoAnswer);

        logDurations(start, parseEnd, betweenParts, end);
    }
}
```
