/*
 * Trifid
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is should be licensed by some license published by 
 *  Czech Technical University.
 *
 */
package cz.merxbj.bezpecnost;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The purpose of this application is to read the input commands from the stdin,
 * based on them perform an <code>trifid</code> encryption/decription and finally 
 * print the results to stdout.
 * 
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

    /**
     * Main entry point:
     *  - prefetch all the commands
     *  - execute them
     *  - fetch the results and print them
     * @param args 
     */
    public static void main(String[] args) {
        InputStream stream = System.in;
        
        List<SecureMessage> messages = loadInputFromStream(stream);
        List<SecureMessage> invertedMessages = invertMessages(messages);

        for (SecureMessage invertedMessage : invertedMessages) {
            System.out.println(invertedMessage);
        }
    }
    
    /**
     * Loads the input commands from the given stream.
     * <b>The expectation is that the input is well-formated.</b>
     * 
     * @param stream
     * @return The list of parsed commands represented as instances of class
     *         SecureMessage.
     */
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
            throw new RuntimeException(ex);
        }
        
        return messages;
    }

    /**
     * Goes over instances of SecureMessage (either OpenedMessage or CipheredMessage)
     * making them exact oposites (invert them). This will cause e.g. OpenedMessage
     * turn to CipheredMessage which is the exact purpose of this excercise.
     * 
     * @param messages
     * @return 
     */
    private static List<SecureMessage> invertMessages(List<SecureMessage> messages) {
        List<SecureMessage> invertedMessages = new ArrayList<SecureMessage>(messages.size());
        TrifidCipher cipher = new TrifidCipher();
        for (SecureMessage message : messages) {
            SecureMessage invertedMessage = message.invert(cipher);
            invertedMessages.add(invertedMessage);
        }
        return invertedMessages;
    }
    
    /**
     * Interface representing the message that could be either encrypted or
     * decrypted. This operation is generalized by the <code>invert</code>
     * method.
     */
    public static interface SecureMessage {
        public SecureMessage invert(TrifidCipher parameter);
        public TrifidKey getKey();
        public String getOriginalCommand();
        public void setOriginalCommand(String command);
    }
    
    public static class OpenedMessage implements SecureMessage {
        char[] openedChars;
        TrifidKey key;
        String originalCommand;

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
            cipheredMessage.setOriginalCommand(originalCommand);
            return cipheredMessage;
        }

        @Override
        public String toString() {
            return new String(openedChars);
        }

        @Override
        public TrifidKey getKey() {
            return key;
        }

        @Override
        public String getOriginalCommand() {
            return this.originalCommand;
        }

        @Override
        public void setOriginalCommand(String command) {
            this.originalCommand = command;
        }
        
    }

    public static class CipheredMessage implements SecureMessage {
        char[] cipheredChars;
        TrifidKey key;
        String originalCommand;

        public CipheredMessage() {
        }

        public CipheredMessage(char[] cipheredChars, TrifidKey key) {
            this.cipheredChars = cipheredChars;
            this.key = key;
        }

        /**
         * Please observe that the encyrpted message is split to groups of 5 which
         * I forgot about and ended up 2 hours of hacking the submitting system.
         * Originally I was splitting the message based on the clustering size.
         * 
         * @return 
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cipheredChars.length; i++) {
                if ((i > 0) && ((i % 5) == 0)) {
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
            openedMessage.setOriginalCommand(originalCommand);
            return openedMessage;
        }

        @Override
        public TrifidKey getKey() {
            return key;
        }
        
        @Override
        public String getOriginalCommand() {
            return this.originalCommand;
        }

        @Override
        public void setOriginalCommand(String command) {
            this.originalCommand = command;
        }
        
    }
    
    /**
     * This is the actual parser of the input from the stdin. It is very error-prone
     * but as it was said before, the expectation is that the input is well-formatted.
     */
    public static class SecureMessageFactory {
        public SecureMessage createMessage(String line) {
            String upperCase = line.toUpperCase();
            SecureMessage message = null;
            switch (upperCase.charAt(0)) {
                case 'E' :
                    message = parseEncryptInstruction(upperCase);
                    break;
                case 'D' :
                    message = parseDecryptInstruction(upperCase);
                    break;
                default :
                    throw new RuntimeException("Unexpected token!");
            }
            
            message.setOriginalCommand(line);
            return message;
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

        /**
         * Make sure that the key is cleared out of any invalid characters!
         * 
         * @param keySubString
         * @return 
         */
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
            
            char[] filteredKeyChars = new char[key.length()];
            int keyCharsIdx = 0;
            boolean[] used = new boolean[255];
            for (char ch : key.toCharArray()) {
                if (!used[ch] && (ch >= 'A') && (ch <= 'Z')) {
                    filteredKeyChars[keyCharsIdx++] = ch;
                    used[ch] = true;
                }
            }
            
            return new TrifidKey(Arrays.copyOf(filteredKeyChars, keyCharsIdx), clustering);
        }
    }
    
    /**
     * This is the class that represents the actual key for the trifid cipher.
     * It is represented by the key itself (<b>it is assumed that the creator will
     * ensure that we are given by a correct key</b>) and by the size of the clustering.
     */
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

        @Override
        public String toString() {
            return "TrifidKey{" + "key=" + new String(key) + ", clustering=" + clustering + '}';
        }
        
    }
    
    /**
     * The trifid cipher algorithm itself.
     */
    public static class TrifidCipher {
        
        /**
         * The encryption process goes as follows:
         *  - The 'Polybius cube' is built based on the key
         *  - For optimalization purposes the mapping from actual letters to tripplets of cube coordinates is built
         *  - The input open message is filtered from any non-encryptable characters
         *  - "Then the coordinates are written out vertically beneath the message" - Wikipedia
         *      - Moreovoer this 'table' is divided to clusters of the given size
         *      - Therefore the resulting data structure is 3-dimensional array (clusters x coordinates x letters)
         * - The 'table' is then read in row basis creating a tripplet of cube coordinates
         * - These tripplets are then converted to actual letters
         * - We are done
         * 
         * @param openedChars
         * @param key
         * @return 
         */
        public char[] cipher(char[] openedChars, TrifidKey key) {
            char[][][] cube = buildCharCubeFromKey(key.getKey());
            Triplet[] mapping = buildCharToCoordinatesMapping(cube);
            char[] filteredOpenedChars = filterOpenedChars(openedChars);
            char[][][] trifidTable = buildTrifidEncryptionTable(filteredOpenedChars, mapping, key.getClusterSize());
            List<Triplet> cipheredCharsCoordinates = getRowBasedTransposedTripplets(trifidTable);
            char[] cipheredChars = buildCharsFromTriplets(cipheredCharsCoordinates, cube);
            return cipheredChars;
        }

        /**
         * The decription process goes as follows:
         *  - The 'Polybius cube' is built based on the key
         *  - For optimalization purposes the mapping from actual letters to tripplets of cube coordinates is built
         *  - The encrypted message is filtered out of any forbidden characters (tipically spaces)
         *  - The encrypted message characters converted to their cube coordinate triplet 
         *    representation are written down in a row-basis into the 'table' of expected size and divided into 
         *    the expected number of clusters of given size
         *  - The 'table' is read in a column-basis and corresponding cube coordinates triplets
         *    are built per each column
         *  - These tripplets are then converted to actual letters
         *  - We are done
         * 
         * @param openedChars
         * @param key
         * @return 
         */
        public char[] decipher(char[] closedChars, TrifidKey key) {
            char[][][] cube = buildCharCubeFromKey(key.getKey());
            Triplet[] mapping = buildCharToCoordinatesMapping(cube);
            char[] filteredClosedChars = filterClosedChars(closedChars);
            char[][][] trifidTable = buildTrifidDecriptionTable(filteredClosedChars, mapping, key.getClusterSize());
            List<Triplet> openedCharsCoordinates = getColumnBasedTransposedTripplets(trifidTable);
            char[] openedChars = buildCharsFromTriplets(openedCharsCoordinates, cube);
            return openedChars;
        }

        /**
         * Iterate the cube and write down
         *  - Key characters
         *  - # character
         *  - remaining alphabet
         * 
         * @param key
         * @return 
         */
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

        /**
         * Go over the cube and create a map from the actual character to its
         * coordinates. This will allow simpler lookup when encrypting/decrypting
         * the message.
         * 
         * @param cube
         * @return 
         */
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

        /**
         * In the opened message only actual letters are allowed.
         * 
         * @param openChars
         * @return 
         */
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

        /**
         * Builds the trifid table in column-basis. That means that for every
         * letter in the open message a new column of coordinates will be writen down.
         * 
         * This whole thing is however divided into the clusters of given size.
         * 
         * @param filteredOpenChars
         * @param mapping
         * @param clusterSize
         * @return 
         */
        private char[][][] buildTrifidEncryptionTable(char[] filteredOpenChars, Triplet[] mapping, int clusterSize) {
            int clusterCount = (int) Math.ceil(filteredOpenChars.length / (double) clusterSize);
            char[][][] trifidTable = createTrifidTable(clusterSize, clusterCount, filteredOpenChars.length);

            // Go over all the letters in the open message, populate the table
            // appropritaly and move to the next cluster every 'clusterSize' letter
            int clusterIdx = 0;
            for (int charsIdx = 0; charsIdx < filteredOpenChars.length; charsIdx++) {
                if ((charsIdx > 0) && ((charsIdx % clusterSize) == 0)) {
                    clusterIdx++;
                }
                Triplet t = mapping[filteredOpenChars[charsIdx]];
                trifidTable[clusterIdx][0][charsIdx % clusterSize] = t.layer;
                trifidTable[clusterIdx][1][charsIdx % clusterSize] = t.row;
                trifidTable[clusterIdx][2][charsIdx % clusterSize] = t.column;
            }
            return trifidTable;
        }

        /**
         * Reads the trifid table in a row-basis.
         * 
         * Important thing is to consider a cluster as a table. That means that
         * the transposition from columns to rows will happen per each cluster.
         * 
         * @param trifidTable
         * @return 
         */
        private List<Triplet> getRowBasedTransposedTripplets(char[][][] trifidTable) {
            List<Triplet> cipheredCharsCoordinates = new ArrayList<Triplet>(trifidTable.length * trifidTable[0].length);

            int transposedCoordinate = 0;
            char transposedLayer = 0, transposedColumn = 0, transposedRow = 0;
            for (int cluster = 0; cluster < trifidTable.length; cluster++) {
                for (int row = 0; row < trifidTable[cluster].length; row++) {
                    for (int column = 0; column < trifidTable[cluster][row].length; column++) {
                        char coordinate = trifidTable[cluster][row][column];
                        switch (transposedCoordinate++ % 3) {
                            case 0:
                                transposedLayer = coordinate;
                                break;
                            case 1:
                                transposedRow = coordinate;
                                break;
                            case 2:
                                transposedColumn = coordinate;
                                cipheredCharsCoordinates.add(new Triplet(transposedLayer, transposedRow, transposedColumn));
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

        /**
         * Builds the trifid table in row-basis. That means that for every
         * letter in the closed message a row within a cluster will be extended by
         * triple of coordinates.
         * 
         * @param filteredClosedChars
         * @param mapping
         * @param clusterSize
         * @return 
         */
        private char[][][] buildTrifidDecriptionTable(char[] filteredClosedChars, Triplet[] mapping, int clusterSize) {
            int clusterCount = (int) Math.ceil(filteredClosedChars.length / (double) clusterSize);
            char[][][] trifidTable = createTrifidTable(clusterSize, clusterCount, filteredClosedChars.length);

            int charsIdx = 0;
            int transposedCoordinate = 0;
            for (int cluster = 0; cluster < clusterCount; cluster++) {
                for (int row = 0; row < 3; row++) {
                    for (int column = 0; column < trifidTable[cluster][row].length; column++) {
                        switch (transposedCoordinate++ % 3) {
                            case 0:
                                trifidTable[cluster][row][column] = mapping[filteredClosedChars[charsIdx]].layer;
                                break;
                            case 1:
                                trifidTable[cluster][row][column] = mapping[filteredClosedChars[charsIdx]].row;
                                break;
                            case 2:
                                trifidTable[cluster][row][column] = mapping[filteredClosedChars[charsIdx]].column;
                                charsIdx++;
                                break;
                        }
                    }
                }
            }

            return trifidTable;
        }

        /**
         * Reads the trifid table in a column-basis.
         * 
         * @param mappedOpenCharsClusters
         * @return 
         */
        private List<Triplet> getColumnBasedTransposedTripplets(char[][][] trifidTable) {
            List<Triplet> triplets = new ArrayList<Triplet>(trifidTable.length * trifidTable[0][0].length);
            for (int cluster = 0; cluster < trifidTable.length; cluster++) {
                for (int column = 0; column < trifidTable[cluster][0].length; column++) {
                    Triplet t = new Triplet(trifidTable[cluster][0][column],
                                            trifidTable[cluster][1][column],
                                            trifidTable[cluster][2][column]);
                    triplets.add(t);
                }
            }
            return triplets;
        }

        /**
         * Only letters and pound sign ('#') are considered as a valid input.
         * The pound sign is an alphabet extension to stuff the alphabet into the
         * 3 x 3 x 3 cube
         * 
         * @param closedChars
         * @return 
         */
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

        /**
         * Creates something which I took the liberty and named Trifid Table.
         * 
         * This is triky, however. The thing is that when you build the whole table and
         * divide it into clusters of a certain size, you will likely end up with
         * the last cluster of not the full size (e.g. 10 letters, clustered by size 3,
         * results in 3 clusters of size 3 and one tiny-tail-cluster of size 1)
         * however the table would be created as 4 full size clusters
         * let us therefore create a new, appropriately sized, tail and replace 
         * the original oversized one
         * 
         * @param clusterSize
         * @param clusterCount
         * @param messageLength
         * @return 
         */
        private char[][][] createTrifidTable(int clusterSize, int clusterCount, int messageLength) {
            char [][][] trifidTable = new char[clusterCount][3][clusterSize];
            
            int orphanedColumns = messageLength % clusterSize;
            if (orphanedColumns != 0) {
                trifidTable[clusterCount - 1] = new char[3][orphanedColumns];
            }

            return trifidTable;
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