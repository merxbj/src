/*
 * Trifid
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Testing vectors:
 * 
 * e radio**QQQ**5 Education is what remains after one has forgotten everything he learned in school.
 * end
 *
 * DD#EO QFLMJ YBF#W ARXHI MPROH RH#XL HFRNG IXIAP ITYYW EFQGX K#DKZ RHDY# AQMXK FGLL
 * 
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
class Bezpecnost {

    public static void main(String[] args) {
        /*
        String input = "e radio**QQQ**5 Education is what remains after one has forgotten everything he learned in school.\nend";
        String input = "d radio**QQQ**5 DD#EO QFLMJ YBF#W ARXHI MPROH RH#XL HFRNG IXIAP ITYYW EFQGX K#DKZ RHDY# AQMXK FGLL\nend";
        byte[] inputBytes = input.getBytes();
        ByteArrayInputStream stream = new ByteArrayInputStream(inputBytes);
         */
        InputStream stream = System.in;
        
        List<SecureMessage> messages = loadInputFromStream(stream);
        List<SecureMessage> invertedMessages = invertMessages(messages);

        for (SecureMessage invertedMessage : invertedMessages) {
            System.out.println(invertedMessage);
        }
    }
    
    private static List<SecureMessage> loadInputFromStream(InputStream stream) {
        List<SecureMessage> messages = new ArrayList<SecureMessage>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        SecureMessageFactory factory = new SecureMessageFactory();
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                if (!line.startsWith("end")) {
                    SecureMessage message = factory.createMessage(line);
                    messages.add(message);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        
        return messages;
    }

    private static List<SecureMessage> invertMessages(List<SecureMessage> messages) {
        List<SecureMessage> invertedMessages = new ArrayList<SecureMessage>(messages.size());
        TrifidCipher cipher = new TrifidCipher();
        for (SecureMessage message : messages) {
            SecureMessage invertedMessage = message.invert(cipher);
            invertedMessages.add(invertedMessage);
        }
        return invertedMessages;
    }
    
    public static interface SecureMessage {
        public SecureMessage invert(TrifidCipher parameter);
    }
    
    public static class OpenedMessage implements SecureMessage {
        char[] openedChars;
        TrifidKey key;

        public OpenedMessage() {
        }

        public OpenedMessage(char[] openedChars, TrifidKey key) {
            this.openedChars = openedChars;
            this.key = key;
        }

        @Override
        public SecureMessage invert(TrifidCipher cipher) {
            char[] cipheredChars = cipher.cipher(openedChars, key);
            CipheredMessage cipheredMessage = new CipheredMessage(cipheredChars, key);
            return cipheredMessage;
        }

        @Override
        public String toString() {
            return new String(openedChars);
        }
    }

    public static class CipheredMessage implements SecureMessage {
        char[] cipheredChars;
        TrifidKey key;

        public CipheredMessage() {
        }

        public CipheredMessage(char[] cipheredChars, TrifidKey key) {
            this.cipheredChars = cipheredChars;
            this.key = key;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cipheredChars.length; i++) {
                if ((i > 0) && ((i % key.getClusterSize()) == 0)) {
                    builder.append(" ");
                }
                builder.append(cipheredChars[i]);
            }
            return builder.toString();
        }

        @Override
        public SecureMessage invert(TrifidCipher cipher) {
            char[] openedChars = cipher.decipher(cipheredChars, key);
            OpenedMessage openedMessage = new OpenedMessage(openedChars, key);
            return openedMessage;
        }
    }
    
    public static class SecureMessageFactory {
        public SecureMessage createMessage(String line) {
            String upperCase = line.toUpperCase();
            switch (upperCase.charAt(0)) {
                case 'E' :
                    return parseEncryptInstruction(upperCase);
                case 'D' :
                    return parseDecryptInstruction(upperCase);
                default :
                    throw new RuntimeException("Unexpected token!");
            }
        }

        private SecureMessage parseEncryptInstruction(String line) {
            final int beginOfKey = 2;
            final int endOfKey = line.indexOf(" ", beginOfKey);

            TrifidKey key = parseKey(line.substring(beginOfKey, endOfKey));
            String text = line.substring(endOfKey + 1);

            return new OpenedMessage(text.toCharArray(), key);
        }

        private SecureMessage parseDecryptInstruction(String line) {
            final int beginOfKey = 2;
            final int endOfKey = line.indexOf(" ", beginOfKey);

            TrifidKey key = parseKey(line.substring(beginOfKey, endOfKey));
            String text = line.substring(endOfKey + 1);

            return new CipheredMessage(text.toCharArray(), key);
        }

        private TrifidKey parseKey(String keySubString) {
            String[] keyAndClustering = keySubString.split("\\*\\*QQQ\\*\\*");

            String key = keyAndClustering[0];
            String clusteringString = keyAndClustering[1];

            int clustering = 0;
            try {
                clustering = Integer.parseInt(clusteringString); 
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            return new TrifidKey(key.toCharArray(), clustering);
        }
    }
    
    public static class TrifidKey {
        private char[] key;
        private int clustering;

        public TrifidKey(char[] key, int clustering) {
            this.key = key;
            this.clustering = clustering;
        }

        public int getClusterSize() {
            return clustering;
        }

        public char[] getKey() {
            return key;
        }
    }
    
    public static class TrifidCipher {
        public char[] cipher(char[] openedChars, TrifidKey key) {
            char[][][] cube = buildCharCubeFromKey(key.getKey());
            Triplet[] mapping = buildCharToCoordinatesMapping(cube);
            char[] filteredOpenedChars = filterOpenedChars(openedChars);
            char[][][] mappedOpenCharsClusters = mapOpenCharsClusters(filteredOpenedChars, mapping, key.getClusterSize());
            List<Triplet> cipheredCharsCoordinates = getRowBasedTransposedTripplets(mappedOpenCharsClusters);
            char[] cipheredChars = buildCharsFromTriplets(cipheredCharsCoordinates, cube);
            return cipheredChars;
        }

        public char[] decipher(char[] closedChars, TrifidKey key) {
            char[][][] cube = buildCharCubeFromKey(key.getKey());
            Triplet[] mapping = buildCharToCoordinatesMapping(cube);
            char[] filteredClosedChars = filterClosedChars(closedChars);
            char[][][] mappedClosedCharsClusters = mapCipheredCharsClusters(filteredClosedChars, mapping, key.getClusterSize());
            List<Triplet> openedCharsCoordinates = getColumnBasedTransposedTripplets(mappedClosedCharsClusters);
            char[] openedChars = buildCharsFromTriplets(openedCharsCoordinates, cube);
            return openedChars;
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

        private char[] filterOpenedChars(char[] openChars) {
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
            char[][][] mappedClusters = new char[clusterCount][3][clusterSize];

            int orphanedColumns = filteredOpenChars.length % clusterSize;
            if (orphanedColumns != 0) {
                mappedClusters[clusterCount - 1] = new char[3][orphanedColumns];
            }

            int clusterIdx = 0;
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

        private List<Triplet> getRowBasedTransposedTripplets(char[][][] mappedOpenCharsClusters) {
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

        private char[][][] mapCipheredCharsClusters(char[] cipheredChars, Triplet[] mapping, int clusterSize) {
            int clusterCount = (int) Math.ceil(cipheredChars.length / (double) clusterSize);
            char[][][] mappedClusters = new char[clusterCount][3][clusterSize];

            int orphanedColumns = cipheredChars.length % clusterSize;
            if (orphanedColumns != 0) {
                mappedClusters[clusterCount - 1] = new char[3][orphanedColumns];
            }

            int charsIdx = 0;
            int transposedCoordinate = 0;
            for (int cluster = 0; cluster < clusterCount; cluster++) {
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < mappedClusters[cluster][row].length; column++) {
                        switch (transposedCoordinate++ % 3) {
                            case 0:
                                mappedClusters[cluster][row][column] = mapping[cipheredChars[charsIdx]].layer;
                                break;
                            case 1:
                                mappedClusters[cluster][row][column] = mapping[cipheredChars[charsIdx]].row;
                                break;
                            case 2:
                                mappedClusters[cluster][row][column] = mapping[cipheredChars[charsIdx]].column;
                                charsIdx++;
                                break;
                        }
                    }
                }
            }

            return mappedClusters;
        }

        private List<Triplet> getColumnBasedTransposedTripplets(char[][][] mappedClosedCharsClusters) {
            List<Triplet> triplets = new ArrayList<Triplet>(mappedClosedCharsClusters.length * mappedClosedCharsClusters[0][0].length);
            for (int cluster = 0; cluster < mappedClosedCharsClusters.length; cluster++) {
                for (int column = 0; column < mappedClosedCharsClusters[cluster][0].length; column++) {
                    Triplet t = new Triplet(mappedClosedCharsClusters[cluster][0][column],
                                            mappedClosedCharsClusters[cluster][1][column],
                                            mappedClosedCharsClusters[cluster][2][column]);
                    triplets.add(t);
                }
            }
            return triplets;
        }

        private char[] filterClosedChars(char[] closedChars) {
            char[] filteredTmp = new char[closedChars.length];
            int filteredTmpIdx = 0;
            for (char ch : closedChars) {
                if (((ch >= 'A') && (ch <= 'Z')) || (ch == '#')) {
                    filteredTmp[filteredTmpIdx++] = ch;
                }
            }
            return Arrays.copyOf(filteredTmp, filteredTmpIdx);
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
    
}

/*
 * try {
            boolean endEncountered = false;
            while ((stream.available() > 0) && !endEncountered) {
                char character = (char) stream.read();
                switch (character) {
                    case 'd':
                        SecureMessage cipheredMessage = new CipheredMessage();
                        if (cipheredMessage.parse(stream)) {
                            messages.add(cipheredMessage);
                        }
                        break;
                    case 'e':
                        if (stream.available() > 2) {
                            SecureMessage openedMessage = new OpenedMessage();
                            if (openedMessage.parse(stream)) {
                                messages.add(openedMessage);
                            }
                        } else {
                            // assume that when after 'e' there are only 2 or less characters left
                            // in the stream, it is the 'end' at the end of the input
                            endEncountered = true;
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
 */