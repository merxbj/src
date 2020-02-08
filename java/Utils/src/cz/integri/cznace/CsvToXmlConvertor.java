/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.integri.cznace;

import java.util.List;

/**
 *
 * @author mexbik
 */
public class CsvToXmlConvertor {

    public static void main(String[] args) {
        EntryLoader loader = new EntryLoader(args[0]);
        List<Entry> entries = loader.Load();
        
        long max = 0;
        for (Entry entry : entries) {
            if (entry.getName().length() > max) {
                max = entry.getName().length();
            }
        }
        System.out.println("Nejdelsi jmeno: " + max);

        XmlFileGenerator generator = new XmlFileGenerator(args[1]);
        generator.generate(entries);
    }

}
