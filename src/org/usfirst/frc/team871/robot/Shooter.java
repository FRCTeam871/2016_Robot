package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Shooter {

	private SpeedController aimShooter, fireMotor1, fireMotor2, beaterBarPos, beaterBarRoller;
	private DoubleSolenoid firePiston;
	private Potentiometer shooterPot;
	private DigitalInput loadedSense, beaterBarDeployed, beaterBarFolded, shooterUpperLimit, shooterLowerLimit;
	private ShootStates currState = ShootStates.AWAIT_INPUT;
	private boolean enabled = true;
	private long fireTimer = 0;
	private Drive tankDrive;
	final NetworkTable dashboard = NetworkTable.getTable("SmartDashboard");
	double desiredAngle;
	double centerOfMassX;
	PIDController pid;
	boolean manualMode = false;
	
	public Shooter(SpeedController aimShooter, SpeedController fireMotor1, SpeedController fireMotor2, SpeedController beaterBarPos, SpeedController beaterBarRoller, DoubleSolenoid firePiston, Potentiometer shooterPot, DigitalInput loadedSense, DigitalInput beaterBarDeployed, DigitalInput beaterBarFolded, Drive tankDrive, DigitalInput shooterUpperLimit, DigitalInput shooterLowerLimit){
		this.aimShooter        = aimShooter;
		this.fireMotor1        = fireMotor1;
		this.fireMotor2        = fireMotor2;
		this.beaterBarPos      = beaterBarPos;
		this.beaterBarRoller   = beaterBarRoller;
		this.firePiston        = firePiston;
		this.shooterPot        = shooterPot;
		this.loadedSense       = loadedSense;
		this.beaterBarDeployed = beaterBarDeployed;
		this.beaterBarFolded   = beaterBarFolded;
		this.tankDrive         = tankDrive;
		this.shooterUpperLimit = shooterUpperLimit;
		this.shooterLowerLimit = shooterLowerLimit;
		
		pid = new PIDController(1, 0, 0, shooterPot, aimShooter);
		pid.setPercentTolerance(1);
	}
	
	
	public void update(){
		desiredAngle  = dashboard.getNumber("theta", 0.0);
		
		if(enabled){
			switch (currState) {
			case AIM:
				tankDrive.autoAim();
				pid.setSetpoint(convertAngleToPotValues(desiredAngle));
				
				if(pid.onTarget() || manualMode){
					fireTimer = System.nanoTime();
					setCurrState(ShootStates.SPIN_UP);
				}
				break;
				
			case AWAIT_INPUT: //Initial State
				
				break;
				
			case FIRE:
				if(loadedSense.get()){
					firePiston.set(Value.kForward);
				}else{
					fireTimer = System.nanoTime();
					fireMotor1.set(0);
					fireMotor2.set(0);
					firePiston.set(Value.kReverse);
					setCurrState(ShootStates.SPIN_DOWN);
				}
				break;
				
			case LOAD_BOULDER:
				if(!loadedSense.get()){
					beaterBarRoller.set(-1); //TODO: What Direction?
				}else{
					beaterBarRoller.set(0);
					setCurrState(ShootStates.MOVE_TRANSPORT);
				}
				break;
				
			case MOVE_LOAD:
				if(!beaterBarDeployed.get()){ //TODO: direction
					beaterBarPos.set(-1);
				}else{
					beaterBarPos.set(0);
					setCurrState(ShootStates.LOAD_BOULDER);
				}
				break;
				
			case MOVE_TRANSPORT:
				fireMotor1.set(0);
				fireMotor2.set(0);
				
				if(!beaterBarFolded.get()){
					beaterBarPos.set(-1);
				}else{
					beaterBarPos.set(0);
				}
				
				if(Math.abs(shooterPot.get() - Vars.SHOOTER_POT_TRANSPORT_POSITION) > .1 ){ //TODO
					aimShooter.set(-1);
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
					fireMotor2.set(1); //TODO: Check directions/speed
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
			}
		}
	}
	
	public ShootStates getCurrState() {
		return currState;
	}

	public void setCurrState(ShootStates currState) {
		this.currState = currState;
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
	
	public double convertAngleToPotValues(double desiredAngle){
		return desiredAngle * 1;//TODO: pot range / angle range
	}
	
	public double setShooterSpeed(double speed){
		double limitedSpeed;
		if(shooterUpperLimit.get() && speed > 0){//TODO: direction
			limitedSpeed = 0;
		}else if(shooterLowerLimit.get() && speed < 0){
			limitedSpeed = 0;
		}else{
			limitedSpeed = speed;
		}
		return limitedSpeed;
	}
	
	public double setBeaterBarSpeed(double speed){
		double limitedSpeed;
		if(beaterBarDeployed.get() && speed > 0){//TODO: direction
			limitedSpeed = 0;
		}else if(beaterBarFolded.get() && speed < 0){
			limitedSpeed = 0;
		}else{
			limitedSpeed = speed;
		}
		return limitedSpeed;
	}

	public void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}
}
