class PIDAction(private val motor: DcMotor, target: Int, private val controller: PIDFController) : InitLoopAction() {

    var target = target
        set(value) {
            pidf.targetPosition = value
            field = value
        }

    override fun init() {
        pidf.setTargetPosition(target)
    }

    override fun loop(p: TelemetryPacket): Boolean {
        val position: Int = motor.getCurrentPosition()
        val power: Double = pidf.update(position.toDouble())

        p.put("Motor Info", "Target: " + target + "; Error " + (target - position) + "; Power: " + power)

        motor.setPower(power)

        return position < (target - 50) || position > (target + 50)
    }
}

fun hasArrived(motor: DcMotor, target: Int) : Condition {
    return { motor.currentPosition !in (target - 50)..(target + 50) }
}

class PIDFActionEx(
    private val motor: DcMotor, target: Int, private val controller: PIDFController)
    : InitLoopCondAction(hasArrived(motor, target))  {

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