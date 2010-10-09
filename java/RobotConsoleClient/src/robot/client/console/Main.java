/*
 * Main
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
package robot.client.console;

import robot.client.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {

    public static void main(String[] args) {

        CommandLine cl = CommandLine.parse(args);
        RobotServerConnection connection = new RobotServerConnection(cl.getAddress(), cl.getPortNumber());
        AutomaticRobot robot = new AutomaticRobot(new SmartRobot(new Robot(connection)));
        
        try {
            String secret = robot.findSecret();
        } catch (Exception ex) {
            
        }
        
    }

}
