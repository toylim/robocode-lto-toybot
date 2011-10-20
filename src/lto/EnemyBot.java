package lto;

//import robocode.StatusEvent;
import robocode.ScannedRobotEvent;
import robocode.RobotStatus;
import robocode.util.Utils;
//import robocode.AdvancedRobot;

/**
 * Helper class for enemy tracking.
 * 
 * @author Toy Lim
 */
public class EnemyBot {
  private static final double ROBOTMAXV = 8.0;
  private static final double ROBOTMAXTURN = 10.0;
  private static final double GUNMAXTURN = 20.0;
  //private static final double BULLETMAXV = 19.7;
  private static final double BULLETMINV = 11.0;
  //private static final double ROBOTWIDTH = 36.0;
  private static final double FIELDWIDTH = 800;
  private static final double FIELDHEIGHT = 600;
  private double bearing, distance, energy, heading, velocity;
  private String name;
  private long time;
  private double x, y, deltaV, deltaA, deltaT;
  private boolean alive;

  /**
   * Construct an empty enemy robot tracker.
   */
  public EnemyBot() {
    reset();
  }

  /**
   * Construct an enemy robot tracker using ScannedRobotEvent data.
   * 
   * @param e ScannedRobotEvent data used to update the tracker
   */
  public EnemyBot(ScannedRobotEvent e) {
    bearing = e.getBearing();
    distance = e.getDistance();
    energy = e.getEnergy();
    time = e.getTime();
    velocity = e.getVelocity();
    heading = e.getHeading();
    name = e.getName();
    x = y = deltaV = deltaA = deltaT = 0.0;
    alive = true;
  }

  /**
   * Construct an enemy robot tracker using ScannedRobotEvent data and RobotStatus.
   * 
   * @param e ScannedRobotEvent data used to update the tracker
   * @param s RobotStatus data used to assist the calculation of target position
   */
  public EnemyBot(ScannedRobotEvent e, RobotStatus s) {
    bearing = e.getBearing();
    distance = e.getDistance();
    energy = e.getEnergy();
    time = e.getTime();
    velocity = e.getVelocity();
    heading = e.getHeading();
    name = e.getName();
    // calculate position
    x = s.getX() + distance * Math.sin(Math.toRadians(s.getHeading() + bearing));
    y = s.getY() + distance * Math.cos(Math.toRadians(s.getHeading() + bearing));
    deltaV = deltaA = deltaT = 0.0;
    alive = true;
  }

  /**
   * Implement the logical equality comparison.
   * 
   * @param aOther the Object being compared to
   * @return true if equal; false otherwise
   */
  @Override
  public boolean equals(Object aOther) {
    // check identity
    if (this == aOther) {
      return true;
    }
    // check instance
    if (!(aOther instanceof EnemyBot)) {
      return false;
    }
    // cast to native object is now safe
    EnemyBot other = (EnemyBot) aOther;
    // native object compare
    if (name.equals(other.getName())) {
      return true;
    }
    return false;
  }

  /**
   * Return the hashCode for the EnemyBot.
   * 
   * @return integer hashCode
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /**
   * Update enemy robot tracker using ScannedRobotEvent data and RobotStatus.
   * 
   * @param e ScannedRobotEvent data used to update the tracker
   * @param s RobotStatus data used to assist the calculation of target position
   */
  public void update(ScannedRobotEvent e, RobotStatus s) {
    bearing = e.getBearing();
    distance = e.getDistance();
    energy = e.getEnergy();
    // calculate time difference since last update
    deltaT = e.getTime() - time;
    time = e.getTime();
    // calculate rate of change in velocity
    deltaV = (e.getVelocity() - velocity) / deltaT;
    velocity = e.getVelocity();
    // calculate rate of change in heading
    deltaA = (e.getHeading() - heading) / deltaT;
    heading = e.getHeading();
    // calculate position
    x = s.getX() + distance * Math.sin(Math.toRadians(s.getHeading() + bearing));
    y = s.getY() + distance * Math.cos(Math.toRadians(s.getHeading() + bearing));
    // assumes caller checked the name for matching update
    name = e.getName();
    alive = true;
  }

  /**
   * Reset the enemy robot tracker.
   */
  public final void reset() {
    bearing = distance = energy = heading = velocity = 0.0;
    name = "";
    time = 0;
    x = y = deltaV = deltaA = deltaT = 0.0;
    alive = true;
  }

  /**
   * Check if the enemy robot tracker was reset.
   * 
   * @return true if the tracker is in reset state, false otherwise
   */
  public boolean none() {
    return "".equals(name);
  }

  /**
   * Check if the enemy robot tracker is alive.
   * 
   * @return true if the tracker is not dead
   */
  public boolean isAlive() {
    return alive;
  }

  /**
   * Update the alive status to false.
   */
  public void kill() {
    alive = false;
  }

  /**
   * Get enemy robot bearing.
   * 
   * @return enemy robot's bearing relative to the robot's heading
   */
  public double getBearing() {
    return bearing;
  }

  /**
   * Get enemy robot distance.
   * 
   * @return enemy robot's distance relative to the robot's position
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Get enemy robot's current energy reading.
   * 
   * @return enemy robot's energy
   */
  public double getEnergy() {
    return energy;
  }

  /**
   * Get enemy robot's heading direction.
   * 
   * @return enemy robot's heading direction in degrees
   */
  public double getHeading() {
    return heading;
  }

  /**
   * Get enemy robot's name.
   * 
   * @return enemy robot's name
   */
  public String getName() {
    return name;
  }

  /**
   * Get enemy robot's velocity.
   * 
   * @return enemy robot's velocity
   */
  public double getVelocity() {
    return velocity;
  }

  /**
   * Get enemy robot's x coordinate.
   * 
   * @return enemy robot's x coordinate
   */
  public double getX() {
    return x;
  }

  /**
   * Get enemy robot's y coordinate.
   * 
   * @return enemy robot's y coordinate
   */
  public double getY() {
    return y;
  }

  /**
   * Predict enemy robot's future x coordinate.
   * 
   * @param when number of turns into the future to predict for
   * @return predicted x coordinate
   */
  public double getFutureX(long when) {
    double futureVelocity, futureHeading;
    double delta = 0;
    futureVelocity = velocity;
    futureHeading = heading;
    for (int i = 0; i < when; i++) {
      if (Math.abs(futureVelocity) < ROBOTMAXV) {
        futureVelocity += deltaV;
      }
      if (Math.abs(deltaA) < ROBOTMAXTURN) {
        futureHeading = (futureHeading + deltaA) % 360;
      }
      delta += futureVelocity * Math.sin(Math.toRadians(futureHeading));
    }
    return (x + delta) >= FIELDWIDTH ? FIELDWIDTH : (x + delta);
  }

  /**
   * Predict enemy robot's future y coordinate.
   * 
   * @param when number of turns into the future to predict for
   * @return predicted y coordinate
   */
  public double getFutureY(long when) {
    double futureVelocity, futureHeading;
    double delta = 0;
    futureVelocity = velocity;
    futureHeading = heading;
    for (int i = 0; i < when; i++) {
      if (Math.abs(futureVelocity) < ROBOTMAXV) {
        futureVelocity += deltaV;
      }
      if (Math.abs(deltaA) <= ROBOTMAXTURN) {
        futureHeading = (futureHeading + deltaA) % 360;
      }
      delta += futureVelocity * Math.cos(Math.toRadians(futureHeading));
    }
    return (y + delta) >= FIELDHEIGHT ? FIELDHEIGHT : (y + delta);
  }

  /**
   * Predict future distance to enemy robot.
   * 
   * @param when number of turns into the future to predict for
   * @param fromX x coordinate of the robot
   * @param fromY y coordinate of the robot
   * @return predicted distance
   */
  public double getFutureDistance(long when, double fromX, double fromY) {
    double futureX, futureY, offsetX, offsetY;
    double futureVelocity, futureHeading;
    futureX = x;
    futureY = y;
    futureVelocity = velocity;
    futureHeading = heading;
    for (int i = 0; i < when; i++) {
      if (Math.abs(futureVelocity) < ROBOTMAXV) {
        futureVelocity += deltaV;
      }
      if (Math.abs(deltaA) <= ROBOTMAXTURN) {
        futureHeading = (futureHeading + deltaA) % 360;
      }
      futureX += futureVelocity * Math.sin(Math.toRadians(futureHeading));
      futureY += futureVelocity * Math.cos(Math.toRadians(futureHeading));
    }
    futureX = futureX >= FIELDWIDTH ? FIELDWIDTH : futureX;
    futureY = futureY >= FIELDHEIGHT ? FIELDHEIGHT : futureY;
    offsetX = futureX - fromX;
    offsetY = futureY - fromY;
    return Math.hypot(offsetX, offsetY);
  }

  /**
   * Predict future absolute bearing to enemy robot.
   * 
   * @param when number of turns into the future to predict for
   * @param fromX x coordinate of the robot
   * @param fromY y coordinate of the robot
   * @return predicted absolute bearing
   */
  public double getFutureAbsoluteBearing(long when, double fromX, double fromY) {
    double futureX, futureY, futureBearing;
    double futureVelocity, futureHeading;
    double offsetX, offsetY, hypot, arcSine;
    futureX = x;
    futureY = y;
    System.out.println("X: " + futureX + " Y: " + futureY);
    futureVelocity = velocity;
    futureHeading = heading;
    for (int i = 0; i < when; i++) {
      if (Math.abs(futureVelocity) < ROBOTMAXV) {
        futureVelocity += deltaV;
      }
      if (Math.abs(deltaA) <= ROBOTMAXTURN) {
        futureHeading = (futureHeading + deltaA) % 360;
      }
      futureX += futureVelocity * Math.sin(Math.toRadians(futureHeading));
      futureY += futureVelocity * Math.cos(Math.toRadians(futureHeading));
    }
    futureX = futureX >= FIELDWIDTH ? FIELDWIDTH : futureX;
    futureY = futureY >= FIELDHEIGHT ? FIELDHEIGHT : futureY;
    offsetX = futureX - fromX;
    offsetY = futureY - fromY;
    hypot = Math.hypot(offsetX, offsetY);
    // Math error detection, return last known bearing
    if (hypot <= 0 || Math.abs(offsetY / hypot) > 1) {
      System.out.println("Error in getFutureAbsoluteBearing()");
      return Utils.normalAbsoluteAngleDegrees(heading + bearing);
    }
    arcSine = Math.toDegrees(Math.asin(offsetY / hypot));
    System.out.println("arcSine is: " + arcSine + " Distance: " + hypot + " dY: " + offsetY);
    if (offsetX < 0) {
      futureBearing = 270 + arcSine;
    }
    else {
      futureBearing = 90 - arcSine;
    }
    return Utils.normalAbsoluteAngleDegrees(futureBearing);
  }

  /**
   * Predict how long it will take to hit the enemy.
   * 
   * @return number of turn a bullet will take to hit the enemy's current position
   */
  public long predictHit() {
    return (long) Math.ceil(distance / BULLETMINV);
  }

  /**
   * Predict how long it will take to hit the enemy, given the robot's gun heading.
   * 
   * @param s RobotStatus used to calculate additional gun turning time
   * @return number of turn it will take to turn gun, fire, and hit the enemy's current position
   */
  public long predictHit(RobotStatus s) {
    long when = 0;
    double absoluteBearing = Utils.normalAbsoluteAngleDegrees(bearing + s.getHeading());
    when +=
        (long) Math.ceil(Math.abs(Utils.normalRelativeAngleDegrees(absoluteBearing
            - s.getGunHeading())
            / GUNMAXTURN));
    when += Math.floor(distance / BULLETMINV);
    return when;
  }
}
