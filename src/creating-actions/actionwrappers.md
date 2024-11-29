# Action Wrappers
Action Wrappers are a way to create actions that wrap around other actions. 
Two examples of this are `SequentialAction` and `ParallelAction`, present in RoadRunner itself.
These actions allow you to run multiple actions in sequence or in parallel, respectively.
In Expressway, we have a few more action wrappers that you can use to create more complex actions.

## RaceParallelAction

`RaceParallelAction` is an action that runs multiple actions in parallel, 
but stops when the first action finishes.

## ConditionalAction

`ConditionalAction` is an action that runs one of two actions based on a condition or `determinant`.
The `determinant` is a function that returns a Boolean, and the action will run `trueAction` if `determinant` returns true, 
and `falseAction` otherwise.

Here is an example `ConditionalAction`:

```kotlin
fun choose(): Action {
    return ConditionalAction(
        SleepAction(1.0),  // will be run if the conditional is true
        SleepAction(2.0) // will be run if the conditional is false
    ) { Math.random() > 0.5 } // lambda conditional function, returning either true or false;
    // this example picks which one to run randomly
}
```

In this example, `choose` returns a `ConditionalAction` that runs a `SleepAction` 
with a duration of 1 second if the random number is greater than 0.5,
and a `SleepAction` with a duration of 2 seconds otherwise.

Here is a Java version of the same action:

```java
public Action choose() {
    return new ConditionalAction(
        new SleepAction(1.0), // will be run if the conditional is true
        new SleepAction(2.0), // will be run if the conditional is false
        () -> Math.random() > 0.5 // lambda conditional function, returning either true or false;
        // this example picks which one to run randomly
    );
}
```

## TimeoutAction

TimeoutAction is an action that runs another action, but forcibly stops it after a certain amount of time.
This is useful if you want to ensure that an action does not run for too long.

However, it is important to ensure that the force stop does not cause any issues with the robot.

Here is an example `TimeoutAction`:

```kotlin
fun timeout(): Action {
    return TimeoutAction(
        SleepAction(5.0), // the action to run
        3.0 // the timeout in seconds
    )
}
```