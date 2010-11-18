/*
 * ResponseOk
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

package robot.common.response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import robot.common.exception.RobotException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ResponseOk extends Response {

    protected final static String robotDataFormat = "(%d,%d,%d)";
    protected int remainingBattery;
    protected int x;
    protected int y;

    public ResponseOk(int remainingBattery, int x, int y) {
        this.remainingBattery = remainingBattery;
        this.x = x;
        this.y = y;
    }

    public ResponseOk() {
        this(-1,-1,-1);
    }

    public String formatForTcp() {
        return new StringBuilder("250 ").append("OK ").append(String.format(robotDataFormat, getRemainingBattery(), getX(), getY())).append("\r\n").toString();
    }

    public int getRemainingBattery() {
        return remainingBattery;
    }

    public void setRemainingBattery(int remainingBattery) {
        this.remainingBattery = remainingBattery;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean isEndGame() {
        return false;
    }

    @Override
    public boolean parseParamsFromTcp(String params) {
        Pattern pattern = Pattern.compile("\\(.+\\)");
        Matcher match = pattern.matcher(params);
        if (match.find()) {
            String temp = match.group();
            String status = temp.substring(1, temp.length() - 1);
            String[] tokens = status.split(",");
            if (tokens.length == 3) {
                try {
                    this.remainingBattery = Integer.parseInt(tokens[0]);
                    this.x = Integer.parseInt(tokens[1]);
                    this.y = Integer.parseInt(tokens[2]);
                    return true;
                } catch (Exception ex) {}
            }
        }
        return false;
    }

    public void handle(ResponseHandler handler) throws RobotException {
        handler.handleOk(remainingBattery, x, y);
    }

}
