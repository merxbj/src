/*
 * WorkItemStatus
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

public enum WorkItemStatus {
    PLEASE_RESOLVE(1), WAITING(2), IN_PROGRESS(3), CLOSED(4), DELETED(5);
    
    private int value;
    
    WorkItemStatus(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    private static TreeMap<Integer, WorkItemStatus> map;
    static {
        map = new TreeMap<Integer, WorkItemStatus>();
        for (WorkItemStatus wis : WorkItemStatus.values()) {
            map.put(new Integer(wis.getValue()), wis);
        }
    }
    
    public static WorkItemStatus lookup(int value) {
        return map.get(new Integer(value));
    }
    
    public Color getColor() {
        return ColorManager.getStateColor(this);
    }
}
