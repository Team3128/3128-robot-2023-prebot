package frc.team3128;

public class PositionConstants {
    public static enum Position {
        HIGH_CONE(47, 0, true),
        HIGH_CUBE(48.5, 35, false),
        MID_CONE(41, -31, true),
        MID_CUBE(25, 30, false),
        LOW(3, 45, true),

        SHELF_CONE(53, -30, true),
        SHELF_CUBE(37, 22, false),
        CHUTE_CONE(10, 55, true),
        CHUTE_CUBE(10, 45, false),

        GROUND_CONE(8, -18, true),
        GROUND_CUBE(2.5, 0, false),

        NEUTRAL(3, 80, false);

        public double elvDist;
        public double wristAngle;
        public boolean cone;

        private Position(double elvDist, double wristAngle, boolean cone) {
            this.elvDist = elvDist;
            this.wristAngle = wristAngle;
            this.cone = cone;
        }
    }
}
