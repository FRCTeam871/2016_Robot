package org.usfirst.frc.team871.robot;

import org.usfirst.frc.team871.robot.Logitech.ButtonType;
import org.usfirst.frc.team871.robot.XBoxController.Axes;
import org.usfirst.frc.team871.robot.XBoxController.Buttons;

public class Vars {

	public static final int SPIN_UP_TIME               = 2000000;
	public static final int SPIN_DOWN_TIME             = 2000000;
	
	public static final int BEATER_BAR_LOAD_POINT      = -50;  		//TODO
	public static final int BEATER_BAR_TRANSPORT_POINT = -50;   	//TODO
	
	public static final int SHOOTER_LOAD_POINT         = -50;		//TODO
	public static final int SHOOTER_TRANSPORT_POINT    = -50;       //TODO
	
	public static final double DEFAULT_AXIS_DEADBAND   = 0.1;
	
	//Ports
	
	//Talons
	public static final int DRIVE_LEFT_PORT        = -50;  		//TODO
	public static final int DRIVE_RIGHT_PORT       = -50;  		//TODO
	public static final int WINCH_PORT             = -50;  		//TODO
	public static final int TELESCOPE_PORT         = -50;       //TODO
	public static final int FIRE_MOTOR_1_PORT      = -50;  		//TODO
	public static final int FIRE_MOTOR_2_PORT      = -50;  		//TODO
	public static final int BEATER_BAR_POS_PORT    = -50;  		//TODO
	public static final int BEATER_BAR_ROLLER_PORT = -50;  		//TODO
	public static final int SHOOTER_AIM_PORT       = -50;  		//TODO

	//Solenoids
	public static final int LIFT_PISTON_FORWARD_PORT       = -50;	//TODO
	public static final int LIFT_PISTON_REVERSE_PORT       = -50;
	public static final int FIRE_PISTON_FORWARD_PORT       = -50;
	public static final int FIRE_PISTON_REVERSE_PORT       = -50;
	public static final int LOCK_SOLENOID_FORWARD_PORT	   = -50;
	public static final int LOCK_SOLENOID_REVERSE_PORT     = -50;
	
	//DigitalInputs
	public static final int GRAB_SENSE_PORT                       = -50;  		//TODO
	public static final int TELESCOPE_UPPER_LIMIT_SENSE_PORT      = -50;  		//TODO
	public static final int TELESCOPE_LOWER_LIMIT_SENSE_PORT      = -50;  		//TODO
	public static final int LOADED_SENSE_PORT                     = -50;  		//TODO
	public static final int ARM_DEPLOYED_SENSE_PORT				  = -50;
	public static final int SHOOTER_UPPER_LIMIT_PORT			  = -50;
	public static final int SHOOTER_LOWER_LIMIT_PORT			  = -50;
	
	//Joysticks
	public static final int JOYSTICK_1_PORT        = -50;  		//TODO
	public static final int JOYSTICK_2_PORT        = -50;  		//TODO
	public static final int XBOX_JOYSTICK_PORT     = -50;		//TODO
	
	//Controls
	public static final Buttons LIFTER_ADVANCE_BUTTON = Buttons.A; //TODO
	public static final Buttons LIFTER_ABORT_BUTTON   = Buttons.X;  //TODO
	
	//Encoder
	public static final int LIFT_ENCODER_PORT_A      = -50;
	public static final int LIFT_ENCODER_PORT_B      = -50;
	public static final int TELESCOPE_ENCODER_PORT_A = -50;
	public static final int TELESCOPE_ENCODER_PORT_B = -50;
	
	//Potentiometer
	public static final int SHOOTER_POTENTIOMETER_PORT        = -50;
	public static final double SHOOTER_POT_TRANSPORT_POSITION = -50;
	
}
