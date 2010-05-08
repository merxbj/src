/*
 * GuiEventParams
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

package notwa.gui;

import notwa.common.EventParams;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class GuiEventParams extends EventParams {

    public static final int MENU_EVENT_CONFIGURE            = 1;
    public static final int MENU_EVENT_FILTERING            = 2;
    public static final int MENU_EVENT_USER_MANAGEMENT      = 3;
    public static final int MENU_EVENT_ASSIGNMENT_MANAGER   = 4;
    public static final int MENU_EVENT_EXIT                 = 5;
    public static final int MENU_EVENT_SYNC_AND_REFRESH     = 6;
    public static final int ACTION_EVENT_HIDE_DETAIL        = 7;
    public static final int TABLE_ROW_SORTER_CHANGED        = 8;
    public static final int SELECTED_ROW_CHANGED            = 9;
    public static final int MENU_EVENT_PROJECT_MANAGEMENT   = 10;

    public GuiEventParams(int eventId) {
        super(eventId);
    }

    public GuiEventParams(int eventId, Object params) {
        super(eventId, params);
    }

}
