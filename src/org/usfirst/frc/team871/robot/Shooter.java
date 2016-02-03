package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class Shooter {

	private SpeedController aimShooter, fireMotor1, fireMotor2, beaterBarPos1, beaterBarPos2, beaterBarRoller;
	
	private Solenoid firePiston;
	
	private static ShootStates currState = ShootStates.AWAIT_INPUT;
	
	private boolean enabled = true;
	
	private long fireTimer = 0;
	
	public Shooter(SpeedController aimShooter, SpeedController fireMotor1, SpeedController fireMotor2, SpeedController beaterBarPos1, SpeedController beaterBarPos2, SpeedController beaterBarRoller, Solenoid firePiston){
		this.aimShooter       = aimShooter;
		this.fireMotor1       = fireMotor1;
		this.fireMotor2       = fireMotor2;
		this.beaterBarPos1    = beaterBarPos1;
		this.beaterBarPos2    = beaterBarPos2;
		this.beaterBarRoller  = beaterBarRoller;
		this.firePiston       = firePiston;
	}
	
	@SuppressWarnings("unused")
	public void update(){
		if(enabled){
			switch (currState) {
			case AIM:
				//TODO: Auto Aim
				fireTimer = System.nanoTime();
				setCurrState(ShootStates.SPIN_UP);
				break;
				
			case AWAIT_INPUT: //Initial State
				
				break;
				
			case FIRE:
				if(true/*BOULDER NOT EXITED*/){ //TODO
					firePiston.set(true);
				}else{
					fireTimer = System.nanoTime();
					fireMotor1.set(0);
					fireMotor2.set(0);
					firePiston.set(false);
					setCurrState(ShootStates.SPIN_DOWN);
				}
				break;
				
			case LOAD_BOULDER:
				if(true/*NOT LOADED*/){ //TODO
					beaterBarRoller.set(-1); //TODO: What Direction?
				}else{
					beaterBarRoller.set(0);
					setCurrState(ShootStates.MOVE_TRANSPORT);
				}
				break;
				
			case MOVE_LOAD:
				if(true/*IF ABOVE LOADING POSITION AND NOT IN RANGE*/){ //TODO
					beaterBarPos1.set(-1);
					beaterBarPos2.set(-1);
				}else if(true/*IF BELOW AND NOT IN RANGE*/){ //TODO
					beaterBarPos1.set(1);
					beaterBarPos2.set(1);
				}else{
					beaterBarPos1.set(0);
					beaterBarPos2.set(0);
					setCurrState(ShootStates.LOAD_BOULDER);
				}
				break;
				
			case MOVE_TRANSPORT: //TODO: Fix Logic
				if(true/*IF ABOVE TRANSPORT POSITION AND NOT IN RANGE*/){ //TODO
					beaterBarPos1.set(-1);
					beaterBarPos2.set(-1);
				}else if(true/*IF BELOW AND NOT IN RANGE*/){ //TODO
					beaterBarPos1.set(1);
					beaterBarPos2.set(1);
				}else{
					beaterBarPos1.set(0);
					beaterBarPos2.set(0);
				}
				
				if(true/*IF ABOVE TRANSPORT POSITION AND NOT IN RANGE*/){ //TODO
					aimShooter.set(-1);
				}else if(true/*IF BELOW AND NOT IN RANGE*/){ //TODO
					aimShooter.set(1);
				}else{
					aimShooter.set(0);
					setCurrState(ShootStates.AWAIT_INPUT);
				}
				break;
				
			case SPIN_DOWN:
				if(System.nanoTime() > fireTimer + Vars.SPIN_DOWN_TIME ){
					setCurrState(ShootStates.MOVE_TRANSPORT);
				}else{
					fireMotor1.set(-1);
					fireMotor2.set(1); //TODO: Check directions
				}
				break;
			case SPIN_UP:
				if(System.nanoTime() > fireTimer + Vars.SPIN_UP_TIME ){
					setCurrState(ShootStates.FIRE);
				}else{
					fireMotor1.set(1);
					fireMotor2.set(-1); //TODO: Check directions
				}
				break;
				
			default:
				//Ur Dumb.
				break;
			}
		}
	}
	
	public static ShootStates getCurrState() {
		return currState;
	}

	public static void setCurrState(ShootStates currState) {
		Shooter.currState = currState;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public enum ShootStates{
		AWAIT_INPUT,
		MOVE_LOAD,
		LOAD_BOULDER,
		MOVE_TRANSPORT,
		AIM,
		SPIN_UP,
		FIRE,
		SPIN_DOWN;
	}
	
}
