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

import java.util.TreeMap;

/**
 * Enumeration representing the status of the <code>WorkItem</code>.
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public enum WorkItemStatus {
    
    /**
     * The initial state of every <code>WorkItem</code>.
     * The <code>WorkItem</code> is waiting to be evaluated and eventually assigned.
     */
    EVALUATE(0) {
        @Override
        public String toString() {
            return "Please Evaluate";
        }
    },
    
    /**
     * The <code>WorkItem</code> has been evaluated as defect or enhancement and
     * is currently being resolved.
     */
    RESOLVE(1) {
        @Override
        public String toString() {
            return "Please Resolve";
        }
    },

    /**
     * The <code>WorkItem</code> has been evaluated as unreproducible but the
     * suspicion that it could raise again still prevails.
     */
    WATCH(2) {
        @Override
        public String toString() {
            return "Please Watch";
        }
    },

    /**
     * The <code>WorkItem</code> has been considered as unjustified. It should be
     * therefore returned back to its originator to be eventually closed.
     */
    REEVALUATE(3) {
        @Override
        public String toString() {
            return "Please Reevaluate";
        }
    },

    /**
     * The <code>WorkItem</code> has been considered unjastified and has been therefore
     * brought into the close state.
     */
    CLOSED(4) {
        @Override
        public String toString() {
            return "Closed";
        }
    },

    /**
     * The <code>WorkItem</code> has been evaluated as defect or enhancement and
     * it has been already resolved and verified.
     */
    VERIFIED(5) {
        @Override
        public String toString() {
            return "Verified";
        }
    },

    /**
     * The sollution for the <code>WorkItem</code> has been found and coded
     * and the reviewer is suposed to make a code review to verify that
     * the actual coder didn't make any potential pitfalls.
     */
    CODE_REVIEW(6) {
        @Override
        public String toString() {
            return "Code Review";
        }
    },

    /**
     * The <code>WorkItem</code> solution has been reviewed and approved.
     */
    CODE_REVIEW_APPROVED(7) {
        @Override
        public String toString() {
            return "Code Review - Approved";
        }
    };
    
    private int value;
    
    /**
     * "Overriden" enum constructor allowing simple association of the enum numerical
     * representation with its object representation.
     *
     * @param value The value of the enumeration.
     */
    WorkItemStatus(int value) {
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
    
    private static TreeMap<Integer, WorkItemStatus> map;
    
    /**
     * Static constructor of this enumeration initializing the numerical representation
     * to the object representation mapping.
     */
    static {
        map = new TreeMap<Integer, WorkItemStatus>();
        for (WorkItemStatus wis : WorkItemStatus.values()) {
            map.put(new Integer(wis.getValue()), wis);
        }
    }
    
    /**
     * Gets the object enum representation of the given numberical representation.
     *
     * @param value The numberical representation.
     * @return The object enum representation.
     */
    public static WorkItemStatus lookup(int value) {
        return map.get(new Integer(value));
    }
}
