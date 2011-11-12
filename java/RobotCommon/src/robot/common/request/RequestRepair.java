/*
 * RequestRepair
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

package robot.common.request;

import robot.common.response.ResponseNoDamage;
import robot.common.response.Response;
import robot.common.response.ResponseOk;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RequestRepair extends Request {

    private int blockToRepair;

    public RequestRepair(String address, int blockToRepair) {
        super(address);
        this.blockToRepair = blockToRepair;
    }

    public RequestRepair(int blockToRepair) {
        this("", blockToRepair);
    }

    public RequestRepair() {
        this("", 0);
    }

    public String formatForTcp() {
        return new StringBuilder(getAdress()).append(" OPRAVIT ").append(blockToRepair).append("\r\n").toString();
    }

    public Response route(RequestProcessor processor) {
        return processor.processRepair(blockToRepair);
    }

    @Override
    public boolean parseParamsFromTcp(String params) {
        String[] tokens = params.split(" ");
        if (tokens.length == 1) {
            try {
                this.blockToRepair = Integer.parseInt(tokens[0]);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected List<Response> getSupportedResponses() {
        return Arrays.asList(new Response[] {new ResponseOk(), new ResponseNoDamage()});
    }

}
