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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import robot.common.request.*;
import robot.server.exception.InvalidAddressException;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class ClientRequestFactory {

    private String address;
    
    private final static Pattern validRequestPattern;
    private final static HashMap<String, Request> prototypes;
    static {
        prototypes = new HashMap<String, Request>();
        prototypes.put("KROK", new RequestStep());
        prototypes.put("VLEVO", new RequestTurnLeft());
        prototypes.put("ZVEDNI", new RequestPickUp());
        prototypes.put("OPRAVIT", new RequestRepair());

        validRequestPattern = Pattern.compile("(.+) (KROK|VLEVO|ZVEDNI|OPRAVIT|NABIT) ?(\\d)?");
    }

    public ClientRequestFactory(String address) {
        this.address = address;
    }

    public Request parseRequest(String rawRequest) throws InvalidAddressException {
        
        Matcher match = validRequestPattern.matcher(rawRequest);
        if (!match.find()) {
            return new RequestUnknown();
        }

        try {

            Request prototype = prototypes.get(match.group(2));
            if (prototype == null) {
                return new RequestUnknown();
            }

            if (!address.equals(match.group(1))) {
                throw new InvalidAddressException(match.group(1), address, rawRequest);
            }

            Request request = prototype.clone();
            request.setAdress(address);
            if (request.parseParamsFromTcp(match.groupCount() == 3 ? match.group(3) : "")) {
                return request;
            } else {
                return new RequestUnknown();
            }


        } catch (Exception ex) {
            return new RequestUnknown();
        }
    }
}
