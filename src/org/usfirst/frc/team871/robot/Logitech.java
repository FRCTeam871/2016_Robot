package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Logitech extends Joystick{
	
	public Logitech(int port) {
		super(port);
	}
	/**
	 * Returns true when the button is just pressed
	 * @param bt
	 * @return
	 */
	public boolean getRisingEdge(ButtonType bt){
		boolean press = false;
		
		 if(!bt.wasPressed && getRawButton(bt.get())){
			 press = true;
		 }
		 
		 bt.wasPressed = getRawButton(bt.get());
		 return press;
	}
	/**
	 * Returns the value of the toggled variable
	 * @param bt
	 * @return
	 */
	public boolean getToggledButton(ButtonType bt){
		
		return bt.toggled;
	}
	/**
	 * Toggles the value of the toggle variable
	 * @param bt
	 */
	public void toggleButton(ButtonType bt){
		if(getRisingEdge(bt)){
			bt.toggled = !bt.toggled;
		}
	}
	/**
	 * Returns the deadbanded value of the given Axis
	 * @param at
	 * @return
	 */
	public double getDeadAxis(AxisType at){
		double x = getRawAxis(at.get());
		double dead = at.getDeadBand();
		
		if (x < -dead){
    		return (x/(1.0-dead))+ (dead/(1-dead));
    	}
    	else if (x > dead){
    		return (x/(1.0-dead))- (dead/(1-dead));
    	}
    	else {
    		return 0;
    	}
		
	}

	public enum ButtonType {
		ONE (1),
		TWO (2),
		THREE (3),
		FOUR (4),
		FIVE (5),
		SIX (6),
		SEVEN (7),
		EIGHT (8),
		NINE (9),
		TEN (10),
		ELEVEN (11);
		
		final int tValue;
		
		boolean toggled = false;
		boolean wasPressed = false;
		
		private ButtonType(int tType) {
			tValue = tType;
		}
		
		public int get(){
			return tValue;
		}
	}
	
	public enum AxisType {
		X (0),
		Y (1),
		Z (2);
		
		final int tValue;
		double deadBand = Vars.DEFAULT_AXIS_DEADBAND;
		
		private AxisType(int tType) {
			tValue = tType;
		}
		
		public void setDeadBand(double deadBand){
			this.deadBand = deadBand;
		}
		
		public double getDeadBand(){
			return deadBand;
		}
		
		public int get(){
			return tValue;
		}
	}
	
}
