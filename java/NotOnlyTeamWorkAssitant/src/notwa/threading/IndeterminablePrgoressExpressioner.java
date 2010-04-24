/*
 * IndeterminablePrgoressExpressioner
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

package notwa.threading;

/**
 * The implemntor of this interface, tipicaly some kind of progress indicator, will
 * implement this to provide a unified interface to express an indeterminate progress
 * state which may begin and also may end.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public interface IndeterminablePrgoressExpressioner {

    /**
     * Invoking this method should force the implementor to begin indicate that
     * the current progress status is indeterminate.
     */
    public void beginIndetermination();

    /**
     * Invoking this method should force the implementor to end indicate that
     * the current progress status is indeterminate.
     */
    public void endIndetermination();
}
