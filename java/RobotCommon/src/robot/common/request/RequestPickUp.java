/*
 * RequestPickUp
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

import robot.common.response.ResponseSuccess;
import robot.common.response.Response;
import robot.common.response.ResponseCannotPickUp;
import java.util.Arrays;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RequestPickUp extends Request {

    public RequestPickUp(String adress) {
        super(adress);
        this.supportedResponses = Arrays.asList(new Response[] {new ResponseSuccess(), new ResponseCannotPickUp()});
    }

    public RequestPickUp() {
        this("");
    }

    public String formatForTcp() {
        return new StringBuilder(getAdress()).append(" ZVEDNI ").append("\r\n").toString();
    }

    public Response process(RequestProcessor processor) {
        Response response = processor.processPickUp();
        return assertValidResponse(response);
    }

}
