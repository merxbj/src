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

package cz.cvut.fel.psi.robot.server;

import cz.cvut.fel.psi.robot.common.request.RequestProcessor;
import cz.cvut.fel.psi.robot.common.response.*;
import cz.cvut.fel.psi.robot.common.exception.*;

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
        try {
            String secretMessage = robot.pickUp();
            return new ResponseSuccess(secretMessage);
        } catch (RobotCannotPickUpException ex) {
            return new ResponseCannotPickUp();
        }
    }

    public Response processProcessorRepair(int processorToRepair) {
        try {
            RobotServerInfo info = robot.repair(processorToRepair);
            return new ResponseOk(info.getPosition().x, info.getPosition().y);
        } catch (RobotProcessorOkException ex) {
            return new ResponseProcessorOk();
        }
    }

    public Response processStep() {
        try {
            RobotServerInfo info = robot.doStep();
            return new ResponseOk(info.getPosition().x, info.getPosition().y);
        } catch (RobotCrashedException ex) {
            return new ResponseCrash();
        } catch (RobotCrumbledException ex) {
            return new ResponseCrumbled();
        } catch (RobotProcessorDamagedException ex) {
            return new ResponseProcessorDamaged(ex.getDamagedProcessor());
        }
    }

    public Response processTurnLeft() {
        RobotServerInfo info = robot.turnLeft();
        return new ResponseOk(info.getPosition().x, info.getPosition().y);
    }

    public Response processUnknown() {
        return new ResponseUnknownRequest();
    }

    public String getExpectedAddress() {
        return robot.getName();
    }

}
