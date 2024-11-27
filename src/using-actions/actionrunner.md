# Action Runner

If you've used RoadRunner's Action system, you're probably familiar with `Actions.runBlocking`.
runBlocking is, well, blocking: it runs its argument until its done, blocking any other code from running.
This is usually fine for autonomous, but in teleop, you are often using a loop. 
Since runBlocking blocks the loop, you can't receive other inputs while an action is running.

The [teleop actions](https://rr.brott.dev/docs/v1-0/guides/teleop-actions/) section of the RoadRunner docs 
explain how to run actions during the teleop loop, but it's a bit complicated.

Expressway simplifies this process with the `ActionRunner` class.

## What is ActionRunner?

`ActionRunner` is a class that allows you to run actions asynchronously.
There are actually three variants of `ActionRunner`: `ActionRunner` itself, 
its superclass `NoPreviewActionRunner`, and `UniqueActionRunner`. 

- `ActionRunner` is the most basic of the three, and is the one you will use most often.
- `NoPreviewActionRunner` is a superclass of `ActionRunner` whose constructor requests a function
that takes in a `TelemetryPacket` object and returns nothing (`Unit`).
  - `ActionRunner`'s constructor has no parameters, as it passes FTC Dashboard's `sendTelemetryPacket` 
function to its super constructor.
- `UniqueActionRunner` is a subclass of `ActionRunner` that checks if the action is already present in the queue before adding it.
  - This is useful if you want to prevent the same action from running multiple times.

## How do I use it?

To use `ActionRunner`, you first need to create an instance of it. This must be done in the `init` method of your OpMode.
In Kotlin, you can use `by lazy` to create the instance lazily, like so:

```kotlin
class MyOpMode : OpMode() {
    private val actionRunner by lazy { ActionRunner() }
    
    override fun init() {
        //do something
    }
    
    override fun loop() {
        //do something
    }
}
```

In Java, you can create the instance in the `init` method:

```java
public class MyOpMode extends OpMode {
    private ActionRunner actionRunner;
    
    @Override
    public void init() {
        actionRunner = new ActionRunner();
    }
    
    @Override
    public void loop() {
        //do something
    }
}
```

Once you have an instance of `ActionRunner`, you can add actions to it using the `runAsync` method.
`runAsync` takes in an `Action` object and adds it to the queue to be run asynchronously.

Finally, you need to call `updateAsync` in your loop method to run the actions.

Here is an example of how you might use `ActionRunner` in an OpMode. Assume you've already created a
Claw class with two methods, `open` and `close`, that returns Actions that open and close the claw, respectively,
and an Arm class with a method `createPidfAction` that returns a `PIDFActionEx` object defined 
in the [Creating Actions](../creating-actions/base-actions.md) section.

```kotlin
class MyOpMode : OpMode() {
    private val actionRunner by lazy { ActionRunner() }
    private val claw by lazy { Claw() }
    private val arm by lazy { Arm() }
    private val pidfAction by lazy { arm.createPidfAction() }
    
    override fun init() {
        actionRunner.runAsync(claw.open()) //ensure that the claw opens once we start using runAsync
      
        pidfAction.target = 0 //set the initial target position to 0
        actionRunner.runAsync(pidfAction) //ensure that the arm moves to position 0 when we start using runAsync
    }
    
    override fun loop() {
        if (gamepad1.a) {
            actionRunner.runAsync(claw.close())
        } else if (gamepad1.b) {
            actionRunner.runAsync(claw.open())
        } 
      
        if (gamepad1.dpad_up) {
          pidfAction.target = 100

          if (pidfAction.condition.invoke()) { //check to see if the arm has arrived at its target
              actionRunner.runAsync(pidfAction) //if it had completed, it was removed from the queue, so we need to add it back
          }
        } else if (gamepad1.dpad_down) {
          pidfAction.target = 0

          if (pidfAction.condition.invoke()) { //check to see if the arm has arrived at its target
              actionRunner.runAsync(pidfAction) //if it had completed, it was removed from the queue, so we need to add it back
          }
        }
      
        actionRunner.updateAsync() //update each action
    }
}
```