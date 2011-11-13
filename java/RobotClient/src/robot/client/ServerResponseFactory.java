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
import robot.common.StringUtils;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ServerResponseFactory {

    private final static HashMap<String, Response> prototypes;
    static {
        prototypes = new HashMap<String, Response>();
        prototypes.put("200", new ResponseIdentification());
        prototypes.put("210", new ResponseSuccess());
        prototypes.put("240", new ResponseOk());
        prototypes.put("500", new ResponseUnknownRequest());
        prototypes.put("530", new ResponseCrash());
        prototypes.put("550", new ResponseCannotPickUp());
        prototypes.put("580", new ResponseProcessorDamaged());
        prototypes.put("571", new ResponseProcessorOk());
        prototypes.put("572", new ResponseCrumbled());
    }

    public Response parseResponse(String rawResponse) {

        try {

            List<String> tokens = Arrays.asList(rawResponse.split(" "));

            Response prototype = prototypes.get(tokens.get(0));
            if (prototype == null) {
                return new ResponseUnknown();
            }

            Response response = prototype.clone();
            if (response.parseParamsFromTcp(StringUtils.join(tokens.subList(1, tokens.size()), " "))) {
                return response;
            } else {
                return new ResponseUnknown();
            }

        } catch (Exception ex) {
            return new ResponseUnknown();
        }
    }

}
