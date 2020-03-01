package frc.robot.Autonomous.Basic;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Autonomous.Control.AutoDriveControl;
import frc.robot.Hardware.Sensors.NavX;
import frc.robot.Subsystems.ArcShooter;
import frc.robot.Subsystems.BallSystem;
import frc.robot.Utilities.Control.LimelightAlignment;

/**
 * Basic auto holder for running autos
 * 
 * @author Will Richards
 */
public class BasicAuto{

    LimelightAlignment alignment;
    AutoDriveControl autoDriveControl;

    ArcShooter shooter;
    BallSystem ballSystem;

    // Basic Back Auto
    private boolean hasBackedUp = false;
    private boolean hasAligned = false;
    private int alignCount = 0;

    // 5 Ball Rendezvous Zone Auto
    private boolean hasDroppedIntake = false;
    private boolean hasRaisedIntake = false;
    private boolean hasTurned = false;

    private Timer shooterTimeout;
    

    public BasicAuto(LimelightAlignment alignment, AutoDriveControl autoDriveControl, ArcShooter shooter, BallSystem ballSystem){
        this.alignment = alignment;
        this.autoDriveControl = autoDriveControl;

        this.shooter = shooter;
        this.ballSystem = ballSystem;
    }

    /**
     * Init and reset values
     */
    public void runBasicBackStartup(){
        hasBackedUp = false;
        hasAligned = false;
        alignCount = 0;
        shooterTimeout.reset();
    }

    /**
     * Run the basic 3 path
     */
    public void runBasicBack(){
        if(!hasBackedUp)
        hasBackedUp = autoDriveControl.DriveDistance(-2.1);
       else if(!hasAligned){
        if(alignment.controlLoop())
          alignCount++;
        else{
          alignCount--;
        }
    
        if(alignCount>=7)
          hasAligned = true;
       }
       else if(hasAligned){
         shooter.enableShooter();
         if(shooter.isFull()){
           ballSystem.getIndexer().standardIndex();
           if(shooterTimeout.get() <= 0)
            shooterTimeout.start();
         }
       }
       else if(shooterTimeout.get() > 5){
         shooter.stopShooter();
         ballSystem.getIndexer().stopIndexing();
       }
    
       shooter.runShooter();
    }

    /**
     * Setup for rendezvous
     */
    public void runRendezvousFiveSetup(){
        hasBackedUp = false;
        hasDroppedIntake = false;
        hasRaisedIntake = false;
        hasAligned = false;
        alignCount = 0;
        NavX.get().reset();
        shooterTimeout.reset();
    }

    /**
     * Drive pickup 2 from randezvous and then shoot 5
     */
    public void runRendezvousFive(){
        if(!hasDroppedIntake){
            ballSystem.getIntake().extendIntake();
            ballSystem.getIntake().runFrontIntakeForward();
            hasDroppedIntake = true;
        }
        else if(!hasBackedUp)
            hasBackedUp = autoDriveControl.DriveDistance(2.3876, 0.35);
        else if(!hasRaisedIntake){
            ballSystem.getIntake().retractIntake();
            ballSystem.getIntake().stopFrontIntake();
            hasRaisedIntake = true;
        }
        else if(!hasTurned)
            hasTurned = autoDriveControl.TurnToAngle(180);
        else if(!hasAligned){
            if(alignment.controlLoop())
                alignCount++;
            else
                alignCount--;

            if(alignCount>=7)
                hasAligned = true;
            
        }
        else if(hasAligned){
            shooter.enableShooter();
            if(shooter.isFull()){
              ballSystem.getIndexer().standardIndex();
              if(shooterTimeout.get() <= 0)
               shooterTimeout.start();
            }
          }
          else if(shooterTimeout.get() > 5){
            shooter.stopShooter();
            ballSystem.getIndexer().stopIndexing();
          }
       
          shooter.runShooter();

            
        
    }
}