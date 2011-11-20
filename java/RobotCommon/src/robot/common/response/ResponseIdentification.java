/*
 * ResponseIdentification
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

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import robot.common.StringUtils;
import robot.common.exception.RobotException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ResponseIdentification extends Response {

    protected String address;
    protected static final String idFormatString = "Ahoj kliente! Oslovuj mne %s.";

    public ResponseIdentification(String address) {
        this.address = address;
    }

    public ResponseIdentification() {
        this("");
    }

    public String formatForTcp() {
        return new StringBuilder("200 ").append(String.format(idFormatString, getAddress())).append("\r\n").toString();
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean parseParamsFromTcp(String params) {
        Pattern pattern = Pattern.compile("Oslovuj mne [^.\n\r$]+[.\n\r$]");
        Matcher match = pattern.matcher(params);
        if (match.find()) {
            List<String> tokens = Arrays.asList(match.group().split(" "));
            String almostAddress = StringUtils.join(tokens.subList(2, tokens.size()), " ");
            pattern = Pattern.compile("[^.\n\r]+");
            match = pattern.matcher(almostAddress);
            if (match.find()) {
                this.address = match.group();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEndGame() {
        return false;
    }

    public void handle(ResponseHandler handler) throws RobotException {
        handler.handleIdentification(address);
    }

}
