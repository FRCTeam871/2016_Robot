package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class Lifter {

	SpeedController telescopingMotor, pullUpMotor; //TODO: Check if there's other motors (winch)
	
	Solenoid liftPiston, lockSolenoid;
	
	DigitalInput grabSense, armDeployedSense; //TODO: Check if we have these
	DigitalInput telescopeUpperLimit, telescopeLowerLimit; //TODO: Check if we have these
	
	public LifterStates currState = LifterStates.TRANSPORT;
	
	boolean autoEnabled = true;
	
	public Lifter(SpeedController telescopingMotor, Solenoid raisor, DigitalInput grabSense, DigitalInput armUpSense, Solenoid lockSolenoid, Encoder telescopeEncoder, SpeedController pullUpMotor, DigitalInput armLowerLimit, DigitalInput armUpperLimit){
		this.telescopingMotor = telescopingMotor;
		this.liftPiston = raisor;
		this.lockSolenoid = lockSolenoid;
		this.grabSense = grabSense;
		this.armDeployedSense = armUpSense;
		this.pullUpMotor = pullUpMotor;
		this.telescopeLowerLimit = armLowerLimit;
		this.telescopeUpperLimit = armUpperLimit;
		
	}
	
	@SuppressWarnings("unused")
	public void update(){
		switch(currState){
		case DEPLOY_ARM:
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ABORT_CONTROL)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ADVANCE_CONTROL) && armDeployedSense.get()){ //TODO: Check if it's active low
				currState = LifterStates.EXTEND; 
			}else{
				liftPiston.set(true);
			}
			break;
			
		case PULL_UP:
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ABORT_CONTROL)){
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
			lockSolenoid.set(true);
			break;
			
		case EXTEND:
			if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ABORT_CONTROL)){
				currState = LifterStates.TRANSPORT;
			}
			
			if(armDeployedSense.get()){ //TODO: Check if it's active low
				if(!telescopeUpperLimit.get()){ //TODO: Check if it's active low
					telescopingMotor.set(1);
					//pullUpMotor.set(-1); //TODO: Check
				}else{
					telescopingMotor.set(0);
					//pullUpMotor.set(0); //TODO: Check
					
					if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ADVANCE_CONTROL) && !grabSense.get()){ //TODO: Check if it's active low
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
					//pullUpMotor.set(1); //TODO: Comment this out before booting. (Check Directions) <- VERY IMPORTANT*******************************
				}
				else{
					liftPiston.set(false);
				}
			}
			else{
				if(Robot.stickJoy.getRisingEdge(Vars.LIFER_ADVANCE_CONTROL)){
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
	
	public enum LifterStates{
		TRANSPORT,
		DEPLOY_ARM,
		EXTEND,
		PULL_UP,
		LOCKED;
	}
	
}
