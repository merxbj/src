/*
 * InvalidAddressException
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

package cz.cvut.fel.psi.robot.server.exception;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class InvalidAddressException extends Exception {

    private String requestedAddress;
    private String expectedAddress;
    private String receivedRequest;

    public InvalidAddressException(String requested, String expected, String receivedRequest) {
        this.requestedAddress = requested;
        this.expectedAddress = expected;
        this.receivedRequest = receivedRequest;
    }

    public String getRequestedAddress() {
        return this.requestedAddress;
    }

    public String getExpectedAddress() {
        return expectedAddress;
    }

    public String getReceivedRequest() {
        return receivedRequest;
    }

}
