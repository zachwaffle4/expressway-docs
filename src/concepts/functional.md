# Functions as Parameters

```admonish warning
While this section is not necessary to understand Expressway, 
the concepts of higher order functions are useful to know about.
```

In a lot of Expressway's code, you might notice that a lot of classes and functions take parameters of type `Supplier`. 
`Supplier<T>` is a functional interface (discussed later in this article)
that has a single method,`get()`, which returns a value of type `T`. 
`T` can be any type, allowing us to specify the return type of the `get` method when we create a `Supplier` object,
instead of when we define the `Supplier` interface.

This is useful because it allows us to pass functions as parameters to other functions,
which can be very powerful when you want to create more flexible and reusable code.
For example, to create an `InitLoopCondAction`, 
you need to pass a `Condition`, aka a `Supplier<Boolean>` that returns `true` when the action needs to be repeated.

You might notice that in the examples in the [Base Actions](../creating-actions/base-actions.md) section,
we don't actually define a `Condition` object when we create an `InitLoopCondAction`,
we instead create a function that returns a `Condition` object.

## Lambda Functions

Lambda functions are a way to define functions without giving them a name,
also known as anonymous functions.
They are useful when you only need to use a function once,
and don't want to define it elsewhere in your code.

For example, in the `PIDFActionEx` class in the [Base Actions](../creating-actions/base-actions.md) section,
we define a function `hasArrived` that returns true if the motor is within 50 encoder ticks of the target position.
The return type `Condition` indicates that we are actually returning a function,
and not whether the motor has arrived or not at the time `hasArrived` is called.

When defining `hasArrived`, we use `{}` to specify that we are defining a function to return,
and not returning the Boolean value of the expression inside the braces. 
When creating the `InitLoopCondAction`, we pass `hasArrived` into the *superclass* constructor,
which is the constructor of the `InitLoopCondAction` class.

## Functional Interfaces

Functional interfaces are interfaces that have a single abstract method,
which is why they are also known as Single Abstract Method (SAM) interfaces.

In Java, functional interfaces are used to define lambda functions,
as the lambda function must match the signature of the single abstract method in the functional interface.

As mentioned before, `Supplier<T>` is a functional interface with a single method, `get()`.
This is why we can pass a lambda function that returns a `Boolean` into the `InitLoopCondAction` constructor,
as the lambda function matches the signature of the `get` method in the `Supplier` interface.

In Kotlin, functional interfaces are not explicitly defined, 
as Kotlin has first-class support for lambda functions.
This means that you can pass a lambda function directly into a function that expects a functional interface,
or a function that expects a lambda function.

However, to ensure compatibility with Java, Expressway uses Java's default functional interfaces,
such as `Supplier` and `Consumer`. 
However, some parts of Expressway will either create a Kotlin functional interface,
or a typealias for a Java functional interface, to make the code more readable.
For example, `Condition` is a typealias for `Supplier<Boolean>`, 
which allows us to not specify the type of the `Supplier` when we create a `Condition` object.

## Higher Order Functions

Higher order functions are functions that take other functions as parameters, or return functions as results.
This is why we can pass a lambda function into the `InitLoopCondAction` constructor,
as the `InitLoopCondAction` constructor is a higher order function.

Higher order functions are useful when you want to create more flexible and reusable code.

Java does not have first-class support for higher order functions,
which is why we need to use functional interfaces to define lambda functions.

## Other Functional Interfaces

There are many other functional interfaces in Java, such as `Function`, `BiFunction`, `Predicate`, and `Consumer`.
The other two functional interfaces used in Expressway are `BiFunction` and `Consumer`.

`BiFunction<T, U, R>` is a functional interface with a single method, `apply(T t, U u) : R`, 
that takes two parameters of type `T` and `U`, and returns a value of type `R`.
It is the superclass of `FeedforwardFun`, which is used in the `PIDFController` class.

`Consumer<T>` is a functional interface with a single method, `accept(T t)`,
that takes a parameter of type `T`, and does not return a value.
It used in the PIDToPoint class to allow the user to specify a function 
that sets the powers of drivetrain motors based on the result of the PID controller.
