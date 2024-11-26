class PIDFAction(private val motor: DcMotor, target: Int,coefficients: PIDFController.PIDCoefficients) : InitLoopAction {
    val pidf = PIDFController(coefficients)
    var target = target
        set(value) {
            pidf.targetPosition = value
            field = value
        }

    override fun init() {
        pidf.setTargetPosition(target)
    }

    override fun run(p: TelemetryPacket): Boolean {
        val position = motor.currentPosition
        val power = pidf.update(position.toDouble())

        p.put("Motor Info", "Target: $target; Error ${target-position}; Power: $power")

        motor.power = power

        return position !in (target - 50)..(target + 50)
    }
}

fun hasArrived(motor: DcMotor, target: Int) : () -> Boolean {
    return { motor.currentPosition in (target - 50)..(target + 50) }
}

class PIDFActionEx(
    private val motor: DcMotor, target: Int, coefficients: PIDFController.PIDCoefficients, )
    : InitLoopCondAction(hasArrived(motor, target))  {

    private val pidf = PIDFController(coefficients)
    var target = target
        set(value) {
            pidf.targetPosition = value
            field = value
        }

    override fun init() {
        pidf.setTargetPosition(target)
    }

    override fun loop(p: TelemetryPacket) {
        val position = motor.currentPosition
        val power = pidf.update(position.toDouble())

        p.put("Motor Info", "Target: $target; Error ${target-position}; Power: $power")

        motor.power = power
    }
}