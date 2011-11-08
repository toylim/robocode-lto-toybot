package lto;

import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotStatus;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.RobotDeathEvent;
import robocode.util.Utils;
import java.util.List;
import java.util.ArrayList;

/**
 * Implements the ToyBot robot, which completes a circuit around the arena.
 * 
 * @author Toy Lim
 */
public class ToyBot extends AdvancedRobot {
  private static final double RADARMAXTURN = 45.0
  private static final double RADARMINTURN = 15.0;
  //private static final double GUNMAXTURN = 20.0;
  private RobotStatus status;
  private double lastD, deltaD;
  private double radarBearing, gunBearing;
  private List<EnemyBot> targets;
  private EnemyBot currentTarget;
  private int reverse;
  private double gutter;
  private long lastT;

  /**
   * called every time another robot is seen.
   * 
   * @param event ScannedRobotEvent
   */
  @Override
  public void onScannedRobot(ScannedRobotEvent event) {
    EnemyBot target;
    int index = 0;
    String name = event.getName();
    // search for matching target in List, assuming List was initialized and not empty
    do {
      target = targets.get(index);
      index++;
    }
    while (!name.equals(target.getName()) && !target.none() && index < targets.size());
    // add or update ScannedRobotEvent to List
    if (target.none() || name.equals(target.getName())) {
      target.update(event, status);
      out.println("Target #" + index + " is updated.");
      if (currentTarget == null || !currentTarget.isAlive()) {
        currentTarget = target;
      }
    }
    else {
      out.println("Error with List: targets");
    }
    out.println("Saw target at " + getRadarHeading() + " degrees ("
        + Utils.normalAbsoluteAngleDegrees(event.getBearing() + getHeading()) + ")");

    if (name.equals(currentTarget.getName())) {
      deltaD = target.getDistance() - lastD;
      lastD = target.getDistance();
      radarBearing = minTurn(getRadarHeading(), target.getFutureAbsoluteBearing(1, getX(), getY()));
      out.println("Correction angle is " + radarBearing);
      if (Math.abs(radarBearing) < RADARMINTURN) {
        if (radarBearing < 0) {
          radarBearing = -RADARMINTURN;
        }
        else {
          radarBearing = RADARMINTURN;
        }
      }
      long when = target.predictHit(status);
      gunBearing = minTurn(getGunHeading(), target.getFutureAbsoluteBearing(when, getX(), getY()));
      if (gunBearing < 1 && (target.getDistance() < (getBattleFieldWidth() / 2))) {
        setFire(-0.003 * target.getDistance() + 3.1083);
      }
    }
    setTurnRadarRight(radarBearing);
    setTurnGunRight(gunBearing);
  }

  /**
   * Pick one enemy and kill them, while at the same time move in a circular path.
   */
  @Override
  public void run() {
    gutter = Math.hypot(getWidth() / 2, getHeight() / 2) + 8;
    radarBearing = RADARMAXTURN;
    gunBearing = 0;
    lastT = -8;
    lastD = deltaD = 0;
    reverse = 1;
    int targetSize = getOthers();
    targets = new ArrayList<EnemyBot>(targetSize);
    for (int i = 0; i < targetSize; i++) {
      targets.add(new EnemyBot());
    }
    setAdjustGunForRobotTurn(true);
    setAdjustRadarForRobotTurn(true);
    setAdjustRadarForGunTurn(true);
    setTurnRadarRight(360);
    while (true) {
      setAhead(200 * reverse);
      setTurnRight(30);
      setTurnRadarRight(radarBearing);
      setTurnGunRight(gunBearing);
      execute();
    }
  }

  /**
   * called every turn to update status.
   * 
   * @param e StatusEvent
   */
  @Override
  public void onStatus(StatusEvent e) {
    status = e.getStatus();
    if ((e.getTime() % 500) == 0) {
      setTurnRadarRight(360);
    }
    if ((e.getTime() % 50) == 49 && deltaD < 0 && (e.getTime() - lastT) > 16) {
      reverse = -reverse;
      lastT = e.getTime();
    }
    if ((Math.abs(getBattleFieldWidth() - status.getX()) < gutter
        || Math.abs(status.getX()) < gutter
        || Math.abs(getBattleFieldHeight() - status.getY()) < gutter 
        || Math.abs(status.getY()) < gutter)
        && (e.getTime() - lastT) > 8) {
      reverse = -reverse;
      lastT = e.getTime();

    }
  }

  /**
   * called whenever a robot die.
   * 
   * @param e RobotDeathEvent
   */
  @Override
  public void onRobotDeath(RobotDeathEvent e) {
    String name = e.getName();
    for (EnemyBot target : targets) {
      if (name.equals(target.getName())) {
        target.kill();
      }
    }
  }

  /**
   * called whenever robot hit a wall.
   * 
   * @param e HitWallEvent
   */
  @Override
  public void onHitWall(HitWallEvent e) {
    reverse = -reverse;
    lastT = e.getTime();
  }

  /**
   * called whenever robot hit another robot.
   * 
   * @param e HitRobotEvent
   */
  @Override
  public void onHitRobot(HitRobotEvent e) {
    reverse = -reverse;
    lastT = e.getTime();
  }

  /**
   * Calculate the minimum angle and turn direction needed to achieve a turn.
   * 
   * @param fromAngle the starting angle in degrees
   * @param toAngle the desired ending angle in degrees
   * @return the optimal turn angle to use [-180 degrees to 180 degrees]
   */
  public static double minTurn(double fromAngle, double toAngle) {
    return Utils.normalRelativeAngleDegrees(toAngle - fromAngle);
  }
}
