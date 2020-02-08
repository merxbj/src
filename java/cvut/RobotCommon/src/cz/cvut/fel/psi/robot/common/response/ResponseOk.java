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

package cz.cvut.fel.psi.robot.common.response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cz.cvut.fel.psi.robot.common.exception.RobotException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ResponseOk extends Response {

    protected int x;
    protected int y;

    public ResponseOk(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ResponseOk() {
        this(-1,-1);
    }

    public String formatForTcp() {
        return new StringBuilder("240 OK ").append(String.format("(%d,%d)", getX(), getY())).append("\r\n").toString();
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
            if (tokens.length == 2) {
                try {
                    this.x = Integer.parseInt(tokens[0]);
                    this.y = Integer.parseInt(tokens[1]);
                    return true;
                } catch (Exception ex) {}
            }
        }
        return false;
    }

    public void handle(ResponseHandler handler) throws RobotException {
        handler.handleOk(x, y);
    }

}
