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

    Event parseFromLine(String line) {
        String[] tokens = line.split("\\|")[1].split("-");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        int callDepth = Integer.parseInt(tokens[0]);
        String recipient = tokens[1];
        int eventId = Integer.parseInt(tokens[2]);
        long miliseconds = Long.parseLong(tokens[3].split(" ")[0].trim());
        return new Event(recipient, eventId, miliseconds, callDepth);
    }
    
}
