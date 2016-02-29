package org.usfirst.frc.team871.devices;

import edu.wpi.first.wpilibj.DigitalInput;

public class DigitalLimitSwitch implements LimitSwitch {

    DigitalInput dio;
    
    public DigitalLimitSwitch(DigitalInput dio) {
        this.dio = dio;
    }
    
    @Override
    public boolean atLimit() {
        return dio.get();
    }

}
