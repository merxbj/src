/*
 * Generator
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

package grafgenerator;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Generator {
    public static void main(String[] args) {
        int nodeCount = 250;
        List<Hrana> hrany = new ArrayList<Hrana>(nodeCount*nodeCount);
        for (int i = 1; i <= nodeCount; i++) {
            for (int j = 1; j <= nodeCount; j++) {
                if (i != 1) {
                    int cost = (int) Math.floor(Math.random() * 100) + 1;
                    hrany.add(new Hrana(i, j, cost));
                }
            }
        }

        Collections.shuffle(hrany);
        try {
            FileWriter writer = new FileWriter(new File("d:\\temp\\in.txt"));
            writer.write(String.format("%d\n%d\n", nodeCount, 10));
            for (Hrana h : hrany) {
                writer.write(h.toString());
            }
            writer.write(String.format("%d %d %d", 0,0,0));
            writer.close();
        } catch (Exception ex) {
            
        }

    }

    static class Hrana {
        int from, to, cost;

        public Hrana(int from, int to, int cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }

        @Override
        public String toString() {
            return String.format("%d %d %d\n", from, to, cost);
        }
        
    }
}
