package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class Lifter {

	SpeedController telescopingMotor;
	Solenoid liftPiston;
	DigitalInput grabSense, armUpSense; //TODO: Check if we have these
	
	public LifterStates currState = LifterStates.TRANSPORT;
	
	boolean autoEnabled = true;
	
	public Lifter(SpeedController telescopingMotor, Solenoid raisor, DigitalInput grabSense, DigitalInput armUpSense){
		this.telescopingMotor = telescopingMotor;
		this.liftPiston = raisor;
		this.grabSense = grabSense;
		this.armUpSense = armUpSense;
	}
	
	@SuppressWarnings("unused")
	public void update(){
		switch(currState){
		case ARM_UP:
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ABORT_CONTROL)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ADVANCE_CONTROL)){
				if(armUpSense.get()) currState = LifterStates.TELESCOPE_UP; //TODO: Check if it's active low
			}else{
				liftPiston.set(true);
			}
			break;
			
		case PULL_UP:
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ABORT_CONTROL)){
				currState = LifterStates.TRANSPORT;
			}
			if(armUpSense.get()){ //TODO: Check if it's active low
				if(true/*NOT ALL THE WAY UP*/){ //TODO
					telescopingMotor.set(-1);
				}else{
					telescopingMotor.set(0);
				}
			}else{
				currState = LifterStates.ARM_UP;
			}
			break;
			
		case TELESCOPE_UP:
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ABORT_CONTROL)){
				currState = LifterStates.TRANSPORT;
			}
			if(armUpSense.get()){ //TODO: Check if it's active low
				if(true/*NOT FULLY EXTENDED*/){ //TODO
					telescopingMotor.set(1);
				}else{
					if(telescopingMotor.get() != 0) telescopingMotor.set(0);
					if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ADVANCE_CONTROL) && !grabSense.get()){ //TODO: Check if it's active low
						currState = LifterStates.PULL_UP;
					}
				}
			}else{
				currState = LifterStates.ARM_UP;
			}
			break;
			
		case TRANSPORT:
			if(true/*ABOVE TRANSPORT*/){ //TODO
				telescopingMotor.set(-1);
				if(liftPiston.get()) liftPiston.set(false);
			}
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ADVANCE_CONTROL)){
				currState = LifterStates.ARM_UP;
			}
			break;
			
		default:
			//Ur Dumb.
			break;
		
		}
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
	
	public enum LifterStates{
		TRANSPORT,
		ARM_UP,
		TELESCOPE_UP,
		PULL_UP;
	}
	
}
