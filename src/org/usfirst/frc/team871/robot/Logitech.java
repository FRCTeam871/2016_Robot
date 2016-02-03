package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Logitech extends Joystick{
	
	public Logitech(int port) {
		super(port);
	}
	
	public boolean getRisingEdge(ButtonType bt){
		boolean press = false;
		
		 if(!bt.wasPressed && getRawButton(bt.get())){
			 press = true;
		 }
		 
		 bt.wasPressed = getRawButton(bt.get());
		 return press;
	}
	
	public boolean getToggledButton(ButtonType bt){
		
		if(getRisingEdge(bt)){
			bt.toggled = !bt.toggled;
		}
		
		return bt.toggled;
	}
	
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
