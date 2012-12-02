/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author merxbj
 */
public class TextFilesCsobParser implements CsobParser {

    public Collection<Transaction> parse(InputStream is) {
        Collection<Collection<String>> blocks = parseFile(is);
        Collection<Map<String,String>> nvps = parseBlocks(blocks);
        return parseNvps(nvps);
    }

    private Collection<Collection<String>> parseFile(InputStream is) {
        Scanner sc = new Scanner(is);
        Collection<String> block = new ArrayList<String>(12);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            
        }
    }

    private Collection<Map<String, String>> parseBlocks(Collection<Collection<String>> blocks) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Collection<Transaction> parseNvps(Collection<Map<String, String>> nvps) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
