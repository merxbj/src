/*
 * ClientRequestProcessor
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

package robot.server;

import robot.server.exception.*;
import robot.common.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ClientRequestProcessor implements RequestProcessor {

    private Robot robot;

    public ClientRequestProcessor(Robot robot) {
        this.robot = robot;
    }

    public Response processPickUp() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Response processRecharge() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Response processRepair(int blockToRepair) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Response processStep() {
        try {
            RobotStatus status = robot.doStep();
            return new ResponseOk(status.getBattery(), status.getX(), status.getY());
        } catch (RobotCrashedException ex) {
            return new ResponseCrash();
        } catch (RobotBatteryEmptyException ex) {
            return new ResponseBatteryEmpty();
        } catch (RobotCrumbledException ex) {
            return new ResponseCrumbled();
        } catch (RobotDamagedException ex) {
            return new ResponseDamage(ex.getDamagedBlock());
        }
    }

    public Response processTurnLeft() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
