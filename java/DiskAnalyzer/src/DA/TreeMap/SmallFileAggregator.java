/*
 * SmallFileAggregator
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
package DA.TreeMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: Parent changing doesn't seem to work well here. 
 * This is what needs to get examined and fixed.
 * @author eTeR
 * @version %I% %G%
 */
public class SmallFileAggregator extends SimpleFile {

    private List<SimpleFile> aggregates = new LinkedList<SimpleFile>();
    
    public SmallFileAggregator(SimpleFile parent) {
        this.size = 0;
        this.name = "0 more files";
        this.aggregator = null;
        this.path = parent.path;
        this.parent = parent;
        this.depth = parent.depth + 1;
        this.isFile = true;
        this.aggregates = new ArrayList<SimpleFile>();
        this.content = null;
    }
    
    public void aggregate(SimpleFile sf) {
        this.aggregates.add(sf);
        this.size += sf.getSize();
        this.name = String.format("%d more files", aggregates.size());
        sf.setParent(this);
        sf.aggregated = true;
    }

    @Override
    protected void drawContent(Graphics2D g, int x, int y, int width, int height) {
        return;
    }
    
    @Override
    public Color getDepthColor(int depth, boolean isFile) {
        return Color.MAGENTA;
    }

    public List<SimpleFile> relax(long fileSizeTreshold) {
        List<SimpleFile> relaxed = new LinkedList<SimpleFile>();
        long sizeDecrease = 0;
        for (SimpleFile sf : aggregates) {
            if (sf.getSize() > fileSizeTreshold) {
                relaxed.add(sf);
                sf.aggregated = false;
                sf.setParent(this.parent);
                sizeDecrease += sf.getSize();
            }
        }
        this.aggregates.removeAll(relaxed);
        this.size -= sizeDecrease;
        this.name = String.format("%d more files", aggregates.size());
        
        return relaxed;
    }
    
    @Override
    public boolean isValid() {
        return aggregates.size() > 0;
    }
}
