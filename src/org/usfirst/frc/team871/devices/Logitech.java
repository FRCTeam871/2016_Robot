package org.usfirst.frc.team871.devices;

/**
 * This class represents a Logitech joystick.
 * 
 * @author Team871
 *
 */
public class Logitech extends EnhancedJoystick {
    public enum ButtonType {
        ONE(1), 
        TWO(2), 
        THREE(3), 
        FOUR(4), 
        FIVE(5), 
        SIX(6), 
        SEVEN(7), 
        EIGHT(8), 
        NINE(9), 
        TEN(10), 
        ELEVEN(11),
        NUM_BUTTONS(12);

        final int tValue;

        private ButtonType(int tType) {
            tValue = tType;
        }

        public int get() {
            return tValue;
        }
    }

    public enum AxisType {
        X(0), Y(1), Z(2), THROTTLE(3), NUM_AXES(4);

        final int tValue;

        private AxisType(int tType) {
            tValue = tType;
        }

        public int get() {
            return tValue;
        }
    }
    
    public Logitech(int port) {
        super(port,AxisType.NUM_AXES.get(), ButtonType.NUM_BUTTONS.get());
    }

    /**
     * change value if the button state has just changed
     * 
     * @param buttonName
     * @return
     */
    public boolean justChanged(ButtonType buttonName) {
        return justChanged(buttonName.get());
    }

    /**
     * returns true when the button is just pressed
     * 
     * @param buttonName
     * @return
     */
    public boolean justPressed(ButtonType buttonName) {
        return justPressed(buttonName.get());
    }

    /**
     * Returns true when the button is just released
     * 
     * @param buttonName
     * @return
     */
    public boolean justReleased(ButtonType buttonName) {
        return justReleased(buttonName.get());
    }

    /**
     * Toggles a value when the button is pressed
     * 
     * @param buttonName
     * @return
     */
    public boolean isToggled(ButtonType buttonName) {
        return getToggleState(buttonName.get());
    }

    public boolean getButton(ButtonType buttonName) {
        return getRawButton(buttonName.get());
    }
    
    /**
     * Returns the value of a given axis
     * 
     * @param axis
     * @return
     */
    public double getAxis(AxisType axis) {
        return getRawAxis(axis.get());
    }

    /**
     * Returns the deadbanded value of a given axis
     * 
     * @param axis
     * @param deadband
     * @return
     */
    public double getAxis(AxisType axis, double deadband) {
        return getAxes(axis.get(), deadband);
    }
}
