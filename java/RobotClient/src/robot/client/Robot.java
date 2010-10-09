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

import robot.common.response.*;
import robot.common.request.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Robot {

    private RobotServerConnection server;
    String name;

    public Robot(RobotServerConnection server, String name) {
        this.server = server;
        this.name = name;
    }

    public Response doStep() {
        RequestStep req = new RequestStep(name);
        Response res = server.processRequest(req);
    }

    public Response turnLeft() {
        RequestStep req = new RequestTurnLeft(name);
        Response res = server.processRequest(req);
    }
    
    public Response pickUp() {
        RequestStep req = new RequestTurnLeft(name);
        Response res = server.processRequest(req);
    }
    
    public Response repair(int blockToRepair) {

    }
    
    public Response recharge() {
        
    }

}
