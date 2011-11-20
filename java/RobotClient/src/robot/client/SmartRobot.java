/*
 * SmartRobot
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package robot.client;

import robot.client.exception.UnexpectedException;
import robot.common.Direction;
import robot.common.Position;
import robot.common.RobotInfo;
import robot.common.exception.RobotCannotPickUpException;
import robot.common.exception.RobotCrashedException;
import robot.common.exception.RobotCrumbledException;
import robot.common.exception.RobotProcessorDamagedException;
import robot.common.exception.RobotProcessorOkException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SmartRobot {

    private Robot robot;

    public SmartRobot(Robot robot) {
        this.robot = robot;
    }

    public RobotInfo initialize() {
        this.robot.initialize();
        turn();
        Position initialPosition = this.robot.getPos();
        step();
        Position afterFirstStep = this.robot.getPos();
        Direction direction = determineDirection(initialPosition, afterFirstStep);
        this.robot.setDirection(direction);
        return this.robot.getInfo();
    }

    public void stepUp() {
        turn(Direction.North);
        step();
    }

    public void stepLeft() {
        turn(Direction.West);
        step();
    }

    public void stepRight() {
        turn(Direction.East);
        step();
    }

    public void stepDown() {
        turn(Direction.South);
        step();
    }

    public String pickUp() {
        try {
            return robot.pickUp();
        } catch (RobotCannotPickUpException ex) {
            throw new UnexpectedException("Robot tried to pick up the secret message on place where it isn't allowed! Connection lost!", ex);
        }
    }

    private void turn(Direction direction) {
        Direction currentDir = robot.getDirection();
        while (!currentDir.equals(direction)) {
            turn();
            currentDir = Direction.getNextDirection(currentDir);
        }
        robot.setDirection(currentDir);
    }

    private void turn() {
        robot.turnLeft();
    }

    private void step() {
        RobotInfo info = null;
        do {
            try {
                info = robot.doStep();
            } catch (RobotCrashedException ex) {
                throw new UnexpectedException("Robot stepped out of the field! Connection lost!", ex);
            } catch (RobotCrumbledException ex) {
                throw new UnexpectedException("Robot crumbled while trying to do a step! Connection lost!", ex);
            } catch (RobotProcessorDamagedException ex) {
                repair(ex.getDamagedProcessor());
            }
        } while (info == null);
    }

    private void repair(int processorToRepair) {
        boolean repaired = false;
        do {
            try {
                robot.repair(processorToRepair);
                repaired = true;
            } catch (RobotProcessorOkException ex) {
                throw new UnexpectedException("Attempted to repair processor, which is not damaged! Unable to recover!", ex);
            }
        } while (!repaired);
    }

    private Direction determineDirection(Position from, Position to) {
        Direction dir = Direction.fromVector(to.substract(from));
        if (dir == null || dir.equals(Direction.Unknown)) {
            throw new UnexpectedException("Unable to determine the robot direction! Unable to recover!");
        }
        return dir;
    }

}
