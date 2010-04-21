/*
 * LoggingExceptionHandler
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
package notwa.logger;

/**
 * Interface contracting to implement the {@link #handle(java.lang.Exception)}
 * method, which notifies the implementor that the <code>Exception</code> has
 * been thrown during the logging.
 * <p>Please, be wise and do not report such an exceptional state to the same
 * logger which reported this exception</p>.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public interface LoggingExceptionHandler {
    /**
     * Called by the {@link Logger} concrete implementation when <code>Exception</code>
     * has been thrown during the logging process.
     *
     * @param ex The cought <code>Exception</code>.
     */
    public void handle(Exception ex);
}
