/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

/**
 *
 * @author jm185267
 */
public class EntryLoaderFactory {

    static EntryLoader CreateEntryLoader(String path) {
        int lastDotPos = path.lastIndexOf(".");
        String extension = path.substring(lastDotPos + 1, path.length());
        switch (extension)
        {
            case "csv":
                return new CsvEntryLoader(path);
            default:
                throw new RuntimeException("Unsupported file format for import file" + path);
        }
    }
    
}
