/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.integri.cznace;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author mexbik
 */
public class EntryLoader {

    private String csvPath;
    
    public EntryLoader(String csvPath) {
        this.csvPath = csvPath;
    }

    public List<Entry> Load() {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(csvPath), Charset.forName("Windows-1250"));
            final CsvMapReader csvReader = new CsvMapReader(reader, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            final String[] header = createHeader();
            return loadEntries(csvReader, header);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read the import file " + csvPath, ex);
        }
        
        
    }

    private String[] createHeader() {
        return new String[] {Column.LevelOneCode, Column.LevelTwoCode, Column.LevelThreeCode, Column.Name};
    }

    private List<Entry> loadEntries(CsvMapReader csvReader, String[] header) throws IOException{
        List<Entry> entries = new ArrayList<>();
        Map<String, Object> entryMap;
        while ((entryMap = csvReader.read(header, getCellProcessors())) != null) {
            String code = (String)entryMap.get(Column.LevelThreeCode);
            if ((code != null) && !code.trim().equals("")) {
                String name = (String)entryMap.get(Column.Name);
                entries.add(new Entry(code.trim(), name.trim()));
            }
        }
        return entries;
    }
    
    private CellProcessor[] getCellProcessors() {
        return new CellProcessor[4];
    }
    
    private static class Column {
        public static final String LevelOneCode = "LevelOneCode";
        public static final String LevelTwoCode = "LvelTwoCode"; 
        public static final String LevelThreeCode = "LevelThreeCode";
        public static final String Name = "Name";
    }
    
}
