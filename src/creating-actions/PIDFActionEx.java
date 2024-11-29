public class PIDFActionEx extends InitLoopCondAction {
    private final DcMotor motor;
    private final PIDFController pidf;
    private int target;

    public PIDFActionEx(DcMotor motor, int target, PIDFController pidf) {
        super(hasArrived(motor, target));
        this.motor = motor;
        this.pidf = pidf;
        this.target = target;
        this.pidf.setTargetPosition(target);
    }

    private static Condition hasArrived(DcMotor motor, int target) {
        return () -> motor.getCurrentPosition() >= (target - 50) && motor.getCurrentPosition() <= (target + 50);
    }

    public void setTarget(int value) {
        this.pidf.setTargetPosition(value);
        this.target = value;
    }

    @Override
    public void init() {
        pidf.setTargetPosition(target);
    }

    @Override
    public void loop(TelemetryPacket p) {
        int position = motor.getCurrentPosition();
        double power = pidf.update((double) position);

        p.put("Motor Info", "Target: " + target + "; Error " + (target - position) + "; Power: " + power);

        motor.setPower(power);
    }
}