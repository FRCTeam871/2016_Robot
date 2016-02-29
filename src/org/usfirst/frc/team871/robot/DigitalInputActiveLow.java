package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * This class represents a digital input that is active low
 * 
 * In other words, the signal is LOW, then the sensor is ACTIVE
 * 
 * @author Team871
 *
 */
public class DigitalInputActiveLow extends DigitalInput {

    public DigitalInputActiveLow(int channel) {
        super(channel);
    }

    @Override
    public boolean get() {
        return !super.get();
    }

}
