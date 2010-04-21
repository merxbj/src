/*
 * EventHandler
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
 * <code>EventHandler</code> contracts implementing of the {@link #handleEvent(notwa.common.Event)}
 * method which main and only purpose is to notify the subscriber about the
 * <code>Event</code> being fired.
 *
 * @param <T>   The actual <code>Event</code> implementation the implementer is
 *              subscribing for.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public interface EventHandler<T extends Event> {
    /**
     * Notifies the implementer that the given <code>Event</code> occures.
     * 
     * @param e The actul <code>Event</code> occured.
     */
    public void handleEvent(T e);
}
