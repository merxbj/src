/*
 * TrifidCipher
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
package bezpecnost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class TrifidCipher {
    public char[] cipher(char[] openChars, TrifidKey key) {
        char[][][] cube = buildCharCubeFromKey(key.getKey());
        Triplet[] mapping = buildCharToCoordinatesMapping(cube);
        char[] filteredOpenChars = filterOpenChars(openChars);
        char[][][] mappedOpenCharsClusters = mapOpenCharsClusters(filteredOpenChars, mapping, key.getClusterSize());
        List<Triplet> cipheredCharsCoordinates = getTransposedTripplets(mappedOpenCharsClusters);
        char[] cipheredChars = buildCharsFromTriplets(cipheredCharsCoordinates, cube);
        return cipheredChars;
    }

    public char[] decipher(char[] closeChars, TrifidKey key) {
        char[][][] cube = buildCharCubeFromKey(key.getKey());
        Triplet[] mapping = buildCharToCoordinatesMapping(cube);
        // TODO: To complete!
        return new char[1];
    }

    private char[][][] buildCharCubeFromKey(char[] key) {
        char[][][] cube = new char[3][3][3];

        boolean used[] = new boolean[255];
        int keyIdx = 0;
        int alphabetIdx = 0;

        for (int layer = 0; layer < 3; layer++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (keyIdx < key.length) {
                        cube[layer][row][column] = key[keyIdx];
                        used[key[keyIdx]] = true;
                        keyIdx++;
                    } else if (keyIdx++ == key.length) {
                        cube[layer][row][column] = '#';
                    } else {
                        while (used['A' + alphabetIdx]) {
                            alphabetIdx++;
                        }
                        cube[layer][row][column] = (char) ('A' + alphabetIdx);
                        alphabetIdx++;
                    }
                }
            }
        }
        
        return cube;
    }

    private Triplet[] buildCharToCoordinatesMapping(char[][][] cube) {
        Triplet[] mapping = new Triplet[255];

        for (int layer = 0; layer < 3; layer++) {
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    char ch = cube[layer][row][column];
                    mapping[ch] = new Triplet(layer, row, column);
                }
            }
        }
        
        return mapping;
    }

    private char[] filterOpenChars(char[] openChars) {
        char[] filteredTmp = new char[openChars.length];
        int filteredTmpIdx = 0;
        for (char ch : openChars) {
            if ((ch >= 'A') && (ch <= 'Z')) {
                filteredTmp[filteredTmpIdx++] = ch;
            }
        }
        return Arrays.copyOf(filteredTmp, filteredTmpIdx);
    }

    private char[][][] mapOpenCharsClusters(char[] filteredOpenChars, Triplet[] mapping, int clusterSize) {
        int clusterCount = (int) Math.ceil(filteredOpenChars.length / (double) clusterSize);
        int clusterIdx = 0;
        char[][][] mappedClusters = new char[clusterCount][3][clusterSize];
        
        int orphanedColumns = filteredOpenChars.length % clusterSize;
        if (orphanedColumns != 0) {
            mappedClusters[clusterCount - 1] = new char[3][orphanedColumns];
        }
        
        for (int charsIdx = 0; charsIdx < filteredOpenChars.length; charsIdx++) {
            if ((charsIdx > 0) && ((charsIdx % clusterSize) == 0)) {
                clusterIdx++;
            }
            Triplet t = mapping[filteredOpenChars[charsIdx]];
            mappedClusters[clusterIdx][0][charsIdx % clusterSize] = t.layer;
            mappedClusters[clusterIdx][1][charsIdx % clusterSize] = t.row;
            mappedClusters[clusterIdx][2][charsIdx % clusterSize] = t.column;
        }
        return mappedClusters;
    }

    private List<Triplet> getTransposedTripplets(char[][][] mappedOpenCharsClusters) {
        List<Triplet> cipheredCharsCoordinates = new ArrayList<Triplet>(mappedOpenCharsClusters.length * mappedOpenCharsClusters[0].length);
        
        int transposedCoordinate = 0;
        char transposedLayer = 0, transposedColumn = 0, transposedRow = 0;
        for (int cluster = 0; cluster < mappedOpenCharsClusters.length; cluster++) {
            for (int row = 0; row < mappedOpenCharsClusters[cluster].length; row++) {
                for (int column = 0; column < mappedOpenCharsClusters[cluster][row].length; column++) {
                    char coordinate = mappedOpenCharsClusters[cluster][row][column];
                    switch (transposedCoordinate++) {
                        case 0:
                            transposedLayer = coordinate;
                            break;
                        case 1:
                            transposedRow = coordinate;
                            break;
                        case 2:
                            transposedColumn = coordinate;
                            cipheredCharsCoordinates.add(new Triplet(transposedLayer, transposedRow, transposedColumn));
                            transposedCoordinate = 0;
                    }
                }
            }
        }
        
        return cipheredCharsCoordinates;
    }

    private char[] buildCharsFromTriplets(List<Triplet> cipheredCharsCoordinates, char[][][] cube) {
        char[] chars = new char[cipheredCharsCoordinates.size()];
        int charsIdx = 0;
        for (Triplet t : cipheredCharsCoordinates) {
            chars[charsIdx++] = cube[t.layer][t.row][t.column];
        }
        return chars;
    }

    private class Triplet {
        public char layer, column, row;

        public Triplet(int layer, int row, int column) {
            this.layer = (char) layer;
            this.row = (char) row;
            this.column = (char) column;
        }
    }
}
