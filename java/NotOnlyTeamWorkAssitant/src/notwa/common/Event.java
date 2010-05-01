/*
 * Event
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
 * Class representing an Event which may occurs within the application that may
 * be handled by the appropriate {@link EventHandler}.
 * <p><code>Event</code> comes along with its {@link EventParams} that are specific
 * to the Event implementation.
 *
 * @params <T> Represents the <code>EventParams</code> that this <code>Event</code>
 *          will carry together.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Event<T extends EventParams> {
    private T params;
    private boolean handled;

    /**
     * The sole constructor accepting <code>EventParams</code> as the only and
     * required parameter.
     * 
     * @params params The actual event parameter.
     */
    public Event(T params) {
        this.params = params;
    }

    /**
     * Returns the actual <code>Event</code> parameter.
     * 
     * @return The <code>EventParameter</code>.
     */
    public T getParams() {
        return params;
    }

    /**
     * Returns the actual <code>Event</code> identifier which is stored within
     * the <code>EventParams</code>. If the <code>EventParams</code> doesn't
     * exists, returns -1.
     *
     * @return The event identifier if applicable, -1 otherwise.
     */
    public int getEventId() {
        return (params != null) ? params.eventId : -1;
    }

    /**
     * Indicates whether this <code>Event</code> has been already handled which 
     * usually means that the event propagation should be stopped.
     *
     * @return  <code>true</code> if this <code>Event</code> has been already
     *          handled, <code>false</code> otherwise.
     */
    public boolean isHandled() {
        return handled;
    }

    /**
     * Sets whether this <code>Event</code> has been already handled which
     * usually means that the event propagation should be stopped.
     *
     * @return  <code>true</code> if this <code>Event</code> has been already
     *          handled, <code>false</code> otherwise.
     */
    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}
