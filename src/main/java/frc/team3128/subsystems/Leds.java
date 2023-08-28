package frc.team3128.subsystems;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdle.VBatOutputMode;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.CANdleControlFrame;
import com.ctre.phoenix.led.CANdleStatusFrame;
import com.ctre.phoenix.led.RainbowAnimation;

import com.ctre.phoenix.led.ColorFlowAnimation.Direction;
import com.ctre.phoenix.led.LarsonAnimation.BounceMode;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;
import com.ctre.phoenix.led.TwinkleOffAnimation.TwinkleOffPercent;


import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team3128.Constants.LedConstants;

public class Leds extends SubsystemBase {
    private final CANdle m_candle = new CANdle(LedConstants.CANDLE_ID);

    private static Leds instance;

    public static Leds getInstance() {
        if (instance == null) {
            instance = new Leds();
        }
        return instance;
    }

    public enum States {
        OFF(0,0,0),
        CONE(255,255,0),
        CUBE(255,87,51),
        HOLDING(255,255,255),

        AUTO(255,0,0),
        DRIVER(0,255,0);

        private final int r;
        private final int b;
        private final int g;

        States(int r, int g, int b) {
            this.r = r;
            this.g = b;
            this.b = g;
        }

    }

    public Leds() {
        configCandle();
    }

    private void configCandle() {
        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = LEDStripType.RGB;
        config.brightnessScalar = 1;
        m_candle.configAllSettings(config);
    }

    public void setElevatorColor(States state) {
        m_candle.setLEDs(state.r,state.g,state.b,LedConstants.WHITE_VALUE,0,LedConstants.ELEVATOR_COUNT);
    }

    public void setUnderglowColor(States state) {
        m_candle.setLEDs(state.r,state.g,state.b,LedConstants.WHITE_VALUE,LedConstants.ELEVATOR_COUNT,LedConstants.UNDERGLOW_COUNT);
    }

    
    

}