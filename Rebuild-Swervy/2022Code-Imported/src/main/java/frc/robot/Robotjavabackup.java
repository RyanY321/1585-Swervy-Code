// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;
//package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Hardware;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robotjavabackup extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private static Relay magnetRelay;
  private final int magnetRelayChannel = 0;
  private final int pickupMotorChannel = 0;
  private final int hopperMotorChannel = 1;
  Spark pickupMotor;
  Spark hopperMotor;
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private static final int kRelayForwardButton = 1;
  private static final int kRelayReverseButton = 2;
  private static final int hopperForwardButton = 3;
  private static final int hopperReverseButton = 4;
  private static boolean magnetState = false;
  private UsbCamera startAutomaticCapture;

//  private final AnalogInput ultrasonic = new AnalogInput(0); //the 0 represents the Analog port number we’ve connected our sensor to.

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    // Configure if any encoders are reversed.
    Hardware.leftFrontTurnEncoder.setReverseDirection(false);
    Hardware.rightFrontTurnEncoder.setReverseDirection(false);
    Hardware.rightRearTurnEncoder.setReverseDirection(false);
    Hardware.leftRearTurnEncoder.setReverseDirection(false);

    // Configure if any turn motor is reversed
    Hardware.leftFrontTurn.setInverted(false);
    Hardware.rightFrontTurn.setInverted(false);
    Hardware.rightRearTurn.setInverted(false);
    Hardware.leftRearTurn.setInverted(false);

    // Configure if any drive motor is reversed
    Hardware.leftFrontDrive.setInverted(false);
    Hardware.rightFrontDrive.setInverted(false);
    Hardware.rightRearDrive.setInverted(false);
    Hardware.leftRearDrive.setInverted(false);

    //preparing pickup motor on a Spark and the electromagnet on a Spike relay
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    pickupMotor = new Spark(pickupMotorChannel);
    magnetRelay = new Relay(magnetRelayChannel);
    hopperMotor = new Spark(hopperMotorChannel);
    //initializing the camera server
    startAutomaticCapture = CameraServer.startAutomaticCapture();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {

    // ONLY uncomment for testing purposes. Do NOT reset during a match!
    // I mean it! bad things WILL happen!

     Hardware.leftFrontTurnEncoder.reset();
     Hardware.rightFrontTurnEncoder.reset();
     Hardware.rightRearTurnEncoder.reset();
     Hardware.leftRearTurnEncoder.reset();

   }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
    Hardware.driveSystem.drive(
     Hardware.controller.getLeftY(), 
     Hardware.controller.getLeftX(),
     Hardware.controller.getRightX()
    );
    
    double beltSpeed1 = Hardware.controller2.getRawAxis(2);
    double beltSpeed2 = Hardware.controller2.getRawAxis(3);
    //System.out.println("axis2: " + beltSpeed1 + "\n  axis3: "+ beltSpeed2);
    pickupMotor.set((beltSpeed1 * -1) + beltSpeed2);

    double hopperSpeed = Hardware.controller2.getRawAxis(1);
    hopperMotor.set(hopperSpeed);
    /*
    boolean hopperForward = Hardware.controller.getRawButton(hopperForwardButton);
    boolean hopperReverse = Hardware.controller.getRawButton(hopperReverseButton);
    if(hopperForward)
    {
      hopperMotor.set(1);
      System.out.println("hopper foward");
    }
    else if(hopperReverse)
    {   
      hopperMotor.set(-1);
      System.out.println("hopper reverse");
    }
    else
    {
      hopperMotor.set(0);
      System.out.println("hopper stop");
    }
    */
    boolean forward = Hardware.controller.getRawButton(kRelayForwardButton);

    //boolean reverse = Hardware.controller.getRawButton(kRelayReverseButton);

    if(magnetState & forward)
    {
        System.out.println("magnet disabled");
        magnetState = false;
    }
    else if(!magnetState && forward)
    {
      System.out.println("magnet enabled");
      magnetState = true;
    }

    if(magnetState)
      magnetRelay.set(Relay.Value.kForward);
    else
      magnetRelay.set(Relay.Value.kOff);
    /*
    if (forward && reverse) {
      magnetRelay.set(Relay.Value.kOn);
    } else if (forward) {
      magnetRelay.set(Relay.Value.kForward);
    } else if (reverse) {
      magnetRelay.set(Relay.Value.kReverse);
    } else {
      magnetRelay.set(Relay.Value.kOff);
    }
    */
    // Hardware.leftRearModule.setDirection(90);

    // Hardware.leftFrontTurn.set(ControlMode.PercentOutput, .1);
    // Hardware.rightFrontTurn.set(ControlMode.PercentOutput, .1);
    // Hardware.rightRearTurn.set(ControlMode.PercentOutput, .1);
    // Hardware.leftRearTurn.set(ControlMode.PercentOutput, .1);


    // TESTING PRINT STATEMENTS
    // Uncomment each line to print it's value to the riolog.

    // ========== ENCODERS ===========
     //System.out.println("LF ENC: " + Hardware.leftFrontTurnEncoder.getDistance());
     //System.out.println("RF ENC: " + Hardware.rightFrontTurnEncoder.getDistance());
     //System.out.println("RR ENC: " + Hardware.rightRearTurnEncoder.getDistance());
     //System.out.println("LR ENC: " + Hardware.leftRearTurnEncoder.getDistance());

    // ========= DRIVE MOTORS =========
     //System.out.println("LF DRIVE: " + Hardware.leftFrontDrive.getMotorOutputPercent());
     //System.out.println("RF DRIVE: " + Hardware.rightFrontDrive.getMotorOutputPercent());
     //System.out.println("RR DRIVE: " + Hardware.rightRearDrive.getMotorOutputPercent());
     //System.out.println("LR DRIVE: " + Hardware.leftRearDrive.getMotorOutputPercent());

    // ========= TURN MOTORS ==========
    //  System.out.println("LF TURN: " + Hardware.leftFrontTurn.getMotorOutputPercent());
    //  System.out.println("RF TURN: " + Hardware.rightFrontTurn.getMotorOutputPercent());
    //  System.out.println("RR TURN: " + Hardware.rightRearTurn.getMotorOutputPercent());
    //  System.out.println("LR TURN: " + Hardware.leftRearTurn.getMotorOutputPercent());


     //SONAR
     //double rawValue = ultrasonic.getValue();
     //double currentDistance = rawValue * 0.125;
    // System.out.println("Sonar Distance: " + currentDistance + " inches?");

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
