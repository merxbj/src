/*
 * ResponseDamage
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
public class ResponseDamage extends Response {

    protected int damagedBlock;

    public ResponseDamage(int damagedBlock) {
        this.damagedBlock = damagedBlock;
    }

    public ResponseDamage() {
        this(-1);
    }

    public String formatForTcp() {
        return new StringBuilder("570 ").append(String.format("PORUCHA BLOK %d", getDamagedBlock())).append("\r\n").toString();
    }

    public int getDamagedBlock() {
        return damagedBlock;
    }

    public void setDamagedBlock(int damagedBlock) {
        this.damagedBlock = damagedBlock;
    }

    @Override
    public boolean isEndGame() {
        return false;
    }

    @Override
    public boolean parseParamsFromTcp(String params) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher match = pattern.matcher(params);
        if (match.find()) {
            try {
                this.damagedBlock = Integer.parseInt(match.group());
                return true;
            } catch (Exception ex) {}
        }
        return false;
    }

    public void handle(ResponseHandler handler) throws RobotException {
        handler.handleDamage(damagedBlock);
    }

}
