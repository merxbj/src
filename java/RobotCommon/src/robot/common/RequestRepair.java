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

package robot.common;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RequestRepair extends Request {

    private int blockToRepair;

    public RequestRepair(String adress, int blockToRepair) {
        super(adress);
        this.blockToRepair = blockToRepair;
    }

    public RequestRepair(int blockToRepair) {
        this("", blockToRepair);
    }

    public RequestRepair() {
        this("", 0);
    }

    public String formatForTcp() {
        return new StringBuilder(getAdress()).append(" ZVEDNI ").append(blockToRepair).append("\r\n").toString();
    }

    public Response process(RequestProcessor processor) {
        Response response = processor.processRepair(blockToRepair);
        return assertValidResponse(response);
    }

}
