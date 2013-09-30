/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.csob.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
        Scanner sc = new Scanner(is,"utf-8");
        Collection<Collection<String>> blocks = new ArrayList<Collection<String>>();
        int totalLinesProcessed = 0;
        while (sc.hasNextLine()) {
            ArrayList<String> block = new ArrayList<String>(11);
            int linesProcessed = 0;
            while (sc.hasNextLine() && linesProcessed < 12) {
                String line = sc.nextLine();
                totalLinesProcessed += 1;
                if (linesProcessed < 11) {
                    block.add(line.trim());
                } else {
                    block.add(block.get(10) + " " + line); // poor csob formatting
                }
                linesProcessed += 1;
            }
            blocks.add(block);

            if (sc.hasNextLine()) {
                String emptyLine = sc.nextLine();
                totalLinesProcessed += 1;
                if (!emptyLine.trim().equals(""))
                {
                    throw new RuntimeException(totalLinesProcessed - 1 + ": Expected empty line but found: " + emptyLine);
                }
            }
        }
        
        return blocks;
    }

    private Collection<Map<String, String>> parseBlocks(Collection<Collection<String>> blocks) {
        Collection<Map<String, String>> tranNvps = new ArrayList<Map<String, String>>(blocks.size());
        for (Collection<String> rawNvps : blocks) {
            Map<String, String> nvps = new HashMap<String, String>();
            for (String rawNvp : rawNvps) {
                String[] pair = parseRawNvp(rawNvp);
                String name = pair[0].trim();
                String value = ((pair.length > 1) && (pair[1] != null) ? pair[1].trim() : "");
                nvps.put(name, value);
            }
            tranNvps.add(nvps);
        }
        return tranNvps;
    }

    private Collection<Transaction> parseNvps(Collection<Map<String, String>> nvps) {
        Collection<Transaction> trans = new ArrayList<Transaction>(nvps.size());
        for (Map<String, String> tranNvps : nvps) {
            Transaction tran = new Transaction();
            tran.parseNvps(tranNvps);
            trans.add(tran);
        }
        return trans;
    }

    private String[] parseRawNvp(String rawNvp) {
        String[] nameAndValue = new String[2];
        int firstColumnPos = rawNvp.indexOf(":");
        nameAndValue[0] = rawNvp.substring(0, firstColumnPos).trim();
        nameAndValue[1] = rawNvp.substring(firstColumnPos + 1).trim();
        return nameAndValue;
    }

}
