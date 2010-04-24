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

import java.util.TreeMap;

/**
 * Enumeration representing the priority of the <code>WorkItem</code>.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public enum WorkItemPriority {
    
    /**
     * Describing the highest possible priority of the <code>WorkItem</code>.
     * This means that the product isn't usable, testable or simple doesn't works.
     */
    CRITICAL(4) {
        @Override
        public String toString() {
            return "Critical";
        }
    },

    /**
     * Desribing the high priority of the <code>WorkItem</code>.
     * This means that the product isn't usable without very complicated workarounds.
     */
    HIGH(3) {
        @Override
        public String toString() {
            return "High";
        }
    }, 
    
    /**
     * Desribing the usual priority of the <code>WorkItem</code>.
     * This means that the product is fairly usable with some simple workarounds.
     */
    MEDIUM(2) {
        @Override
        public String toString() {
            return "Medium";
        }
    }, 
    
    /**
     * Desribing the low priority of the <code>WorkItem</code>.
     * This means that the product is perfectly usable but there could be improvement
     * that would be appreciated by the user.
     */
    LOW(1) {
        @Override
        public String toString() {
            return "Low";
        }
    },

    /**
     * Desribing the lowest priority of the <code>WorkItem</code>.
     * This means that the product is perfectly usable but there could be improvement
     * that would not be probably even noticed by the user. Usualy some code cleaning,
     * simple color changing or GUI refactoring shall be tracked under such priority.
     */
    NICE_TO_HAVE(0) {
        @Override
        public String toString() {
            return "Nice To Have";
        }
    };
    
    private int value;
    
    /**
     * "Overriden" enum constructor allowing simple association of the enum numerical
     * representation with its object representation.
     *
     * @param value The value of the enumeration.
     */
    WorkItemPriority(int value) {
        this.value = value;
    }
    
    /**
     * Gets the assigned value of this enumeration.
     * 
     * @return The numerical value.
     */
    public int getValue() {
        return this.value;
    }
    
    private static TreeMap<Integer, WorkItemPriority> map;

    /**
     * Static constructor of this enumeration initializing the numerical representation
     * to the object representation mapping.
     */
    static {
        map = new TreeMap<Integer, WorkItemPriority>();
        for (WorkItemPriority wip : WorkItemPriority.values()) {
            map.put(new Integer(wip.getValue()), wip);
        }
    }
    
    /**
     * Gets the object enum representation of the given numberical representation.
     *
     * @param value The numberical representation.
     * @return The object enum representation.
     */
    public static WorkItemPriority lookup(int value) {
        return map.get(new Integer(value));
    }
}
