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

package cz.cvut.fel.psi.robot.client;

import java.io.IOException;
import cz.cvut.fel.psi.client.exception.UnexpectedException;
import cz.cvut.fel.psi.client.exception.UnexpectedResponseException;
import cz.cvut.fel.psi.robot.common.Direction;
import cz.cvut.fel.psi.robot.common.Position;
import cz.cvut.fel.psi.robot.common.RobotInfo;
import cz.cvut.fel.psi.robot.common.exception.*;
import cz.cvut.fel.psi.robot.common.response.*;
import cz.cvut.fel.psi.robot.common.request.*;

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

    public RobotInfo doStep() throws RobotCrashedException, RobotCrumbledException, RobotProcessorDamagedException {
        try {
            Response res = server.processRequest(new RequestStep(name));
            res.handle(handler);
            return info;
        } catch (RobotCrumbledException ex) {
            throw ex;
        } catch (RobotCrashedException ex) {
            throw ex;
        } catch (RobotProcessorDamagedException ex) {
            throw ex;
        } catch (RobotException ex) {
            throw new UnexpectedResponseException(ex);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public RobotInfo turnLeft() {
        try {
            Response res = server.processRequest(new RequestTurnLeft(name));
            res.handle(handler);
            return this.info;
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
    
    public void repair(int processorToRepair) throws RobotProcessorOkException {
        try {
            Response res = server.processRequest(new RequestRepair(name, processorToRepair));
            res.handle(handler);
        } catch (RobotProcessorOkException ex) {
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

    public Position getPos() {
        Position pos = this.info.getPosition();
        return new Position(pos.x, pos.y);
    }

    public void setPos(Position position) {
        Position pos = this.info.getPosition();
        pos.x = position.x;
        pos.y = position.y;
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
