/*
 * Action
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
 * Class representing a single action that can be performed by its executor.
 * It actually suplies a delegate accepting a single parameter.
 * 
 * @param <T>   The actual parameter to be accepted by the concrete implementation of
 *              this <code>Action</code>
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public abstract class Action<T> {
    
    /**
     * Parameter to be accepted by the action.
     */
    protected T params;

    /**
     * Simple parameter-less contructor.
     */
    public Action() {
    }

    /**
     * Constructor acceptiong the specified parameter.
     *
     * @param params The parameter.
     */
    public Action(T params) {
        this.params = params;
    }

    /**
     * By implementing this function you should provide a code to be performed by
     * the action executor.
     */
    public abstract void perform();
}
