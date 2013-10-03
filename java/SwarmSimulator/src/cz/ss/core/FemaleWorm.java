/*
 * FemaleWorm
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

package cz.ss.core;

import java.awt.Color;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class FemaleWorm extends Worm {
    
    protected static final int BLINK_RATE = 20;
    
    protected int drawCount = 0;

    public FemaleWorm(Hatchery hatch) {
        super(hatch);
    }

    public FemaleWorm(Hatchery hatch, Vector pos) {
        super(hatch, pos);
    }


    @Override
    protected Color getColor() {
        Color color = Color.MAGENTA;
        if (isDying) {
            color = (drawCount < BLINK_RATE ? Color.RED : Color.MAGENTA);
            drawCount = (drawCount + 1) % (BLINK_RATE * 2);
        }
        return color;
    }

}
