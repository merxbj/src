/*
 * CluedoGameParseException
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
package cluedo.simulation;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CluedoGameParseException extends Exception {

    /**
     * Creates a new instance of <code>CluedoGameParseException</code> without detail message.
     */
    public CluedoGameParseException() {
    }

    /**
     * Constructs an instance of <code>CluedoGameParseException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CluedoGameParseException(String msg, Object... attr) {
        super(String.format(msg, attr));
    }
    
    public CluedoGameParseException(Throwable cause, String msg, Object... attr) {
        super(String.format(msg, attr), cause);
    }
}
