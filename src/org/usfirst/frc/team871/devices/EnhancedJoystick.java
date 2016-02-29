package org.usfirst.frc.team871.devices;

import edu.wpi.first.wpilibj.Joystick;

public class EnhancedJoystick extends Joystick {
    private final boolean[] mLastButtonStates;
    private final boolean[] mButtonToggleStates;
    
    protected EnhancedJoystick(int port, int numAxisTypes, int numButtonTypes) {
        super(port, numAxisTypes, numButtonTypes);
        mLastButtonStates = new boolean[numButtonTypes];
        mButtonToggleStates = new boolean[numButtonTypes];
    }
    
    public boolean getToggleState(int button) {
        boolean toggleState = mButtonToggleStates[button];

        if (justPressed(button)) {
            toggleState = !toggleState;
            mButtonToggleStates[button] = toggleState;
        }
        
        mLastButtonStates[button] = getRawButton(button);// store values

        return toggleState;
    }
    
    public boolean justPressed(int button) {
        boolean justPressed = false;
        boolean buttonValue = getRawButton(button);

        if ((!mLastButtonStates[button] && buttonValue)) {
            justPressed = true;
        }
        
        mLastButtonStates[button] = buttonValue;// store values

        return justPressed;
    }
    
    public boolean justReleased(int button) {
        boolean justReleased = false;
        boolean buttonValue  = getRawButton(button);
        
        if ((mLastButtonStates[button] && !buttonValue)) {
            justReleased = true;
        }
        
        mLastButtonStates[button] = buttonValue;// store values

        return justReleased;
    }
    
    public boolean justChanged(int button) {
        boolean justChanged = false;
        boolean buttonValue = getRawButton(button);
        
        if (mLastButtonStates[button] != buttonValue) {
            justChanged = true;
        }
        
        mLastButtonStates[button] = buttonValue;

        return justChanged;
    }
    
    /**
     * Returns the deadbanded value of a given axis
     * 
     * @param axis
     * @param deadband
     * @return
     */
    public double getAxes(int axis, double deadband) {
        double x = getRawAxis(axis);

        if (x < -deadband) {
            return (x / (1.0 - deadband)) + (deadband / (1 - deadband));
        }
        else if (x > deadband) {
            return (x / (1.0 - deadband)) - (deadband / (1 - deadband));
        }
        else {
            return 0;
        }
    }
}
