package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

public class Lifter {

	SpeedController telescopingMotor, pullUpMotor; //TODO: Check if there's other motors (winch)
	
	DoubleSolenoid liftPiston, lockSolenoid;
	
	DigitalInput grabSense, armDeployedSense; //TODO: Check if we have these
	DigitalInput telescopeUpperLimit, telescopeLowerLimit; //TODO: Check if we have these
	
	Encoder telescopeEncoder;
	
	XBoxController stickJoy;
	
	public LifterStates currState = LifterStates.TRANSPORT;
	boolean autoEnabled = true;
	
	public Lifter(SpeedController telescopingMotor, DoubleSolenoid raisor, DigitalInput grabSense, DigitalInput armUpSense, DoubleSolenoid lockSolenoid, Encoder telescopeEncoder, SpeedController pullUpMotor, DigitalInput telescopeLowerLimit, DigitalInput telescopeUpperLimit, XBoxController stickJoy){
		this.telescopingMotor = telescopingMotor;
		this.liftPiston = raisor;
		this.lockSolenoid = lockSolenoid;
		this.grabSense = grabSense;
		this.armDeployedSense = armUpSense;
		//this.pullUpMotor = pullUpMotor; TODO: this motor may or may not be implemented
		this.telescopeLowerLimit = telescopeLowerLimit;
		this.telescopeUpperLimit = telescopeUpperLimit;
		//this.telescopeEncoder = telescopeEncoder;
		this.stickJoy = stickJoy;
		
	}
	
	public void update(){
		switch(currState){
		case DEPLOY_ARM:
			if(stickJoy.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(stickJoy.justPressed(Vars.LIFTER_ADVANCE_BUTTON) && armDeployedSense.get()){ //TODO: Check if it's active low
				currState = LifterStates.EXTEND; 
			}else{
				liftPiston.set(Value.kForward);
			}
			break;
			
		case PULL_UP:
			if(stickJoy.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(armDeployedSense.get()){ //TODO: Check if it's active low
				if(!telescopeLowerLimit.get()){ //TODO: Check if it's active low
					//pullUpMotor.set(-1); //TODO: Check
					telescopingMotor.set(1);
				}else{
					//pullUpMotor.set(0); //TODO: Check
					telescopingMotor.set(0);
					
					currState = LifterStates.LOCKED;
				}
			}else{
				currState = LifterStates.DEPLOY_ARM;
			}
			break;
			
		case LOCKED:
			lockSolenoid.set(Value.kForward);
			break;
			
		case EXTEND:
			if(stickJoy.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(armDeployedSense.get()){ //TODO: Check if it's active low
				if(!telescopeUpperLimit.get()){ //TODO: Check if it's active low
					telescopingMotor.set(1);
					//pullUpMotor.set(-1); //TODO: Check
				}else{
					telescopingMotor.set(0);
					//pullUpMotor.set(0); //TODO: Check
					
					if(stickJoy.justPressed(Vars.LIFTER_ADVANCE_BUTTON) && grabSense.get()){ //TODO: Check if it's active low
						currState = LifterStates.PULL_UP;
					}
				}
			}else{
				currState = LifterStates.DEPLOY_ARM;
			}
			break;
			
		case TRANSPORT:
			if(armDeployedSense.get() || !telescopeLowerLimit.get()){ //TODO: Check if it's active low
				if(!telescopeLowerLimit.get()){ //TODO: Check if it's active low
					telescopingMotor.set(-1);
					//pullUpMotor.set(1); //TODO: 
				}
				else{
					liftPiston.set(Value.kReverse);
				}
			}
			else{
				if(stickJoy.justPressed(Vars.LIFTER_ADVANCE_BUTTON)){
					currState = LifterStates.DEPLOY_ARM;
				}
			}
			break;
			
		default:
			//Ur Dumb.
			break;
		}
		
		telescopingMotor.set(limitedSpeed(telescopingMotor, telescopeUpperLimit, telescopeLowerLimit));
		pullUpMotor.set(limitedSpeed(pullUpMotor, telescopeUpperLimit, telescopeLowerLimit));
	}
	
	public void doAuto(){ //Spam-Called when auto enabled
		if(autoEnabled){
			update();
		}
	}
	
	public void setAutoEnabled(boolean enabled){
		autoEnabled = enabled;
	}
	
	public boolean getAutoEnabled(){
		return autoEnabled;
	}
	
	private double limitedSpeed(SpeedController motor, DigitalInput upperLimit, DigitalInput lowerLimit){
		double speed = motor.get();
		
		if(upperLimit.get() && speed>0){ //TODO: Check if it's active low; Check Directions.
			speed = 0;
		}else if(lowerLimit.get() && speed<0){ //TODO: Check if it's active low; Check Directions.
			speed = 0;
		}
		
		return speed;
	}
	
	public void reset(){
		currState = LifterStates.TRANSPORT;
	}
	public LifterStates getCurrState(){
		return currState;
	}
	public void setCurrState(LifterStates currState){
		this.currState = currState;
	}
	public enum LifterStates{
		TRANSPORT,
		DEPLOY_ARM,
		EXTEND,
		PULL_UP,
		LOCKED;
	}
	
}
