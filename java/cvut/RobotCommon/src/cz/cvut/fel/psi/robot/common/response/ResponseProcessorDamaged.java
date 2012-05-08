/*
 * ResponseProcessorDamaged
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
public class ResponseProcessorDamaged extends Response {

    protected int damagedProcessor;

    public ResponseProcessorDamaged(int damagedProcessor) {
        this.damagedProcessor = damagedProcessor;
    }

    public ResponseProcessorDamaged() {
        this(-1);
    }

    public String formatForTcp() {
        return new StringBuilder("580 ").append(String.format("SELHANI PROCESORU %d", getDamagedProcessor())).append("\r\n").toString();
    }

    public int getDamagedProcessor() {
        return damagedProcessor;
    }

    public void setDamagedProcessor(int damagedProcessor) {
        this.damagedProcessor = damagedProcessor;
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
                this.damagedProcessor = Integer.parseInt(match.group());
                return true;
            } catch (Exception ex) {}
        }
        return false;
    }

    public void handle(ResponseHandler handler) throws RobotException {
        handler.handleProcessorDamaged(damagedProcessor);
    }

}
