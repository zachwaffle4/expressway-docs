# PIDF Controller
The PIDF Controller is a controller that uses a PIDF algorithm to control a motor's positionn.
It is actually a port of the PIDF Controller present in RoadRunner 0.5, 
so if you're familiar with that, you should be able to use this controller with ease.

## What is PIDF?
PIDF stands for Proportional, Integral, Derivative, and Feedforward.
It is a control loop feedback mechanism widely used in industrial control systems.
The PIDF controller calculates an error value as the difference between a desired setpoint (target) 
and a measured process variable (actual value - in FTC, a motor position).
The controller attempts to minimize the error by calculating a power value to apply to the motor.

For more information, see [CtrlAltFtc](https://www.ctrlaltftc.com/), 
a website commonly referenced for control theory in FTC.

## How do I use it?
To use the PIDF Controller, you first need to create an instance of it.
There are a few constructor options for it, 
but the most basic one is `PIDFController(coefficients: PIDCoefficients)`.
This constructor takes in a `PIDCoefficients` object, which is a data class 
with three double parameters: `kp`, `ki`, and `kd`.

Here is an example of how you might use the PIDF Controller in an OpMode:
```kotlin
class MyOpMode : OpMode {
    private val motor by lazy { hardwareMap.get(DcMotorEx::class.java, "motor") } }
    private val pidfController by lazy { PIDFController(PIDCoefficients(1.0, 0.0, 0.0)) } 
    //the coefficients are just placeholders, you should tune them to your motor

    override fun init() {
        pidfController.targetPosition = 1000 //set the target position to 1000
    }

    override fun loop() {
        motor.power = pidfController.update(motor.currentPosition)
        //update the motor power based on the current position *every loop*
    }
}
```

## Feedforward
The other constructors for PIDFController allow you to specify feedforward coefficients.
Feedforward is a control strategy that uses a model of the system to predict the output of the system.

There are three feedforward coefficients and a feedforward function: `kV`, `kA`, `kStatic`, and `kF`.
- `kV` is the velocity feedforward coefficient.
- `kA` is the acceleration feedforward coefficient.
- `kStatic` is the additive feedforward constant.
- `kF` is a custom feedforward function of type `FeedforwardFun`.
  - `FeedforwardFun` is an interface with a single method `compute(position: Double, velocity: Double?): Double`.

When creating a PIDFController object, you can specify all of those, just `kV`, `kA` and `kStatic`, or just `kF`.

Here is an example of how you might use the PIDF Controller with feedforward in an OpMode:
```kotlin
class MyOpMode : OpMode {
    private val motor by lazy { hardwareMap.get(DcMotorEx::class.java, "motor") } }
    private val pidfController by lazy { PIDFController(PIDCoefficients(1.0, 0.0, 0.0), 0.1, 0.1, 0.1) } 
    //the coefficients are just placeholders, you should tune them to your motor

    override fun init() {
        pidfController.targetPosition = 1000 //set the target position to 1000
    }

    override fun loop() {
        motor.power = pidfController.update(System.nanoTime(), motor.currentPosition, motor.velocity)
        //when specifing velocity, you must also specify the time in nanoseconds
        //update the motor power based on the current position and velocity *every loop*
    }
}
```

Using the PIDFController with Actions can be found in the [Base Actions](../creating-actions/base-actions.md) section.

## Tuning

Tuning the PIDF Controller is a process of adjusting the coefficients to get the desired behavior.
There are many resources online that can help you with this,
including the [CtrlAltFtc](https://www.ctrlaltftc.com/) website mentioned earlier.

To make tuning easier, all of the PIDFController parameters
are public `var`s so you can change them after object creation. 

This article will not go into detail about tuning the PIDF Controller,
but here is a brief overview of the effects of increasing the basic PID coefficients on the system:

### Effects of increasing coefficients on the system:

| Parameter | Rise Time    | Overshoot | Settling Time | Steady-State Error | Stability |
|-----------|--------------|-----------|---------------|--------------------|-----------|
| `kP`      | Decrease     | Increase  | Small Change  | Decrease           | Decrease  |
| `kI`      | Decrease     | Increase  | Increase      | Eliminate          | Decrease  |
| `kD`      | Small Change | Decrease  | Decrease      | No Change          | Increase  |

Table sourced from [Wikipedia](https://en.wikipedia.org/wiki/PID_controller#Manual_tuning).