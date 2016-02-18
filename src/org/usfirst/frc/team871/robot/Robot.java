
//This code is copied directly from undertale.
package org.usfirst.frc.team871.robot;

import org.usfirst.frc.team871.robot.Logitech.AxisType;
import org.usfirst.frc.team871.robot.Logitech.ButtonType;
import org.usfirst.frc.team871.robot.Shooter.ShootStates;
import org.usfirst.frc.team871.robot.XBoxController.Axes;
import org.usfirst.frc.team871.robot.XBoxController.Buttons;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
	
    public final NetworkTable dashboard = NetworkTable.getTable("SmartDashboard");
    
    Drive tankDrive;
    Shooter shoot;
    Lifter lift;
    
    Compressor airCompressor;
    
    Logitech stickJoy;
	Logitech stickJoy2;
	
	XBoxController xbox;
	
	LimitedSpeedController aimShooter, beaterBarPos;
    
    SpeedController fireMotor1, fireMotor2, beaterBarRoller, 
    				 driveL, driveR, winch, telescopeMotor;
    
    DigitalInput loadedSense, grabSense, beaterBarDeployed, beaterBarFolded, armDeployedSense, 
    			 shooterUpperLimit, shooterLowerLimit;
    
    DoubleSolenoid firePiston, liftPiston, lockSolenoid;
    Encoder liftEncoder, telescopeEncoder;
    Potentiometer shooterPot;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        airCompressor   = new Compressor();  
        driveL          = new CANTalon(Vars.DRIVE_LEFT_PORT);
        driveL.setInverted(true);
        driveR          = new CANTalon(Vars.DRIVE_RIGHT_PORT);
	    fireMotor1      = new CANTalon(Vars.FIRE_MOTOR_1_PORT);
	    fireMotor2      = new CANTalon(Vars.FIRE_MOTOR_2_PORT);
	    beaterBarRoller = new Talon(Vars.BEATER_BAR_ROLLER_PORT);
	    winch           = new CANTalon(Vars.WINCH_PORT);
        telescopeMotor  = new CANTalon(Vars.TELESCOPE_PORT);
	    
	    
	    //Sensors
	    stickJoy  = new Logitech(Vars.JOYSTICK_1_PORT);
        stickJoy2 = new Logitech(Vars.JOYSTICK_2_PORT);
        
        xbox      = new XBoxController(Vars.XBOX_JOYSTICK_PORT);
        
        liftPiston   = new DoubleSolenoid(Vars.LIFT_PISTON_FORWARD_PORT, Vars.LIFT_PISTON_REVERSE_PORT);
        firePiston   = new DoubleSolenoid(Vars.FIRE_PISTON_FORWARD_PORT, Vars.FIRE_PISTON_REVERSE_PORT);
        lockSolenoid = new DoubleSolenoid(Vars.LOCK_SOLENOID_FORWARD_PORT, Vars.LOCK_SOLENOID_REVERSE_PORT);
        
        grabSense                = new DigitalInput(Vars.GRAB_SENSE_PORT);
        loadedSense              = new DigitalInput(Vars.LOADED_SENSE_PORT);
        armDeployedSense         = new DigitalInputActiveLow(Vars.ARM_DEPLOYED_SENSE_PORT);
        shooterUpperLimit        = new DigitalInput(Vars.SHOOTER_UPPER_LIMIT_PORT);
        shooterLowerLimit        = new DigitalInput(Vars.SHOOTER_LOWER_LIMIT_PORT);
        beaterBarDeployed		 = new DigitalInputActiveLow(Vars.BEATER_BAR_DEPLOYED_PORT);
        beaterBarFolded			 = new DigitalInputActiveLow(Vars.BEATER_BAR_FOLDED_PORT);
        
        aimShooter      = new LimitedSpeedController(shooterUpperLimit, shooterLowerLimit, new CANTalon(Vars.SHOOTER_AIM_PORT));
        beaterBarPos    = new LimitedSpeedController(beaterBarDeployed, beaterBarFolded,   new Talon(Vars.BEATER_BAR_POS_PORT));
        
        liftEncoder      = new Encoder(Vars.LIFT_ENCODER_PORT_A, Vars.LIFT_ENCODER_PORT_B);
        telescopeEncoder = new Encoder(Vars.TELESCOPE_ENCODER_PORT_A, Vars.TELESCOPE_ENCODER_PORT_B);
        
        shooterPot = new AnalogPotentiometer(Vars.SHOOTER_POTENTIOMETER_PORT);
        
        tankDrive = new Drive(driveL, driveR);
        
        lift  = new Lifter(telescopeMotor, liftPiston, grabSense, armDeployedSense, lockSolenoid, telescopeEncoder, winch, xbox, beaterBarPos);
        shoot = new Shooter(aimShooter, fireMotor1, fireMotor2, beaterBarPos, beaterBarRoller, firePiston, shooterPot, loadedSense, beaterBarDeployed, beaterBarFolded, tankDrive, shooterUpperLimit, shooterLowerLimit);
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = customAuto;//(String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
		
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
    		double tBefore = System.currentTimeMillis();
    		double runTime = 0;
    		
    		if(runTime > 3000){
    			tankDrive.driveBothMotors(0, 0);
    			if(shoot.getCurrState() != ShootStates.AIM){
    				shoot.setCurrState(ShootStates.AIM);
    			}
    			
    		}else{
    			tankDrive.driveBothMotors(1, 1);
    		}
    		
    		shoot.update();
    		
    		runTime += System.currentTimeMillis() - tBefore;
    		
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
    	}
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	dashboard.putString("shootState", shoot.getCurrState().name());
    	dashboard.putString("lifterState", lift.getCurrState().name());
    	dashboard.putBoolean("manualMode", shoot.manualMode);
    	airCompressor.start();
    	//Drive control
    	double axis1 = stickJoy.getDeadAxis(AxisType.Y);
    	double axis2 = stickJoy2.getDeadAxis(AxisType.Y);
    	
    	tankDrive.driveBothMotors(axis1, axis2);
    	
    	
    	/*
    	 * user inputs
    	 * switch between automatic and manual modes
    	 * automatic mode: shooter and beater bar are controlled by the state machine
    	 */
    	if(xbox.isToggled(Vars.MANUAL_MODE_TOGGLE_BUTTON)){
    		shoot.setManualMode(false);
    		
    		if(stickJoy.getRisingEdge(Vars.LOAD_BUTTON)){
        		shoot.setCurrState(ShootStates.MOVE_LOAD);
        	}
        	
        	if(stickJoy.getRisingEdge(Vars.FIRE_BUTTON) && loadedSense.get()){
        		shoot.setCurrState(ShootStates.AIM);
        	}
    	}else{
    		//manual mode: shooter and beater bar are controlled by xbox controller
    		shoot.setManualMode(true);
    		
    		
    		double shooterSpeed = xbox.getAxisValue(Vars.SHOOTER_RAISE_AXIS_MANUAL) - xbox.getAxisValue(Vars.SHOOTER_LOWER_AXIS_MANUAL);
    		shoot.setShooterSpeed(shooterSpeed);
    		
    		double beaterBarSpeed = xbox.getAxisValue(Vars.BEATER_BAR_MANUAL_CONTROL);
    		shoot.setBeaterBarSpeed(beaterBarSpeed * .5);
    		
    		if(stickJoy.getRisingEdge(Vars.FIRE_BUTTON)){
        		shoot.setCurrState(ShootStates.AIM);
        	}
    		
    	
    		
    	}
    	
    	//state machines
    	lift.doAuto();
    	shoot.update();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	
    }
    
    public void disabledInit(){
    	airCompressor.stop();
    }
    
}
