package org.usfirst.frc.team871.devices;

public class XBoxController extends EnhancedJoystick {

    public enum Axes {
        LEFTx(0), 
        LEFTy(1), 
        lTRIGGER(2), 
        rTRIGGER(3), 
        RIGHTx(4), 
        RIGHTy(5),
        NUM_AXES(6);

        private final int axisNum;

        Axes(int axis) {
            axisNum = axis;
        }

        public int getAxisNum() {
            return axisNum;
        }
    }

    public enum Buttons {
        A(1), 
        B(2), 
        X(3), 
        Y(4), 
        LB(5), 
        RB(6), 
        BACK(7), 
        START(8),
        NUM_BUTTONS(9);

        private final int buttonNum;

        Buttons(int button) {
            this.buttonNum = button;
        }

        public int getButtonNum() {
            return buttonNum;
        }
    }
    
    public XBoxController(int port) {
        super(port, Axes.NUM_AXES.getAxisNum(), Buttons.NUM_BUTTONS.getButtonNum());
    }

    /**
     * change value if the button state has just changed
     * 
     * @param buttonName
     * @return
     */
    public boolean justChanged(Buttons buttonName) {
        return justChanged(buttonName.getButtonNum());
    }

    /**
     * returns true when the button is just pressed
     * 
     * @param buttonName
     * @return
     */
    public boolean justPressed(Buttons buttonName) {
        return justPressed(buttonName.getButtonNum());
    }

    /**
     * Returns true when the button is just released
     * 
     * @param buttonName
     * @return
     */
    public boolean justReleased(Buttons buttonName) {
        return justReleased(buttonName.getButtonNum());
    }

    /**
     * Toggles a value when the button is pressed
     * 
     * @param buttonName
     * @return
     */
    public boolean isToggled(Buttons buttonName) {
        return getToggleState(buttonName.getButtonNum());
    }

    /**
     * Returns the value of a given axis
     * 
     * @param axis
     * @return
     */
    public double getAxes(Axes axis) {
        return getRawAxis(axis.getAxisNum());
    }

    /**
     * Returns the deadbanded value of a given axis
     * 
     * @param axis
     * @param deadband
     * @return
     */
    public double getAxes(Axes axis, double deadband) {
        return getAxes(axis.getAxisNum(), deadband);
    }
}
