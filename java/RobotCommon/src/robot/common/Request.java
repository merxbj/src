/*
 * Request
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

package robot.common;

import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class Request implements TcpFormatable, Processable {

    protected String adress;
    protected List<Response> supportedResponses;

    public Request() {
        this("");
    }

    public Request(String adress) {
        this.adress = adress;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public boolean isResponseSupported(Response response) {
        for (Response supportedResponse : supportedResponses) {
            if (supportedResponse.getClass().equals(response.getClass())) {
                return true;
            }
        }
        return false;
    }

    public Response assertValidResponse(Response response) {
        if (isResponseSupported(response)) {
            return response;
        } else {
            throw new MissbehavedRequestProcessorException(
                    String.format("Unsupported response %s on request %s!",
                    response.getClass().getName(),
                    this.getClass().getName()));
        }
    }

}
