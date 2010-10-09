/*
 * ServerResponseFactory
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

package robot.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import robot.common.response.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ServerResponseFactory {

    private final static List<String> validResponseNumbers;
    private final static HashMap<String, Response> prototypes;
    static {
        prototypes = new HashMap<String, Response>();
        prototypes.put("220", new ResponseIdentification());
        prototypes.put("221", new ResponseSuccess());
        prototypes.put("250", new ResponseOk());
        prototypes.put("500", new ResponseUnknownRequest());
        prototypes.put("530", new ResponseCrash());
        prototypes.put("540", new ResponseBatteryEmpty());
        prototypes.put("550", new ResponseCannotPickUp());
        prototypes.put("570", new ResponseDamage());
        prototypes.put("571", new ResponseNoDamage());
        prototypes.put("572", new ResponseCrumbled());

        validResponseNumbers = Arrays.asList(new String[] {"220", "221", "250", "500", "530", "540", "550", "570", "571", "572"});
    }

    public ServerResponseFactory() {
    }

    public Response parseResponse(String rawResponse) {

        try {

            String[] tokens = rawResponse.split(" ");

            Response prototype = prototypes.get(tokens[0]);
            if (prototype == null) {
                return new ResponseUnknown();
            }

            Response response = prototype.clone();
            response.parseParams();
            if (request.parseFromTcp(Arrays.asList(tokens))) {
                return request;
            } else {
                return new RequestUnknown();
            }


        } catch (Exception ex) {
            return new RequestUnknown();
        }
    }

    private String extractPotentialAddress(String rawRequest) {

        for (String request : validResponseNumbers) {
            int requestPos = rawRequest.indexOf(request);
            if (requestPos > 2) {
                return rawRequest.substring(0, requestPos - 1);
            }
        }
        return "/unknown/";
    }

}
