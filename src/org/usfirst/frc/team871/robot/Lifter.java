package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;

public class Lifter {

	SpeedController telescopingMotor, pullUpMotor; //TODO: Check if there's other motors (winch)
	LimitedSpeedController beaterBarPos;
	DoubleSolenoid liftPiston, lockSolenoid;
	
	DigitalInput grabSense, armDeployedSense; //TODO: Check if we have these
	DigitalInput telescopeUpperLimit, telescopeLowerLimit; //TODO: Check if we have these
	
	Encoder telescopeEncoder;
	
	XBoxController stickJoy;
	
	public LifterStates currState = LifterStates.TRANSPORT;
	boolean autoEnabled = true;
	
	public Lifter(SpeedController telescopingMotor, DoubleSolenoid raisor, DigitalInput grabSense, DigitalInput armUpSense, DoubleSolenoid lockSolenoid, Encoder telescopeEncoder, SpeedController pullUpMotor, DigitalInput telescopeLowerLimit, DigitalInput telescopeUpperLimit, XBoxController stickJoy, LimitedSpeedController beaterBarPos){
		this.telescopingMotor = telescopingMotor;
		this.liftPiston = raisor;
		this.lockSolenoid = lockSolenoid;
		this.grabSense = grabSense;
		this.armDeployedSense = armUpSense;
		this.pullUpMotor = pullUpMotor;
		this.telescopeLowerLimit = telescopeLowerLimit;
		this.telescopeUpperLimit = telescopeUpperLimit;
		this.telescopeEncoder = telescopeEncoder;
		this.stickJoy = stickJoy;
		this.beaterBarPos = beaterBarPos;
		
	}
	/**
	 * This method is called in doAuto() to update the state machine
	 */
	public void update(){
		switch(currState){
		case TRANSPORT:
			//if the lift is not in transport mode retract arm down and telescope down
			if(armDeployedSense.get() || !telescopeLowerLimit.get()){ //TODO: Check if it's active low
				//make sure that the telescope arms are down before folding the arm down
				if(!telescopeLowerLimit.get()){ //TODO: Check if it's active low
					telescopingMotor.set(-1);
					pullUpMotor.set(1); //TODO: 
				}
				else{
					//fold down arm
					liftPiston.set(Value.kReverse);
				}
			}
			else{
				if(stickJoy.justPressed(Vars.LIFTER_ADVANCE_BUTTON)){
					currState = LifterStates.DEPLOY_ARM;
				}
			}
			break;
			
		case DEPLOY_ARM:
			if(stickJoy.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			beaterBarPos.set(-0.05);
			
			if(stickJoy.justPressed(Vars.LIFTER_ADVANCE_BUTTON) && !armDeployedSense.get() && beaterBarPos.isAtLowerLimit()){ //TODO: Check if it's active low
				currState = LifterStates.EXTEND; 
			}else{
				liftPiston.set(Value.kForward);
			}
			break;
			
		case EXTEND:
			if(stickJoy.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(armDeployedSense.get()){ //TODO: Check if it's active low
				if(!telescopeUpperLimit.get()){ //TODO: Check if it's active low
					telescopingMotor.set(1);
					pullUpMotor.set(-1); //TODO: Check
				}else{
					telescopingMotor.set(0);
					pullUpMotor.set(0); //TODO: Check
					
					if(stickJoy.justPressed(Vars.LIFTER_ADVANCE_BUTTON) && grabSense.get()){ //TODO: Check if it's active low
						currState = LifterStates.PULL_UP;
					}
				}
			}else{
				currState = LifterStates.DEPLOY_ARM;
			}
			break;
			
		case PULL_UP:
			if(stickJoy.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(armDeployedSense.get()){
				if(!telescopeLowerLimit.get()){
					pullUpMotor.set(-1); //TODO: Direction
					telescopingMotor.set(1);
				}else{
					pullUpMotor.set(0);
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
			
		}
		
	}
	/**
	 * Call this method periodically to update the state machine
	 */
	public void doAuto(){ //Spam-Called when auto enabled
		if(autoEnabled){
			update();
		}
	}
	/**
	 * Use this method to enable/disable the state machine
	 * @param enabled
	 */
	public void setAutoEnabled(boolean enabled){
		autoEnabled = enabled;
	}
	/**
	 * returns the current state of the lifter
	 * @return
	 */
	public LifterStates getCurrState(){
		return currState;
	}
	/**
	 * Use this to manually set the state of the lifter
	 * @param currState
	 */
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
