/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author merxbj
 */
public class Parser {
    
    private EventParser eventParser;

    public Parser() {
        eventParser = new EventParser();
    }
    

    ProfilingResults parse(String filePath) throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            return parseFromReader(reader);
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                ;
            }
        } 
        
    }

    /**
     * Parse the event call trace from the supplied reader.
     * The expectation is that all the calls are reported in sequential order
     * @param reader
     * @return
     * @throws Exception 
     */
    private ProfilingResults parseFromReader(BufferedReader reader) throws Exception {
        ProfilingResults results = new ProfilingResults();
        Event currentEvent = results.getRootEvent();
        while (reader.ready()) {
            String line = reader.readLine();
            if (eventParser.isOpeningEvent(line)) {
                Event event = eventParser.parseEventFromOpeningLine(line);
                currentEvent.addEvent(event);
                currentEvent = event;
            } else {
                eventParser.updateEventFromEndingLine(currentEvent, line);
                currentEvent = currentEvent.getParent();
            }
        }
        return results;//.aggregate();
    }
}
