package frc.team3128.common.hardware.limelight;

public enum Pipeline {
    CONE(0),
    CUBE(1),
    BOTH(2);

    private int pipeline;
    private Pipeline(int pipeline) {
        this.pipeline = pipeline;
    }

    public int getPipeline() {
        return pipeline;
    }
}