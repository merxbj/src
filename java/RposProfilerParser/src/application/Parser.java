/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

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
        ArrayList<String> lines = new ArrayList<String>();
        while (reader.ready()) {
            String line = reader.readLine();
            lines.add(line);
        }

        ProfilingResults results = new ProfilingResults();
        buildResults(lines, results);
        return results;
    }

    private void buildResults(ArrayList<Event> events, Indexer indexer, EventTracer tracer) {
        while (indexer.hasNext()) {
            Event event = events.get(indexer.getIndex());
            if (event.getCallDepth() == (tracer.getAcceptedCallDepth())) {
                tracer.addEvent(event);
                indexer.moveNext();
                System.out.println(event);
            } else if (event.getCallDepth() < tracer.getAcceptedCallDepth()) {
                return;
            } else if (event.getCallDepth() > tracer.getAcceptedCallDepth()) {
                buildResults(events, indexer, events.get(indexer.getIndex() - 1));
            }
        }
    }
    
    private class Indexer {
        private int index;
        private int maxIndex;
        
        public Indexer(Collection<?> col) {
            maxIndex = col.size();
            index = 0;
        }
        
        public void moveNext() {
            index++;
        }
        
        public void movePrevious() {
            index--;
        }
        
        public int getIndex() {
            return index;
        }
        
        public boolean hasNext() {
            return (index < maxIndex);
        }
    }
    
}
