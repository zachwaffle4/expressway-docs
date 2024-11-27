# Action OpModes

Expressway comes with two new OpMode types: `ActionOpMode` and `ActionLinearOpMode`,
which can be used instead of the default OpMode and LinearOpMode, respectively.

## ActionOpMode
At its core, `ActionOpMode` is simply an `OpMode` with an `runner` object called `runner`.
This allows you to run actions asynchronously in the `loop` method of your OpMode, 
using the instructions in the [Action Runner](runner.md) section.

Here is an example of how you might use `ActionOpMode` in your code. Assume you've already created a
Claw class with two methods, `open` and `close`, that returns Actions that open and close the claw, respectively,
and an Arm class with a method `createPidfAction` that returns a `PIDFActionEx` object defined
in the [Creating Actions](../creating-actions/base-actions.md) section.

```kotlin
class MyOpMode : ActionOpMode() {
    private val claw by lazy { Claw() }
    private val arm by lazy { Arm() }
    private val pidfAction by lazy { arm.createPidfAction() }

    override fun init() {
        runner.runAsync(claw.open()) //ensure that the claw opens once we start using runAsync

        pidfAction.target = 0 //set the initial target position to 0
        runner.runAsync(pidfAction) //ensure that the arm moves to position 0 when we start using runAsync
    }

    override fun loop() {
        if (gamepad1.a) {
            runner.runAsync(claw.close())
        } else if (gamepad1.b) {
            runner.runAsync(claw.open())
        }

        if (gamepad1.dpad_up) {
            pidfAction.target = 100

            if (pidfAction.condition.invoke()) { //check to see if the arm has arrived at its target
                runner.runAsync(pidfAction) //if it had completed, it was removed from the queue, so we need to add it back
            }
        } else if (gamepad1.dpad_down) {
            pidfAction.target = 0

            if (pidfAction.condition.invoke()) { //check to see if the arm has arrived at its target
                runner.runAsync(pidfAction) //if it had completed, it was removed from the queue, so we need to add it back
            }
        }

        runner.updateAsync() //update each action
    }
}
```

## ActionLinearOpMode
(WIP)