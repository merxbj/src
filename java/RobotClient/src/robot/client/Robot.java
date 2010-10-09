/*
 * Robot
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

import java.io.IOException;
import robot.common.exception.RobotCrashedException;
import robot.common.exception.RobotException;
import robot.common.response.*;
import robot.common.request.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Robot {

    private RobotServerConnection server;
    private ServerResponseHandler handler;
    String name;

    public Robot(RobotServerConnection server) {
        this.server = server;
        this.name = server.getAddress();
        this.handler = new ServerResponseHandler(this);
    }

    public void doStep() throws IOException, RobotCrashedException {
        try {
            Response res = server.processRequest(new RequestStep(name));
            res.handle(handler);
        } catch (RobotException ex) {
            throw ex.getCause();
        }
    }

    public void turnLeft() {
        RequestTurnLeft req = new RequestTurnLeft(name);
        Response res = server.processRequest(req);
    }
    
    public String pickUp() {
        RequestPickUp req = new RequestPickUp();
        Response res = server.processRequest(req);
    }
    
    public void repair(int blockToRepair) {

    }
    
    public void recharge() {
        
    }

}
