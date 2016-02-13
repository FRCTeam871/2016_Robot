package org.usfirst.frc.team871.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Shooter class used to create a shooter
 * @author Team871-5
 *
 *
 * Things to do:
 *	Organize state machine
 *	
 */
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
		
		pid = new PIDController(1, 0, 0, this.shooterPot, this.aimShooter);
		pid.setPercentTolerance(1);
	}
	
	/**
	 * Call this method periodically to update the state machine.
	 */
	public void update(){
		desiredAngle  = dashboard.getNumber("theta", 0.0);
		
		if(enabled){
			switch (currState) {
			case AWAIT_INPUT: //Initial State
				
				break;
				
			case AIM:
				//dont autoaim if youre in manual mode
				if(!manualMode){
					tankDrive.autoAim();
					pid.setSetpoint(convertAngleToPotValues(desiredAngle));
				}
				
				if(pid.onTarget() || manualMode){
					fireTimer = System.nanoTime();
					setCurrState(ShootStates.SPIN_UP);
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
				
			case FIRE:
				if(loadedSense.get()){
					firePiston.set(Value.kForward);
				}else{
					fireTimer = System.nanoTime();
					firePiston.set(Value.kReverse);
					setCurrState(ShootStates.SPIN_DOWN);
				}
				break;
				
			case SPIN_DOWN:
				if(System.nanoTime() > fireTimer + Vars.SPIN_DOWN_TIME ){
					//motors are stopped in MOVE_TRANSPORT
					setCurrState(ShootStates.MOVE_TRANSPORT);
				}else{
					fireMotor1.set(-1);
					fireMotor2.set(1); //TODO: Check directions/speed
				}
				break;
				
			case MOVE_LOAD:
				if(!beaterBarDeployed.get()){ //TODO: direction
					beaterBarPos.set(-.05);
				}else{
					beaterBarPos.set(0);
					setCurrState(ShootStates.LOAD_BOULDER);
				}
				break;
				
			case LOAD_BOULDER:
				if(!loadedSense.get()){
					beaterBarRoller.set(-.3); //TODO: What Direction?
				}else{
					beaterBarRoller.set(0);
					setCurrState(ShootStates.MOVE_TRANSPORT);
				}
				break;
				
			case MOVE_TRANSPORT:
				//stop motors
				fireMotor1.set(0);
				fireMotor2.set(0);
				
				//fold down beater bar
				if(!beaterBarFolded.get()){
					beaterBarPos.set(.85);
				}else{
					beaterBarPos.set(0);
				}
				
				//move shooter into transport position
				pid.setSetpoint(Vars.SHOOTER_POT_TRANSPORT_POSITION);
				if(pid.onTarget()){
					setCurrState(ShootStates.AWAIT_INPUT);
				}
				break;
			}
		}
	}
	/**
	 * Returns the current State
	 * @return
	 */
	public ShootStates getCurrState() {
		return currState;
	}
	/**
	 * Sets the current state
	 * @param currState
	 */
	public void setCurrState(ShootStates currState) {
		this.currState = currState;
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
	/**
	 * Converts an angle in degrees to a potentiometer value
	 * @param desiredAngle
	 * @return
	 */
	private double convertAngleToPotValues(double desiredAngle){
		return desiredAngle * 1;//TODO: pot range / angle range
	}
	
	public void setShooterSpeed(double speed){
		if(manualMode){
			aimShooter.set(speed);
		}
	}
	
	public void setBeaterBarSpeed(double speed){
		if(manualMode){
			beaterBarPos.set(speed);
		}
	}
	/**
	 * Used to put the state machine into manual mode
	 * @param manualMode
	 */
	public void setManualMode(boolean manualMode) {
		this.manualMode = manualMode;
	}
}
