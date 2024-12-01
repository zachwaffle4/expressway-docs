class PIDFAction extends InitLoopAction {
    private final DcMotor motor;
    private final PIDFController pidf;
    private int target;

    public PIDFAction(DcMotor motor, int target, PIDFController controller) {
        this.motor = motor;
        this.pidf = controller;
        this.target = target;
        this.pidf.setTargetPosition(target);
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
    public boolean loop(TelemetryPacket p) {
        int position = motor.getCurrentPosition();
        double power = pidf.update((double) position);

        p.put("Motor Info", "Target: " + target + "; Error " + (target - position) + "; Power: " + power);

        motor.setPower(power);

        return position < (target - 50) || position > (target + 50);
    }
}