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

package cz.cvut.fel.psi.robot.common.request;

import cz.cvut.fel.psi.robot.common.response.ResponseProcessorOk;
import cz.cvut.fel.psi.robot.common.response.Response;
import cz.cvut.fel.psi.robot.common.response.ResponseOk;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RequestRepair extends Request {

    private int processorToRepair;

    public RequestRepair(String address, int processorToRepair) {
        super(address);
        this.processorToRepair = processorToRepair;
    }

    public RequestRepair(int processorToRepair) {
        this("", processorToRepair);
    }

    public RequestRepair() {
        this("", 0);
    }

    public String formatForTcp() {
        return new StringBuilder(getAdress()).append(" OPRAVIT ").append(processorToRepair).append("\r\n").toString();
    }

    public Response route(RequestProcessor processor) {
        return processor.processProcessorRepair(processorToRepair);
    }

    @Override
    public boolean parseParamsFromTcp(String params) {
        try {
            this.processorToRepair = Integer.parseInt(params);
            return ((processorToRepair > 0) && (processorToRepair < 10));
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected List<Response> getSupportedResponses() {
        return Arrays.asList(new Response[] {new ResponseOk(), new ResponseProcessorOk()});
    }

}
