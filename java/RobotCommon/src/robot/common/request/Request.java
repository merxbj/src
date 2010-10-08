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

package robot.common.request;

import robot.common.response.Response;
import java.util.List;
import robot.common.exception.MissbehavedRequestProcessorException;
import robot.common.Processable;
import robot.common.TcpFormatable;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class Request implements TcpFormatable, Processable, Cloneable {

    protected String adress;
    protected List<Response> supportedResponses;

    public Request() {
        this("/nobody/");
    }

    public Request(String adress) {
        this.adress = adress;
    }

    @Override
    public Request clone() {
        Request clone = null;
        try {
            clone = (Request) super.clone();
        } catch (CloneNotSupportedException ex) {}
        return clone;
    }

    public Response process(RequestProcessor processor) {

        Response response = route(processor);
        if (!isResponseSupported(response)) {
            throw new MissbehavedRequestProcessorException(
                String.format("Unsupported response %s on request %s!",
                response.getClass().getName(),
                this.getClass().getName()));
        }

        return response;
    }

    public boolean parseFromTcp(List<String> requestStringAndParams) {
        return true;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    protected boolean isResponseSupported(Response response) {

        if (supportedResponses == null) {
            supportedResponses = getSupportedResponses();
        }

        for (Response supportedResponse : supportedResponses) {
            if (supportedResponse.getClass().equals(response.getClass())) {
                return true;
            }
        }
        return false;
    }

    protected abstract Response route(RequestProcessor processor);
    protected abstract List<Response> getSupportedResponses();

}
