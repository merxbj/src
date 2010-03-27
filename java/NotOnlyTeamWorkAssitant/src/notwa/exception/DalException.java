/*
 * DalException
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
package notwa.exception;

/**
 * <code>Throwable</code> exception which should be (acutally shouldn't be) caught
 * after the DAL processing.
 * 
 * @author eTeR
 */
public class DalException extends Exception {

    /**
     * Default constructor.
     */
    public DalException() {
    }

    /**
     * Constructor providing you with the possibility to specify the exception
     * message which should briefly descrie the reason of the exception.
     * 
     * @param message Brief description of cause of the exceptional situtation.
     */
    public DalException(String message) {
        super(message);
    }

    /**
     * Constructor providing you with the possibility to specify the inner exception
     * which was probably caught by some general <code>catch</code> statement and
     * wrapped by this instance of <code>DalException</code>.
     * Actually it is discouraged to use this constructor solely, without providing an
     * explanation message.
     *
     * @param cause The actual exception which is going to be wrapped by this new instance.
     *              Usually refered to as inner exeption.
     */
    public DalException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor providing you with the possibility to specify the exception 
     * message which should briefly describe the reason of the exception and the
     * inner exception which was probably caught by some general <code>catch</code>
     * statement and is going to be wrapped into this new instance which should be
     * definitely more descriptive.
     * 
     * @param message Brief description of cause of the exceptional situtation.
     * @param cause The actual exception which is going to be wrapped by this new instance.
     *              Usually refered to as inner exeption.
     */
    public DalException(String message, Throwable cause) {
        super(message, cause);
    }

}
