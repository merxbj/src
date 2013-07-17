/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.toggl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

/**
 *
 * @author jm185267
 */
public class CsvEntryLoader implements EntryLoader {

    private String importFilePath;
    private HashMap<String, Integer> importFileFormat;
   
    
    public CsvEntryLoader(String path) {
        importFilePath = path;
        importFileFormat = new HashMap<>();
    }

    @Override
    public List<TimeEntry> load() {
        
        try (BufferedReader reader = new BufferedReader(new FileReader(importFilePath))) {
            return loadEntries(reader);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to read the import file " + importFilePath, ex);
        }
        
    }

    private List<TimeEntry> loadEntries(BufferedReader reader) throws IOException {
        List<TimeEntry> entries = new ArrayList<>();
        boolean firstLine = true;
        while (reader.ready()) {
            String line = reader.readLine();
            if (firstLine) {
                ParseFileFormat(line);
                firstLine = false;
            } else {
                entries.add(parseLine(line));
            }
        }
        return entries;
    }

    private TimeEntry parseLine(String line) {
        String[] tokens = line.split(",", 14);
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }

        TimeEntry entry = new TimeEntry();
        entry.setUser(tokens[importFileFormat.get("User")]);
        entry.setEmail(tokens[importFileFormat.get("Email")]);
        entry.setClient(tokens[importFileFormat.get("Client")]);
        entry.setProject(tokens[importFileFormat.get("Project")]);
        entry.setTask(tokens[importFileFormat.get("Task")]);
        entry.setDescription(tokens[importFileFormat.get("Description")]);
        entry.setBillable(parseBillable(tokens[importFileFormat.get("Billable")]));
        entry.setStartDateTime(parseDateTime(tokens[importFileFormat.get("Start date")], tokens[importFileFormat.get("Start time")]));
        entry.setEndDateTime(parseDateTime(tokens[importFileFormat.get("End date")], tokens[importFileFormat.get("End time")]));
        entry.setDuration(parseDuration(tokens[importFileFormat.get("Duration")]));
        entry.setTags(parseTags(tokens[importFileFormat.get("Tags")]));
        entry.setAmountUSD(parseAmount(tokens[importFileFormat.get("Amount (USD)")]));

        return ValidateEntry(entry);
    }

    private void ParseFileFormat(String line) {
        String[] tokens = line.split(",");
        for (int i = 0; i < tokens.length; i++) {
            importFileFormat.put(tokens[i], i);
        }
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
        if (!entry.calculateDuration().isEqual(entry.getDuration())) {
            System.out.printf("WARNING! Provided duration %s does not match to calculated duration %s!\n", Common.getPeriodFormatter().print(entry.getDuration().toPeriod()), Common.getPeriodFormatter().print(entry.calculateDuration().toPeriod()));
        }
        return entry;
    }

    
}
