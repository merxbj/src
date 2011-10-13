/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

/**
 *
 * @author merxbj
 */
public class EventParser {

    boolean isOpeningEvent(String line) {
        String[] tokens = line.split("\\|")[1].split("-");
        return tokens[0].equals(" + ");
    }
    
    Event parseEventFromOpeningLine(String line) {
        String[] tokens = line.split("\\|")[1].split("-");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        
        int callDepth = Integer.parseInt(tokens[1]);
        String recipient = tokens[2];
        int eventId = Integer.parseInt(tokens[3]);
        return new Event(recipient, eventId, 0, callDepth);
    }
    
    void parseMilisecondsFromEndingLine(String line) {
        String[] tokens = line.split("\\|")[1].split("-");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        
        int callDepth = Integer.parseInt(tokens[1]);
        String recipient = tokens[2];
        int eventId = Integer.parseInt(tokens[3]);
        long miliseconds = Long.parseLong(tokens[4].split(" ")[0].trim());
        
        
        return new Event(recipient, eventId, miliseconds, callDepth);
    }
    
}
