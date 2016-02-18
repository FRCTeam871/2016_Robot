package org.usfirst.frc.team871.robot;

import org.usfirst.frc.team871.robot.Logitech.AxisType;
import org.usfirst.frc.team871.robot.XBoxController.Axes;

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
	
	Encoder telescopeEncoder;
	
	XBoxController xbox;
	
	public LifterStates currState = LifterStates.TRANSPORT;
	boolean autoEnabled = true;
	
	public Lifter(SpeedController telescopingMotor, DoubleSolenoid raisor, DigitalInput grabSense, DigitalInput armUpSense, DoubleSolenoid lockSolenoid, Encoder telescopeEncoder, SpeedController pullUpMotor, XBoxController xbox, LimitedSpeedController beaterBarPos){
		this.telescopingMotor = telescopingMotor;
		this.liftPiston = raisor;
		this.lockSolenoid = lockSolenoid;
		this.grabSense = grabSense;
		this.armDeployedSense = armUpSense;
		this.pullUpMotor = pullUpMotor;
		this.telescopeEncoder = telescopeEncoder;
		this.xbox = xbox;
		this.beaterBarPos = beaterBarPos;
		
		if(this.armDeployedSense.get()){
			setCurrState(LifterStates.STARTUP_RESET);
		}
		
	}
	/**
	 * This method is called in doAuto() to update the state machine
	 */
	public void update(){
		switch(currState){
		case TRANSPORT:
			liftPiston.set(Value.kReverse);
			
			if(xbox.justPressed(Vars.LIFTER_ADVANCE_BUTTON)){
				currState = LifterStates.DEPLOY_ARM;
			}
			
			break;
			
		case DEPLOY_ARM:
			if(xbox.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			//beaterBarPos.set(-0.05);
			
			if(xbox.justPressed(Vars.LIFTER_ADVANCE_BUTTON) && armDeployedSense.get()){ //TODO: Check if it's active low
				currState = LifterStates.EXTEND; 
			}else{
				liftPiston.set(Value.kForward);
			}
			break;
			
		case EXTEND:
			if(xbox.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			telescopingMotor.set(xbox.getAxisDeadBand(Axes.RIGHTy, .15));

			if(xbox.justPressed(Vars.LIFTER_ADVANCE_BUTTON) && grabSense.get()){ //TODO: Check if it's active low
				currState = LifterStates.PULL_UP;
			}
			
			break;
			
		case PULL_UP:
			if(xbox.justPressed(Vars.LIFTER_ABORT_BUTTON)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(armDeployedSense.get()){
				pullUpMotor.set(xbox.getAxisDeadBand(Axes.RIGHTy, .15));
			
				if(xbox.justPressed(Vars.LIFTER_ADVANCE_BUTTON)){
					currState = LifterStates.LOCKED;
				}
				
			}else{
				currState = LifterStates.DEPLOY_ARM;
			}
			break;
			
		case LOCKED:
			lockSolenoid.set(Value.kForward);
			break;
			
		case STARTUP_RESET:
			telescopingMotor.set(xbox.getAxisDeadBand(Axes.RIGHTy, .15));
			pullUpMotor.set(-xbox.getAxisDeadBand(Axes.RIGHTy, .15));//TODO direction
			
			if(xbox.justPressed(Vars.LIFTER_ADVANCE_BUTTON)){ //TODO: Check if it's active low
				currState = LifterStates.TRANSPORT;
			}
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
		LOCKED,
		STARTUP_RESET;
	}
	
}
