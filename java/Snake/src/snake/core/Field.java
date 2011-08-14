/*
 * Field
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
package snake.core;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class Field implements Drawable {
    
    private Dimension size;

    public void init() {
        
    }

    public Dimension getSize() {
        return new Dimension(size);
    }

    public void setSize(Dimension size) {
        this.size = new Dimension(size);
    }

    public void draw(Graphics2D g) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
