**ToyBot** is a Robocode robot package

# Introduction #

**ToyBot** is a Robocode robot implementing the following strategy:

**Movement**: ToyBot moves in a circular pattern, with the movement direction reversing underling under a few conditions: 1) when the robot approaches the edge of the field; 2) when the distance between the robot and its chosen opponent decreases at a specified checking interval; 3) when the robot hits a wall; and 4) when the robot hits another robot. These direction reversal conditions causes the robot to have a distinct random movement of its own.

**Targeting**: Each scanned robots are tracked in an internal data object. Currently, the targeting uses wide-beam tracking, with the first scanned robot selected as the initial target. Once a target is selected, a predictive firing solution is calculated and used to turn the gun.

**Firing**: When gun is turned to predicted firing solution and target is within a defined range, a distance-relative power shot is fired.

**Vulnerabilities**: There are many opportunities for improvement of this robot. It does not attempt to avoid other robots and does nothing when it is shot. It does not save any information about other robots between rounds in order to improve its strategy. It becomes easy target in the cases when its initial target is not its nearest shooter.

# Details #

Add your content here.  Format your content with:
  * Text in **bold** or _italic_
  * Headings, paragraphs, and lists
  * Automatic links to other wiki pages