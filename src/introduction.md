# Introduction

Welcome to the Expressway docs! 
This site will house examples and documentation 
of various features found within Expressway and Roadrunner itself!

Expressway can be found [here](https://github.com/j5155/Expressway), and 
installation instructions can be found [here](installation.md).

We also have a [Discord server](https://discord.gg/YucesWwY4N) 
where you can ask questions and get help with Expressway and RoadRunner.

```admonish success
Latest Version: 0.3.6
```

## What is Expressway?

Expressway is a library built on top of RoadRunner that provides a more flexible and powerful way to create actions.
Actions, introduced in RoadRunner 1.0, are a way to create reusable and composable pieces of robot code.
Expressway builds on this concept by providing a way to create more complex actions that can be used in a variety of situations.

```admonish quote title="What is an Action?"
Actions help you define simple behaviors that are easy to combine into large routines. 
By breaking down autonomous programs you make them easier to understandand and modify. 
And then the base actions can be reused in new autonomous programs so you never need to start from scratch.
But most importantly of all your code will play nice with the Road Runner Quickstart. Let’s see how this all works!

\- RoadRunner Documentation
```

```admonish info
Actions are very similar to commands as implemented in libraries like WPILib and FTCLib. 
Road Runner uses a different name for this pattern to distinguish its particular design from these peer libraries. 
The ideas have also been explored extensively outside the FIRST realm. 
Check out cooperative multitasking and coroutines if you’re interested.
```

In addition to our additions to the action system, 
Expressway also provides a number of other features,
including converters for geometry classes, and a PIDF controller.

## Kotlin?

Expressway is written in Kotlin, a modern language that is fully interoperable with Java.
This means that you can use both Java and Kotlin code in the same project.
For the most part, the code examples in this documentation will be in Kotlin,
but Java versions of the same code will be provided when necessary.

More information about Kotlin can be found [here](https://kotlinlang.org/), 
and [this Cookbook article](https://cookbook.dairy.foundation/misc/why_kotlin/why_kotlin.html) 
explains some of its benefits and usage in FTC.
