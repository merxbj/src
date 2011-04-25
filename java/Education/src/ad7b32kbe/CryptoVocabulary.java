/*
 * CryptoVocabulary
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

package ad7b32kbe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CryptoVocabulary {

    private HashMap<String, String> vocabulary;

    public CryptoVocabulary() {
        vocabulary = new HashMap<String, String>();
    }
    
    public int loadFromFile(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            while (reader.ready()) {
                String word = reader.readLine();
                vocabulary.put(word, word);
            }
            return vocabulary.size();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public boolean isSentenseNoSpaces(String sentenseWithoutSpaces) {
        List<String> words = new ArrayList<String>(4);
        int totalSize = 0;
        for (String word : vocabulary.keySet()) {
            if (sentenseWithoutSpaces.contains(word)) {
                words.add(word);
                totalSize += word.length();
                if (totalSize + 10 >= sentenseWithoutSpaces.length()) {
                    System.out.println("Found maybe sentense: " + sentenseWithoutSpaces + " based on words:");
                    for (String w : words) {
                        System.out.println(w);
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isWord(String word) {
        return vocabulary.containsKey(word);
    }
    
}
