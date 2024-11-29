# Squid Controller

The Squid Controller is a variant of the PIDF Controller 
that square roots the output. The reason for this is 
the simple kinematic equation \\({v_f}^2 = {v_i}^2 + 2ad \\),
where \\(v_f\\) is the final velocity, \\(v_i\\) is the initial velocity,
\\(a\\) is the acceleration, and \\(d\\) is the displacement.

If we take the square root of both sides, we get
\\(v_f = \sqrt{{v_i}^2 + 2ad} \\), which is the equation for the final velocity.
Note that in a PID controller, we are minimizing the error by incorporating the position, velocity, and acceleration,
which are the three terms in the equation above. 

Since setting motor power effectively sets velocity[^note], 
the output of the Squid Controller will be more kinematically accurate than the output of the PIDF Controller.

Another benefit of the Squid Controller is that it is nonlinear,
which means that it can be more accurate than a linear controller in some cases.
For example, a linear controller might not be able to accurately control a motor at low speeds,
but a Squid Controller can.

## Using `SquidController`

Using the `SquidController` is very similar to using the `PIDFController`.
You can create an instance of it with the same constructor as the `PIDFController`,
and the `update` method works the same way as the `update` method in the `PIDFController`.

Here is an example of how you might use the `SquidController` in an OpMode:
```kotlin
class MyOpMode : OpMode {
    private val motor by lazy { hardwareMap.get(DcMotorEx::class.java, "motor") }
    private val squidController by lazy { SquidController(PIDCoefficients(1.0, 0.0, 0.0)) } 
    //the coefficients are just placeholders, you should tune them to your motor

    override fun init() {
        squidController.targetPosition = 1000 //set the target position to 1000
    }

    override fun loop() {
        motor.power = squidController.update(motor.currentPosition)
        //update the motor power based on the current position *every loop*
    }
}
```

Actions that use the `SquidController` could thus be built in the same way as actions that use the `PIDFController`;
since the `SquidController` is a subclass of the `PIDFController`,
you can actually pass a `SquidController` object into an action that expects a `PIDFController`, 
such as the `PIDFAction` and `PIDFActionEx` from the [Base Actions](../creating-actions/base-actions.md) section.


[^note]: There is also a `setVelocity` method in the `DcMotorEx` class,
which runs an internal velocity PID controller, but it isn't recommended
because the internal controller runs at a much lower frequency than the vast majority of OpMode loops.