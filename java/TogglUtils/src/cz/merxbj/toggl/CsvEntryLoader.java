/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author jm185267
 */
public class CsvEntryLoader implements EntryLoader {
    private String importFilePath;
    
    public CsvEntryLoader(String path) {
        importFilePath = path;
    }

    @Override
    public List<TimeEntry> load() {
        try {
            final CsvMapReader csvReader = new CsvMapReader(new FileReader(importFilePath), CsvPreference.STANDARD_PREFERENCE);
            final String[] header = csvReader.getHeader(true);
            return loadEntries(csvReader, header);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read the import file " + importFilePath, ex);
        }
        
    }

    private List<TimeEntry> loadEntries(final CsvMapReader reader, final String[] header) throws IOException {
        List<TimeEntry> entries = new ArrayList<>();
        Map<String, Object> timeEntryMap;
        while ((timeEntryMap = reader.read(header, getCellProcessors())) != null) {
            entries.add(parseTimeEntryMap(timeEntryMap));
        }
        return entries;
    }

    private TimeEntry parseTimeEntryMap(Map<String, Object> timeEntryMap) {
        TimeEntry entry = new TimeEntry();
        entry.setUser((String)timeEntryMap.get("User"));
        entry.setEmail((String)timeEntryMap.get("Email"));
        entry.setClient((String)timeEntryMap.get("Client"));
        entry.setProject((String)timeEntryMap.get("Project"));
        entry.setTask((String)timeEntryMap.get("Task"));
        entry.setDescription((String)timeEntryMap.get("Description"));
        entry.setBillable(parseBillable((String)timeEntryMap.get("Billable")));
        entry.setStartDateTime(parseDateTime((String)timeEntryMap.get("Start date"), (String)timeEntryMap.get("Start time")));
        entry.setEndDateTime(parseDateTime((String)timeEntryMap.get("End date"), (String)timeEntryMap.get("End time")));
        entry.setDuration(parseDuration((String)timeEntryMap.get("Duration")));
        entry.setTags(parseTags((String)timeEntryMap.get("Tags")));
        entry.setAmountUSD(parseAmount((String)timeEntryMap.get("Amount (USD)")));

        return ValidateEntry(entry);
    }

    private boolean parseBillable(String billableString) {
        switch (billableString.toLowerCase())
        {
            case "yes":
                return true;
            case "no":
                return false;
            default:
                return false;
        }
    }

    private DateTime parseDateTime(String dateString, String timeString) {
        DateTime date = DateTime.parse(dateString, Common.getDateFormatter());
        DateTime time = DateTime.parse(timeString, Common.getTimeFormatter());
        return date.plus(time.getMillisOfDay());
    }

    private Duration parseDuration(String durationString) {
        Period p = Common.getPeriodFormatter().parsePeriod(durationString);
        return p.toStandardDuration();
    }

    private List<String> parseTags(String tagsString) {
        List<String> tags = new ArrayList<>();
        tags.add(tagsString); // I just don't care now
        return tags;
    }

    private BigDecimal parseAmount(String amountString) {
        if ((amountString == null) || (amountString.isEmpty())) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(amountString);
    }

    private TimeEntry ValidateEntry(TimeEntry entry) {
        Duration calculated = entry.calculateDuration();
        Duration provided = entry.getDuration();
        double ratio = (double)Math.min(calculated.getMillis(), provided.getMillis()) / Math.max(calculated.getMillis(), provided.getMillis());
        if (ratio < 0.90) {
            System.out.printf("WARNING! Provided duration %s does not fit within a reasonable ratio with calculated duration %s!\n", Common.getPeriodFormatter().print(provided.toPeriod()), Common.getPeriodFormatter().print(calculated.toPeriod()));
        }
        return entry;
    }

    private CellProcessor[] getCellProcessors() {
        CellProcessor[] cp = new CellProcessor[14];
        for (int i = 0; i < cp.length; i++) {
            cp[i] = new ConvertNullTo("");
        }
        return cp;
    }
}
