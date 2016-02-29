package org.usfirst.frc.team871.robot.subsystems;

import org.usfirst.frc.team871.robot.Vars;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Shooter class used to create a shooter
 * 
 * @author Team871-5
 * 
 */
public class Shooter {

    private SpeedController    aimShooter, fireMotor1, fireMotor2, beaterBarPos, beaterBarRoller;
    private Drive              tankDrive;
    private DoubleSolenoid     firePiston;
    
    private Potentiometer      shooterPot;
    private Potentiometer      beaterBarPot;
    
    private PIDController      pidShooterAngle, pidBeaterBar;
    
    private ShootStates        currState  = ShootStates.MOVE_TRANSPORT;
    //Enabled state of the controlling state machine
    private boolean            enabled    = true;
    private boolean            manualMode = true;
    
    private long               fireTimer    = 0;
    private long               tLastStateChange = 0;
    private final NetworkTable dashboard    = NetworkTable.getTable("SmartDashboard");
    private double             desiredAngle;

    private static final double LOAD_TIMEOUT = 500000000; //Half a second
    
    private static final double SHOOTER_UPPER_ANGLE = 62.5;
    private static final double SHOOTER_LOWER_ANGLE = -16.5;
    private static final double POT_UPPER_VALUE     = .616104;
    private static final double POT_LOWER_VALUE     = .000994;
    
    public static final double BEATER_BAR_POT_LOAD_SETPOINT     = 0.5;

    private static double      POT_TO_ANGLE_VALUE  = Math.abs(SHOOTER_UPPER_ANGLE - SHOOTER_LOWER_ANGLE)
            / Math.abs(POT_UPPER_VALUE - POT_LOWER_VALUE);

    public Shooter(SpeedController aimShooter, SpeedController fireMotor1, SpeedController fireMotor2,
                   SpeedController beaterBarPos, SpeedController beaterBarRoller, DoubleSolenoid firePiston,
                   Potentiometer shooterPot, Potentiometer beaterBarPot, Drive tankDrive) {
        
        this.aimShooter = aimShooter;
        this.fireMotor1 = fireMotor1;
        this.fireMotor2 = fireMotor2;
        this.beaterBarPos = beaterBarPos;
        this.beaterBarRoller = beaterBarRoller;
        this.firePiston = firePiston;
        this.shooterPot = shooterPot;
        this.tankDrive = tankDrive;
        this.beaterBarPot = beaterBarPot;

        pidShooterAngle = new PIDController(-100, 0, 0, this.shooterPot, this.aimShooter);
        pidShooterAngle.setPercentTolerance(1);
        pidShooterAngle.enable();

        pidBeaterBar = new PIDController(50, 0, 0, this.beaterBarPot, this.beaterBarPos);
        pidBeaterBar.setPercentTolerance(1);
        pidBeaterBar.enable();
        pidBeaterBar.disable();

        fireTimer = System.nanoTime();
        firePiston.set(Value.kForward);
    }

    /**
     * Call this method periodically to update the state machine.
     */
    public void update() {
        desiredAngle = dashboard.getNumber("theta", 0.0);
        dashboard.putNumber("shooterAngle", convertPotValuesToAngle(shooterPot.get()));
        dashboard.putNumber("beaterBarPos", beaterBarPot.get());

        if (enabled) {
            switch (currState) {
                case AWAIT_INPUT: // Initial State

                    if (System.nanoTime() > (fireTimer + 500000000)) {
                        firePiston.set(Value.kOff);
                    }
                    else {
                        firePiston.set(Value.kForward);
                    }
                    break;

                case AIM:
                    // don't autoaim if you're in manual mode
                    if (!manualMode) {
                        tankDrive.autoAim();
                        pidShooterAngle.setSetpoint(convertAngleToPotValues(desiredAngle));
                    }

                    if ((Math.abs(pidShooterAngle.getError()) < .05) || manualMode) {
                        fireTimer = System.nanoTime();
                        setCurrState(ShootStates.SPIN_UP);
                    }
                    break;

                case SPIN_UP:
                    if (System.nanoTime() > fireTimer + Vars.SPIN_UP_TIME) {
                        fireTimer = System.nanoTime();
                        setCurrState(ShootStates.FIRE);
                    }
                    else {
                        fireMotor1.set(1);
                        fireMotor2.set(-1);
                    }
                    break;

                case FIRE:
                    if (System.nanoTime() < fireTimer + Vars.SPIN_UP_TIME) {
                        firePiston.set(Value.kReverse);
                    }
                    else {
                        fireTimer = System.nanoTime();
                        firePiston.set(Value.kForward);
                        setCurrState(ShootStates.SPIN_DOWN);
                    }
                    break;

                case SPIN_DOWN:
                    if (System.nanoTime() > fireTimer + Vars.SPIN_DOWN_TIME) {
                        fireMotor1.set(0);
                        fireMotor2.set(0);
                        setCurrState(ShootStates.MOVE_TRANSPORT);
                    }
                    break;

                case LOAD_BOULDER:
                    pidBeaterBar.setSetpoint(BEATER_BAR_POT_LOAD_SETPOINT);
                    pidShooterAngle.setSetpoint(convertAngleToPotValues(SHOOTER_LOWER_ANGLE));
                    
                    //If the PID controllers are in range, spin all the motors up
                    if( (pidBeaterBar.getError() < .001) &&
                        (pidShooterAngle.getError() < .05) ){
                        beaterBarRoller.set(.3);
                        fireMotor1.set(-.2);
                        fireMotor2.set(.2);
                    }
                    
                    //Automatically switch to MOVE_TRANSPORT if we havent been
                    //commanded to this state for a while
                    if(System.nanoTime() > (tLastStateChange + LOAD_TIMEOUT)) {
                        setCurrState(ShootStates.MOVE_TRANSPORT);
                    }
                    
                    break;

                case MOVE_TRANSPORT:
                    pidBeaterBar.setSetpoint(Vars.BEATER_BAR_POT_DEPLOYED_SETPOINT);
                    pidShooterAngle.setSetpoint(convertAngleToPotValues(Vars.SHOOTER_POT_TRANSPORT_POSITION));
                    
                    if (Math.abs(pidShooterAngle.getError()) < .05) {
                        setCurrState(ShootStates.AWAIT_INPUT);
                        fireTimer = System.nanoTime();
                    }
                    break;
            }
        }
    }

    /**
     * Returns the current State
     * 
     * @return
     */
    public ShootStates getCurrState() {
        return currState;
    }

    /**
     * Sets the current state
     * 
     * @param currState
     */
    public void setCurrState(ShootStates currState) {
        this.currState = currState;
        tLastStateChange = System.nanoTime();
    }

    public enum ShootStates {
        AWAIT_INPUT, LOAD_BOULDER, MOVE_TRANSPORT, AIM, SPIN_UP, FIRE, SPIN_DOWN;
    }

    /**
     * Converts an angle in degrees to a potentiometer value
     * 
     * @param desiredAngle
     * @return
     */
    private double convertAngleToPotValues(double desiredAngle) {
        // desiredAngle = SHOOTER_UPPER_ANGLE - (desiredAngle);
        return POT_UPPER_VALUE - (((desiredAngle - SHOOTER_LOWER_ANGLE) / POT_TO_ANGLE_VALUE) + POT_LOWER_VALUE);

    }

    private double convertPotValuesToAngle(double potValues) {
        potValues = POT_UPPER_VALUE - (potValues);

        return (((potValues - POT_LOWER_VALUE) * POT_TO_ANGLE_VALUE) + SHOOTER_LOWER_ANGLE);
    }

    public void setShooterSpeed(double speed) {
        if (manualMode) {
            aimShooter.set(speed);
        }
    }

    public void setBeaterBarRollSpeed(double speed) {
        if (manualMode) {
            beaterBarRoller.set(speed);
        }
    }

    public void setBeaterBarSpeed(double speed) {
        if (manualMode) {
            beaterBarPos.set(speed);
        }
    }

    /**
     * Used to put the state machine into manual mode
     * 
     * @param manualMode
     */
    public void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
        if (manualMode) {
            pidShooterAngle.disable();
            pidBeaterBar.disable();
        }
        else {
            pidShooterAngle.enable();
            //pidBeaterBar.enable();
        }
    }

    public boolean isManualControl() {
        return manualMode;
    }
}
