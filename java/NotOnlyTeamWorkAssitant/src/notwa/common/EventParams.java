/*
 * EventParams
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

package notwa.common;

/**
 * Parameters that comes altogether with an, usually, handled {@link Event}.
 * Specific implementation of the base <code>Event</code> usually provides a
 * specific implementation of this <code>class</code>.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class EventParams {

    /**
     * The uniqe identifier of the <code>Event</code> being fired and handled.
     */
    protected int eventId;

    /**
     * The generic parameter used for general purposes in case that there is no
     * need for extra specification (very suitable for just-in-case event with
     * single params)
     */
    protected Object params;

    /**
     * The params-less constructor of the base implementation accepts only the <code>Event</code>
     * identifier.
     * 
     * @param eventId The actual event identifier.
     */
    public EventParams(int eventId) {
        this.eventId = eventId;
    }

    /**
     * The constructor accepting the <code>Event</code> identifier and the specified
     * params.
     * 
     * @param eventId The actual event identifier.
     * @param params The actual parameters.
     */
    public EventParams(int eventId, Object params) {
        this.eventId = eventId;
        this.params = params;
    }

    /**
     * Gets the actual <code>Event</code> identifier which uniqely identifies
     * the type of <code>Event</code> fired.
     *
     * @return The <code>Event</code> identifier.
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Gets the actual <code>Event</code> generic parameters.
     * @return The <code>Event</code> parameters.
     */
    public Object getParams() {
        return params;
    }
}
