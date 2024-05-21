package rescueagents;

import rescueframework.AbstractRobotControl;
import world.Path;
import world.Robot;
import world.RobotPercepcion;

/**
 *  RobotControl class to implement custom robot control strategies
 */
public class RobotControl extends AbstractRobotControl{
    /**
     * Default constructor saving world robot object and percepcion
     * 
     * @param robot         The robot object in the world
     * @param percepcion    Percepcion of all robots
     */
    public RobotControl(Robot robot, RobotPercepcion percepcion) {
        super(robot, percepcion);
    }
    
    
    /**
     * Custom step strategy of the robot, implement your robot control here!
     * 
     * @return  Return NULL for staying in place, 0 = step up, 1 = step right,
     *          2 = step down, 3 = step left
     */
    public Integer step() {      
        // By default the robot stays in place
        return null;
    }
}
