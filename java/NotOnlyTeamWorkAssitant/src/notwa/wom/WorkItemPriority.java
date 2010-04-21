/*
 * WorkItemPriority
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
package notwa.wom;

import java.awt.Color;
import java.util.TreeMap;

import notwa.common.ColorManager;

public enum WorkItemPriority {
    CRITICAL(4), IMPORTANT(3), NORMAL(2), NICE_TO_HAVE(1), UNNECESSARY(0);
    
    private int value;
    
    WorkItemPriority(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    private static TreeMap<Integer, WorkItemPriority> map;
    static {
        map = new TreeMap<Integer, WorkItemPriority>();
        for (WorkItemPriority wip : WorkItemPriority.values()) {
            map.put(new Integer(wip.getValue()), wip);
        }
    }
    
    public static WorkItemPriority lookup(int value) {
        return map.get(new Integer(value));
    }
    
    public Color getColor() {
        return ColorManager.getPriorityColor(this);
    }
}
