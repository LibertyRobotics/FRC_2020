package frc.robot.Autonomous.Pathing;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotConstants;
import frc.robot.Hardware.Sensors.NavX;
import frc.robot.Subsystems.DriveTrainSystem;

/**
 * A more specilized drive subsytstem solely for use with the ramsete controller
 */
public class TrajectoryDriveSubsystem extends SubsystemBase{

    DriveTrainSystem drive;

    //Create 2 encoders
    Encoder leftDriveTrainEncoder;
    Encoder rightDriveTrainEncoder;

    // Gyro
    NavX navX;

    //Robots predictive location
    DifferentialDriveOdometry odometry;

    boolean inverted;

    public TrajectoryDriveSubsystem(DriveTrainSystem drive){
        this.drive = drive;

        // Get instances of the dt encoders
        leftDriveTrainEncoder = drive.getLeftSideEncoder();
        rightDriveTrainEncoder = drive.getRightSideEncoder();

        if(inverted){
            
        }

        // Get a reference to the NavX
        navX = NavX.get();

        //Initialize the robot odometry
        odometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(getHeading()));
    }

    @Override
    public void periodic() {

        // If the path is inverted then flip the drive train encoder directions to account 
        if(inverted){
             // Update the odometry in the periodic block
            odometry.update(Rotation2d.fromDegrees(getHeading()), leftDriveTrainEncoder.getDistance()*-1,
            rightDriveTrainEncoder.getDistance()*-1);
        }

        // If not update as normal
        else{
            // Update the odometry in the periodic block
            odometry.update(Rotation2d.fromDegrees(getHeading()), leftDriveTrainEncoder.getDistance(),
                            rightDriveTrainEncoder.getDistance());
        }

        // Print out the current Translation and Rotational values
        //System.out.println("Current Translation: " + getPose().getTranslation());
        //System.out.println("Current Rotation: " + getPose().getRotation());
    }

    /**
     * Set whether or not the path is meant to be run backwards
     * @param value inverted status
     */
    public void setInverted(boolean value){
        inverted = value;
    }

    /**
     * Drive the robot with a tank style drive, but pass voltages to the motor controllers instead of "powers"
     */
    public void tankDriveVolts(double leftVolts, double rightVolts){
        drive.getLeftSideMotors().setVoltage(-leftVolts);
        drive.getRightSideMotors().setVoltage(-rightVolts);

    }

    /**
     * Adds the ability to drive the robot backwards, to run paths in reverse
     * @param leftVolts power to the left side of the drive train
     * @param rightVolts power to the right side of the drive train
     */
    public void inverseTankDriveVolts(double leftVolts, double rightVolts){
        drive.getLeftSideMotors().setVoltage(leftVolts);
        drive.getRightSideMotors().setVoltage(rightVolts);
    }

    /**
     * Reset Encoders
     */
    public void resetEncoders(){
        leftDriveTrainEncoder.reset();
        rightDriveTrainEncoder.reset();
    }

     /**
     * Returns the currently estimated pose of the robot
     * @return the pose
     */
    public Pose2d getPose(){
        return odometry.getPoseMeters();
    }

     /**
     * Gets the currents speeds of each output shaft
     * @return the current wheel speeds
     */
    public DifferentialDriveWheelSpeeds getWheelSpeeds(){
        return new DifferentialDriveWheelSpeeds(leftDriveTrainEncoder.getRate(), rightDriveTrainEncoder.getRate());
    }

    /**
     * Reset the odometry to a know pose
     * @param pose the pose we wish to reset to
     */
    public void resetOdometry(Pose2d pose){
        drive.resetEncoders();
        odometry.resetPosition(pose, Rotation2d.fromDegrees(getHeading()));
    }

    /**
     * Gets modified heading specifically for trajectory planning
     * @return the altered heading
     */
    public double getHeading(){

        // If set to invert flip whatever the current reversed gyro value to flip directions
        if(inverted)
            return Math.IEEEremainder(navX.getAngle(), 360) * (!RobotConstants.kGyroReversed ? -1.0 : 1.0);
        
        //If inverted
        return Math.IEEEremainder(navX.getAngle(), 360) * (RobotConstants.kGyroReversed ? -1.0 : 1.0);
    }
    /**
     * Returns the turn rate factoring in weather or not the gyro is reversed
     */
    public double getTurnRate(){

        // If set to invert flip whatever the current reversed gyro value to flip directions
        if(inverted)
            return navX.getRate() * (!RobotConstants.kGyroReversed ? -1.0 : 1.0);

        return navX.getRate() * (RobotConstants.kGyroReversed ? -1.0 : 1.0);
    }
    

}