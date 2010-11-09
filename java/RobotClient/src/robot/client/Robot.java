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
import robot.client.exception.UnexpectedException;
import robot.client.exception.UnexpectedResponseException;
import robot.common.Battery;
import robot.common.Direction;
import robot.common.Position;
import robot.common.RobotInfo;
import robot.common.exception.*;
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
    private String name;
    private String secretMessage;
    private RobotInfo info;

    public Robot(RobotServerConnection server) {
        this.server = server;        
        this.handler = new ServerResponseHandler(this);
        this.info = new RobotInfo();
    }

    public void initialize() {
        try {
            Response res = this.server.connect();
            res.handle(handler);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        } catch (RobotException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public RobotInfo doStep() throws RobotCrashedException, RobotCrumbledException, RobotBatteryEmptyException, RobotDamagedException {
        try {
            Response res = server.processRequest(new RequestStep(name));
            res.handle(handler);
            return info;
        } catch (RobotCrumbledException ex) {
            throw ex;
        } catch (RobotCrashedException ex) {
            throw ex;
        } catch (RobotBatteryEmptyException ex) {
            throw ex;
        } catch (RobotDamagedException ex) {
            throw ex;
        } catch (RobotException ex) {
            throw new UnexpectedResponseException(ex);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public RobotInfo turnLeft() throws RobotBatteryEmptyException {
        try {
            Response res = server.processRequest(new RequestTurnLeft(name));
            res.handle(handler);
            return this.info;
        } catch (RobotBatteryEmptyException ex) {
            throw ex;
        } catch (RobotException ex) {
            throw new UnexpectedResponseException(ex);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }
    
    public String pickUp() throws RobotCannotPickUpException {
        try {
            Response res = server.processRequest(new RequestPickUp(name));
            res.handle(handler);
            return secretMessage; // secret message filled by the handler
        } catch (RobotCannotPickUpException ex) {
            throw ex;
        } catch (RobotException ex) {
            throw new UnexpectedResponseException(ex);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }
    
    public void repair(int blockToRepair) throws RobotNoDamageException {
        try {
            Response res = server.processRequest(new RequestRepair(name, blockToRepair));
            res.handle(handler);
        } catch (RobotNoDamageException ex) {
            throw ex;
        } catch (RobotException ex) {
            throw new UnexpectedResponseException(ex);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }
    
    public void recharge() throws RobotDamagedException, RobotCrumbledException {
        try {
            Response res = server.processRequest(new RequestRecharge(name));
            res.handle(handler);
        } catch (RobotDamagedException ex) {
            throw ex;
        } catch (RobotCrumbledException ex) {
            throw ex;
        } catch (RobotException ex) {
            throw new UnexpectedResponseException(ex);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public String getSecretMessage() {
        return secretMessage;
    }

    public void setSecretMessage(String secretMessage) {
        this.secretMessage = secretMessage;
    }

    public Battery getBattery() {
        return this.info.getBattery();
    }

    public Position getPos() {
        return this.info.getPosition();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RobotServerConnection getServer() {
        return server;
    }

    public Direction getDirection() {
        return this.info.getDirection();
    }

    public RobotInfo getInfo() {
        return info;
    }

    public void setDirection(Direction direction) {
        this.info.setDirection(direction);
    }

}
