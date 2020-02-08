/*
 * RequestUnknown
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

import java.util.Arrays;
import java.util.List;
import cz.cvut.fel.psi.robot.common.response.Response;
import cz.cvut.fel.psi.robot.common.response.ResponseUnknownRequest;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RequestUnknown extends Request {

    public RequestUnknown(String address) {
        super(address);
    }

    public RequestUnknown() {
    }

    public String formatForTcp() {
        throw new UnsupportedOperationException("Does not make sense.");
    }

    public Response route(RequestProcessor processor) {
        return processor.processUnknown();
    }

    @Override
    protected List<Response> getSupportedResponses() {
        return Arrays.asList(new Response[] {new ResponseUnknownRequest()});
    }

}
