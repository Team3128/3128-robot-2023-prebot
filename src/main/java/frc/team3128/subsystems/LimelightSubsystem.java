package frc.team3128.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team3128.common.hardware.limelight.Limelight;
import frc.team3128.common.hardware.limelight.LimelightKey;
import frc.team3128.common.hardware.limelight.Pipeline;
import frc.team3128.common.utility.NAR_Shuffleboard;
import static frc.team3128.Constants.LimelightConstants.*;

import java.util.function.BooleanSupplier;


//Class for the Limelight Subsystem 


public class LimelightSubsystem extends SubsystemBase{
    
    public static LimelightSubsystem instance;
    private Limelight m_Limelight;

    private BooleanSupplier isAligned;
    private BooleanSupplier addPlateau;

    public LimelightSubsystem() {
        m_Limelight = new Limelight("limelight-blob", CAMERA_ANGLE, CAMERA_HEIGHT, FRONT_DISTANCE);
        isAligned = () -> Math.abs(getObjectTX()) <= TX_THRESHOLD && getObjectHasValidTarget();
        addPlateau = () -> Math.abs(getObjectTX()) <= 3 && getObjectHasValidTarget();
        Pipeline p_both = Pipeline.BOTH;
        m_Limelight.setPipeline(p_both);
        initShuffleboard();
    }

    public static synchronized LimelightSubsystem getInstance() {
        if (instance == null) {
            instance = new LimelightSubsystem();
        }
        return instance;
    }

    public void initShuffleboard() {
        // data that will appear under General Tab
        NAR_Shuffleboard.addData("General", "Range", this::calculateObjectDistance, 1, 3);
        NAR_Shuffleboard.addData("General", "hasValidTarget", this::getObjectHasValidTarget, 2, 2);
        NAR_Shuffleboard.addData("General", "isCone", this::getisCone, 2, 2);
        NAR_Shuffleboard.addData("General", "isCube", this::getisCube, 2, 2);
        NAR_Shuffleboard.addData("General", "isGeneral", this::getisGeneral, 2, 2);
        NAR_Shuffleboard.addData("General", "ty", this::getObjectTY, 4, 1);
        NAR_Shuffleboard.addData("General", "tx", this::getObjectTX, 3, 1);
        NAR_Shuffleboard.addComplex("General", "LimelightInfo", this, 0,0);
        // data that will appear under Limelight Tab
        NAR_Shuffleboard.addData("Limelight", "ty", this::getObjectTY, 4, 1);
        NAR_Shuffleboard.addData("Limelight", "tx", this::getObjectTX, 3, 1);
        NAR_Shuffleboard.addComplex("Limelight", "LimelightInfo", this, 0,0);

    }

    
    
    //method to uniformly calculate distance to a ground target using a limelight
     
    public double calculateObjectDistance() {
        return m_Limelight.calculateDistToGroundTarget(OBJ_TARGET_HEIGHT / 2);
    }


    
    //method to get limelight horizontal offset (tx) to target
     
    public double getObjectTX() {
        return m_Limelight.getValue(LimelightKey.HORIZONTAL_OFFSET);
    }
    //getting shuffleboard data for cone 
    public boolean getisCone() {
        double[] data = m_Limelight.getCustomData();
        return data[0] == 1.00;
    }
    //getting shuffleboard data for cube
    public boolean getisCube() {
        double[] data2 = m_Limelight.getCustomData();
        return data2[1] == 0.00;
    }
    //getting shuffleboard data for general object
    public boolean getisGeneral() {
        double[] data3 = m_Limelight.getCustomData();
        return data3[2] == 2.00;
        

    }


     //method for getting limelight vertical offset (ty) to target
 
    public double getObjectTY() {
        return m_Limelight.getValue(LimelightKey.VERTICAL_OFFSET);
    }

    //method for getting if the ball limelight has a valid target
 
    public boolean getObjectHasValidTarget() {

        if(m_Limelight.getArea()> MIN_AREA){
            return m_Limelight.hasValidTarget();
        }
        else{
            return false;
        }
    }
    
    /**
     * Returns bottom-facing limelight object
     * @return ballLimelight object
     */
    public Limelight getObjectLimelight() {
        return m_Limelight;
    }
    

    public boolean isAligned() {
        return isAligned.getAsBoolean();
    }

    public boolean addPlateau() {
        return addPlateau.getAsBoolean();
    }

}
