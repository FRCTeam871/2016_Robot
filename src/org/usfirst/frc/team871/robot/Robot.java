
package org.usfirst.frc.team871.robot;

import org.usfirst.frc.team871.robot.Logitech.AxisType;
import org.usfirst.frc.team871.robot.Logitech.ButtonType;
import org.usfirst.frc.team871.robot.Shooter.ShootStates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
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
	
    private Drive tankDrive;
    
    public static Logitech stickJoy, stickJoy2;
    
    private SpeedController aimShooter, fireMotor1, fireMotor2, beaterBarPos1, beaterBarPos2, beaterBarRoller, winch, driveL, driveR;
    
    private Shooter shoot;
    
    private DigitalInput loadSense, armUpSense, armDownSense, grabSense;
    
    private Solenoid firePiston, liftPiston;
    
    private Lifter lift;
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        stickJoy  = new Logitech(Vars.JOYSTICK_1_PORT);
        stickJoy2 = new Logitech(Vars.JOYSTICK_2_PORT);
        
        winch           = new Talon(Vars.WINCH_PORT);
        driveL          = new Talon(Vars.DRIVE_LEFT_PORT);
        driveR          = new Talon(Vars.DRIVE_RIGHT_PORT);
	    aimShooter      = new Talon(Vars.SHOOTER_AIM_PORT);
	    fireMotor1      = new Talon(Vars.FIRE_MOTOR_1_PORT);
	    fireMotor2      = new Talon(Vars.FIRE_MOTOR_2_PORT);
	    beaterBarPos1   = new Talon(Vars.BEATER_BAR_POS_1_PORT);
	    beaterBarPos2   = new Talon(Vars.BEATER_BAR_POS_2_PORT);
	    beaterBarRoller = new Talon(Vars.BEATER_BAR_ROLLER_PORT);
        
        liftPiston = new Solenoid(Vars.LIFT_PISTON_PORT);
        firePiston = new Solenoid(Vars.FIRE_PISTON_PORT);
        
        grabSense  = new DigitalInput(Vars.GRAB_SENSE_PORT);
        armUpSense = new DigitalInput(Vars.ARM_UP_SENSE_PORT);
        armDownSense = new DigitalInput(Vars.ARM_DOWN_SENSE_PORT); //TODO: Get this on robot
        loadSense  = new DigitalInput(Vars.LOADED_SENSE_PORT);
        
        tankDrive = new Drive(driveL, driveR);
        
        lift = new Lifter(winch, liftPiston, grabSense, armUpSense);
        
        shoot = new Shooter(aimShooter, fireMotor1, fireMotor2, beaterBarPos1, beaterBarPos2, beaterBarRoller, firePiston);
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
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
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
    	
    	double axis = stickJoy.getDeadAxis(AxisType.Y);
    	
    	tankDrive.driveBothMotors(axis, axis);
    	
    	lift.doAuto();
    	
    	if(stickJoy.getRisingEdge(ButtonType.TWO)){
    		Shooter.setCurrState(ShootStates.MOVE_LOAD);
    	}
    	
    	if(stickJoy.getRisingEdge(ButtonType.ONE) && !loadSense.get()){ //TODO: Is it active low?
    		Shooter.setCurrState(ShootStates.AIM);
    	}
    	
    	shoot.update();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	
    }
    
}
