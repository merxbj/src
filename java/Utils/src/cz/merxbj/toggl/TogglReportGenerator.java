/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.util.List;

/**
 *
 * @author jm185267
 */
public class TogglReportGenerator {
    
    public static void main(String[] args) {
        
        String path = args[0];
        
        EntryLoader loader = EntryLoaderFactory.CreateEntryLoader(path);
        List<TimeEntry> entries = loader.load();
        
        WeeklyReportGenerator generator = new WeeklyReportGenerator();
        WeeklyReport report = generator.generate(entries);
        
        ReportPrinter printer = new WeeklyReportPrinter();
        printer.print(report);
    }
    
}
