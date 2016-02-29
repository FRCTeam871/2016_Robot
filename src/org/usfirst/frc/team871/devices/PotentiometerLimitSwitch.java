package org.usfirst.frc.team871.devices;

import edu.wpi.first.wpilibj.interfaces.Potentiometer;

public class PotentiometerLimitSwitch implements LimitSwitch{
    public enum LimitDirection {
        Above, Below;
    }
    
    Potentiometer input;
    double value;
    LimitDirection direction;
    
    public PotentiometerLimitSwitch(Potentiometer input, double value, LimitDirection direction) {
        this.input = input;
        this.value = value;
        this.direction = direction;
    }
    
    @Override
    public boolean atLimit() {
        if(direction == LimitDirection.Above) {
            return input.get() > value;
        }
        else return input.get() < value;
    }

}
