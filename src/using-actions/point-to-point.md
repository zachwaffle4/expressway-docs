# PID-To-Point Action

The `PIDToPoint` class is an action that uses the [SquID Controller](../controllers/squidcontroller.md)
to move a drivetrain to a specified point. 
It is designed to work with RoadRunner's `MecanumDrive` class,
present in the `roadrunner-quickstart` repository.

```admonish warning 
The PIDToPoint action has not been tested in a real-world scenario, so it may not work as expected.
If you encounter any issues, please let us know by creating an issue on the Expressway GitHub repository, 
or by contacting us on our Discord server.
```

## Constructing `PIDToPoint`

The `PIDToPoint` constructor is the following:
```kt
    private val pose: Supplier<Pose2d>,
    private val vel: Supplier<PoseVelocity2d>,
    private val target: Pose2d,
    private val powerUpdater: Consumer<PoseVelocity2d>,
    axialCoefs: PIDFController.PIDCoefficients,
    lateralCoefs: PIDFController.PIDCoefficients,
    headingCoefs: PIDFController.PIDCoefficients,
```

A lot, right? Let's break it down.

### `Pose` and `Vel`

The `pose` and `vel` suppliers are functions that return the current pose and velocity of the robot, respectively.
The reason they are suppliers is that the pose and velocity of the robot change over time, 
and we need to get the current pose and velocity every time the action is updated.

They are designed to work with RoadRunner's `MecanumDrive` class, present in the `roadrunner-quickstart` repository. 
However, in order to update the `pose` variable of the `MecanumDrive` class, you need to call the `updatePoseEstimate` method,
so we recommend creating a function in your `MecanumDrive` class (or any subclass of it) called `getPose`
that updates the pose estimate and returns the pose.

```java
    public Pose2d getPoseEstiamte() {
        updatePoseEstimate();
        return pose;
    }
```
### `Target`

The `target` is the point that the robot is moving to.

### `PowerUpdater`

The `powerUpdater` consumer is a function that sets the power of the drivetrain motors based on the output of the Squid Controller.
It is designed to work with `MecanumDrive.setDrivePowers`, present in the `roadrunner-quickstart` repository.

### Coefficients

The `axialCoefs`, `lateralCoefs`, and `headingCoefs` are the PID coefficients for the Squid Controller,
discussed in the [PID Controller](../controllers/pidf-controller.md) section.
Conveniently, the values for your coefficients are actually tuned during the RoadRunner tuning process.

Because most of these parameters are obtained from the `MecanumDrive` class,
we recommend creating a function in your `MecanumDrive` class (or any subclass of it)
that returns a `PIDToPoint` action object with the correct parameters.

## Implementing `PIDToPoint`

Here is how you could create a function to return a `PIDToPoint` object in your `MecanumDrive` class. 

```java
    public PIDToPoint pidToPointAction(Pose2d target) {
        return new PIDToPoint(
                (this::getPoseEstimate)
                (this::updatePoseEstimate) // updatePoseEstimate, in addition to updating the pose property, returns the velocity of the robot
                target, // the target pose
                (this::setDrivePowers), // setDrivePowers uses inverse kinematics to set the powers of the drivetrain motors
                new PIDFController.PIDCoefficients(PARAMS.axialGain, 0, PARAMS.axialVelGain), // the axial PID coefficients
                new PIDFController.PIDCoefficients(PARAMS.lateralGain, 0, PARAMS.lateralVelGain), // the lateral PID coefficients
                new PIDFController.PIDCoefficients(PARAMS.headingGain, 0, PARAMS.headingVelGain) // the heading PID coefficients
        );
    }
```

Or in Kotlin (even though the RoadRunner quickstart is in Java, you can put Kotlin code in your project):

```kotlin
    fun pidfToPointAction(target: Pose2d): PIDToPoint = PIDToPoint(
        this::getPoseEstimate,
        this::linearVel,
        target,
        this::setDrivePowers,
        PIDFController.PIDCoefficients(RR_PARAMS.axialGain, 0.0, RR_PARAMS.axialVelGain), // the axial PID coefficients
        PIDFController.PIDCoefficients(RR_PARAMS.lateralGain, 0.0, RR_PARAMS.lateralVelGain), // the lateral PID coefficients
        PIDFController.PIDCoefficients(RR_PARAMS.headingGain, 0.0, RR_PARAMS.headingVelGain) // the heading PID coefficients
    )
```

The `::` operator in both Java and Kotlin can be used to reference a method or function,
so if a class method has the same signature as a function type (like `Supplier` or `Consumer`),
you can use the `::` operator to reference the method. 
In Kotlin, you can also use the `::` operator to reference a top-level function, or a class property.
For example, the `updatePoseEstimate` method in the `MecanumDrive` class has the same signature as a `Supplier<Pose2d>`,
so you can use `this::updatePoseEstimate` to reference the method and pass it to the `PIDToPoint` constructor.

Alternatively, since other Action types are classes, 
you could create a PIDToPointAction class that extends the PIDToPoint class
as a subclass of `MecanumDrive` that simply calls the constructor of the superclass with the correct parameters.

```kt 
class PIDToPointAction(target: Pose2d) : PIDToPoint(
            this::getPoseEstimate,
            this::linearVel,
            target,
            this::setDrivePowers,
            new PIDFController.PIDCoefficients(PARAMS.axialGain, 0, PARAMS.axialVelGain),
            new PIDFController.PIDCoefficients(PARAMS.lateralGain, 0, PARAMS.lateralVelGain),
            new PIDFController.PIDCoefficients(PARAMS.headingGain, 0, PARAMS.headingVelGain)
)
```

## Using `PIDToPoint`

Let's assume you added the `pidToPointAction` function to your `MecanumDrive` class.

Here is an example of how you might use the `PIDToPoint` action in an OpMode:

```kt 
class MyOpMode : ActionOpMode {
    private val drive by lazy { MecanumDrive(hardwareMap, Pose2d(0.0, 0.0, 0.0)) }
    private val target = Pose2d(24.0, 24.0, 0.0)
    private val action = drive.pidToPointAction(target)

    override fun init() {
        drive.updatePoseEstimate()
    }
    
    override fun start() {
        runner.runAction(action)
    }

    override fun loop() {
        runner.updateAsync()
    }
}
```

In this example, the robot will move to the point (24, 24, 0) until it reaches the target.
runAsync is only called during `start` so that the robot does not move during initialization.
The `updateAsync` method is called every loop to update the action.