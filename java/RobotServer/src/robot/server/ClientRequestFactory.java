/*
 * ClientRequestFactory
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

package robot.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import robot.common.StringUtils;
import robot.server.exception.InvalidAddressException;
import robot.common.request.*;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ClientRequestFactory {

    private String address;
    
    private final static List<String> validRequestNames;
    private final static HashMap<String, Request> prototypes;
    static {
        prototypes = new HashMap<String, Request>();
        prototypes.put("KROK", new RequestStep());
        prototypes.put("VLEVO", new RequestTurnLeft());
        prototypes.put("ZVEDNI", new RequestPickUp());
        prototypes.put("OPRAVIT", new RequestRepair());
        prototypes.put("NABIT", new RequestRecharge());

        validRequestNames = Arrays.asList(new String[] {"KROK", "VLEVO", "ZVEDNI", "OPRAVIT", "NABIT"});
    }

    public ClientRequestFactory(String address) {
        this.address = address;
    }

    public Request parseRequest(String rawRequest) throws InvalidAddressException {
        
        if (!rawRequest.startsWith(address)) {
            throw new InvalidAddressException(extractPotentialAddress(rawRequest), address, rawRequest);
        }

        try {

            String requestStringOnly = rawRequest.substring(address.length() + 1); // strip out the address
            List<String> tokens = Arrays.asList(requestStringOnly.split(" "));

            Request prototype = prototypes.get(tokens.get(0));
            if (prototype == null) {
                return new RequestUnknown();
            }

            Request request = prototype.clone();
            request.setAdress(address);
            if (request.parseParamsFromTcp(StringUtils.join(tokens.subList(1, tokens.size()), " "))) {
                return request;
            } else {
                return new RequestUnknown();
            }


        } catch (Exception ex) {
            return new RequestUnknown();
        }
    }

    private String extractPotentialAddress(String rawRequest) {

        for (String request : validRequestNames) {
            int requestPos = rawRequest.indexOf(request);
            if (requestPos > 2) {
                return rawRequest.substring(0, requestPos - 1);
            }
        }
        return "/unknown/";
    }

}
