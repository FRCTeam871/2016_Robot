package org.usfirst.frc.team871.robot;



import edu.wpi.first.wpilibj.Joystick;

public class XBoxController extends Joystick{

	boolean[] lastButtonValues = new boolean[8];
	
	public XBoxController(int port) {
		super(port);
	}
	
	public enum Axes{
		LEFTx(0),
		LEFTy(1),
		lTRIGGER(2),
		rTRIGGER(3),
		RIGHTx(4),
		RIGHTy(5);
		
		private final int axisNum;
		Axes(int axis){
			axisNum = axis;
		}
		public int getAxisNum(){
			return axisNum;
		}
	}
	
	public enum Buttons{
		A(1, false),
		B(2, false),
		X(3, false),
		Y(4, false),
		LB(5, false),
		RB(6, false),
		BACK(7, false),
		START(8, false);
		
		private final int buttonNum;
		boolean toggleValue;
		
		Buttons(int button, boolean toggleState){
			this.buttonNum = button;
			this.toggleValue = toggleState;
		}
		public int getButtonNum(){
			return buttonNum;
		}
	}
	
	public boolean justChanged(Buttons buttonName){
		int button = buttonName.getButtonNum();
		
		boolean justChanged = false;
		
		if(lastButtonValues[button] != getRawButton(button)){
			justChanged = true;
		}
		lastButtonValues[button] = getRawButton(button);
		
		return justChanged;
	}
	
	public boolean justPressed(Buttons buttonName){
		int button = buttonName.getButtonNum();
		
		boolean justPressed = false;
		
		if((!lastButtonValues[button] && getRawButton(button))){
			justPressed = true;
		}
		lastButtonValues[button] = getRawButton(button);//store values
		
		return justPressed;
	}
	
	public boolean justReleased(Buttons buttonName){
		int button = buttonName.getButtonNum();
		
		boolean justReleased = false;
		
		if((lastButtonValues[button] && !getRawButton(button))){
			justReleased = true;
		}
		lastButtonValues[button] = getRawButton(button);//store values
		
		return justReleased;
	}
	
	public boolean isToggled(Buttons buttonName){
		
		int button = buttonName.getButtonNum();
		
		boolean toggleState = buttonName.toggleValue;
		
		if(justPressed(buttonName)){
			toggleState = !toggleState;
		}
		lastButtonValues[button] = getRawButton(button);//store values
		
		return toggleState;
	}
	
	public double getAxisValue(Axes axis){
		return getRawAxis(axis.getAxisNum());
	}
	
	public double getAxisDeadBand(Axes axis, double deadband){
		double x = getRawAxis(axis.getAxisNum());
		double dead = deadband;
		
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
}
