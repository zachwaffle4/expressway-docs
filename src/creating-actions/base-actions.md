# New Base Actions 
Expressway comes with two new base action types: `InitLoopAction` and `InitLoopCondAction` (aka `ILCAction`).
These can be used for anything a regular `Action` can be used for, but they are easier to read and write.

## InitLoopAction
`InitLoopAction` is an `Action` with two methods: `init` and `loop: Boolean`. 
`init` is called once when the action is first started, 
and `loop` is what is called when the action is running 
(we will discuss ways to run your Actions later).

Just like the base `Action`, `loop` should return `true` if the action needs to be continued, 
and `false` if it is done.

Here is an example of an `InitLoopAction` that moves a motor to a position 
using Expressway's PIDF Controller (more information about what it does and how it works
are in [PIDF Controller](../controllers/pidf-controller.md)):

```kotlin
{{#include pidfactions.kt::23}}
```

In this example, `init` sets the target position of the PIDF controller,
and `loop` uses the PIDF controller to calculate what the motor's power should be, and sets that power appropriately.
Note that just like a regular `Action`, `loop` should return `true` if the action is not done, and `false` if it is.
In the case of this action, it will return `true` until the motor is within 50 encoder ticks of the target position.
In addition, just like with the base `Action`, there  is a `TelemetryPacket p` that is passed as an argument to the `loop` method.
This allows you to add telemetry to your action, which is sent to FTCDashboard and can help in debugging.

When declaring the `target` variable, we specify that its `set` function 
(called in Kotlin by saying `<object>.target = value` or in Java with `<object>.setTarget(value)`)
will also update the `targetPosition` field of the PIDF controller.
This allows us to change the target position of the action before it completes, which may be useful in some cases.

Here is a Java version of the same action:

```java
{{#include PIDFAction.java}}
``` 

### InitLoopCondAction

Similarly to the `InitLoopAction`, the `InitLoopCondAction` is an `Action` with two methods: `init` and `loop`.
However, the constructor of the `InitLoopCondAction` takes a `Condition`, 
an interface with a single method `invoke: Boolean`. 
Every time the action is run, it will check `condition` and stop if it returns true (which is opposite to both `Action` and `InitLoopAction`).

One benefit of this (aside from the fact that it is easier to read and write)
is that you can access the `isRunning` property to check if the action is running (i.e. `condition` is false).

You can also access the `condition` function from the `Action` object
with `<object>.condition` (or `<object>.getCondition()` in Java).
Because `condition` is a function, to call it you need parentheses, like `<object>.condition()`.
Note that in Java you must call the `invoke` function explicitly, like `<object>.condition.invoke()`, 
whereas in Kotlin you can call it like a regular function, `<object>.condition()`.
The reason for this is explained in the example below.

Here is an example of an `InitLoopCondAction` using the same PIDF controller as before:

```kotlin
{{#include pidfactions.kt:23:}}
```

In this situation, we define a function `hasArrived(motor: DcMotor, target: Int): () -> Boolean` that returns true if the motor is within 50 encoder ticks of the target position.
The return type `() -> Boolean` indicates that we are actually returning a function[^note], 
and not whether the motor has arrived or not at the time `hasArrived` is called. 
This is because we want to check if the motor has arrived every time the action is run, not just when it is first instantiated.
When defining `hasArrived`, we use `{}` to specify that we are defining a function to return, and not returning the Boolean value of the expression inside the braces.

When creating the `InitLoopCondAction`, we pass `hasArrived` into the *superclass* constructor.
and the action will return `true` when `hasArrived` returns `false`, meaning it will continue.
Once `hasArrived` returns `true`, the action will return `false`, meaning it is done.

We can also see that the `init` method is the same as before, 
and the `loop` method is almost the same as before, but it does not return any value.

Lastly, instead of defining `hasArrived` as a separate function, we could define it as an anonymous function in the constructor of the `InitLoopCondAction`:

```kotlin
class PIDFActionEx(
    private val motor: DcMotor, target: Int, coefficients: PIDFController.PIDCoefficients, ) 
    : InitLoopCondAction( { motor.currentPosition in (target - 50)..(target + 50) }) {
```

This is useful if you only need to use the function once, and do not want to define it elsewhere in your code.
Remember that if you create a `PIDFActionEx` object called `action`, you can access the `condition` function with `action.condition`,
and you can call it with `action.condition()`.

Here is a Java version of the same action:

```java
{{#include PIDFActionEx.java}}
```

Note that in Java we must specifically define `hasArrived` as returning a `Condition` object.
This is because Java does not automatically convert a lambda[^note] function to an interface with a single method
(also known as a functional interface) like Kotlin does.

Just like before, though, we can define `hasArrived` as an anonymous function in the constructor of the `InitLoopCondAction`:

```java
public class PIDFActionEx extends InitLoopCondAction {
    public PIDFActionEx(DcMotor motor, int target, PIDFController.PIDCoefficients coefficients) {
        super(() -> motor.getCurrentPosition() >= target - 50 && motor.getCurrentPosition() <= target + 50);
        //rest of the constructor
    }
}
```

[^note]: Kotlin will convert `hasArrived` to a `Condition` object automatically,
which is why we can pass it directly into the `InitLoopCondAction` constructor.
This is similar to how Java converts `0` to a `double` when you pass it into a method that takes a `double` parameter.
    
[^note]: A lambda function is a function that is not defined by name, but rather by its parameters and return type. 
For example, `val add = { a: Int, b: Int -> a + b }` is a lambda function that takes two `Int`s and returns their sum.
The contents of `hasArrived` in both the Kotlin and Java examples actually return a lambda function,
which is why it works as a `Condition` object in Kotlin. 
Lambda functions and functional interfaces go hand-in-hand,
as lambda functions are often used to define the single method in a functional interface,
like the variant constructors of `PIDFActionEx` in both Kotlin and Java.
