/*
 * ColorManager
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

import java.awt.Color;

import notwa.wom.WorkItemPriority;
import notwa.wom.WorkItemStatus;

/**
 * Centralized color management, all style coloring should be here for better
 * styling of application
 * 
 * @author Tomas Studnicka
 * @version %I% %G%
 */
public class ColorManager {
    private static Color lightRed = new Color(255,190,190);
    private static Color lighterRed = new Color(255,210,210);
    private static Color littleRed = new Color(255,235,235);
    private static Color white = new Color(255,255,255);
    private static Color lightGreen = new Color(210,255,160);
    private static Color lightGray = new Color(230,230,230);
    private static Color lightBlue = new Color(170, 220, 255);
    
    /**
     * Gets the color for the given priority.
     * 
     * @param wip Priority to be colorfully expressed.
     * @return <code>Color</code> expressing the given priority.
     */
    public static Color getPriorityColor(WorkItemPriority wip) {
        switch(wip) {
            case CRITICAL:
                return lightRed;
            case HIGH:
                return lighterRed;
            case MEDIUM:
                return white;
            case LOW:
                return lightGreen;
            case NICE_TO_HAVE:
                return lightGray;
            default:
                return white;
        }
        
    }
    
    /**
     * Gets the color for the given status.
     * 
     * @param wis Status to be colorfully expressed.
     * @return <code>Color</code> expressing the given status.
     */
    public static Color getStatusColor(WorkItemStatus wis) {
        switch(wis) {
            case EVALUATE:
                return lighterRed;
            case RESOLVE:
                return lightRed;
            case WATCH:
                return littleRed;
            case REEVALUATE:
                return lighterRed;
            case CLOSED:
                return white;
            case VERIFIED:
                return lightGreen;
            case CODE_REVIEW:
                return lightGray;
            case CODE_REVIEW_APPROVED:
                return lightGreen;
            case PLEASE_VERIFY:
                return littleRed;
            default:
                return white;
        }
    }
    
    /**
     * Gets the color of the odd row of the table.
     * @return The <code>Color</code>
     */
    public static Color getTableOddRowColor() {
        return white;
    }
    
    /**
     * Gets the color of the even row of the table.
     * @return The <code>Color</code>
     */
    public static Color getTableEvenRowColor() {
        return lightGray;
    }
    
    /**
     * Gets the color of the selected row of the table.
     * @return The <code>Color</code>
     */
    public static Color getTableSelectedRowColor() {
        return lightBlue;
    }
    
    /**
     * Gets the color for WIT's that has date today or after.
     * @return The <code>Color</code>
     */    
    public static Color getExpectingDateWarningColor() {
        return lightRed;
    }
    
    /**
     * Gets the color for WIT's that has 7 days to completion.
     * @return The <code>Color</code>
     */
    public static Color getExpectingDateAttentionColor() {
        return lighterRed;
    }
}
