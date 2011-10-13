/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author merxbj
 */
public class Event implements EventTracer {
    private List<Event> subsequentEvents;
    private String recipient;
    private int eventId;
    private long miliseconds;
    private int callDepth;

    public Event(String recipient, int eventId, long miliseconds, int callDepth) {
        this.recipient = recipient;
        this.eventId = eventId;
        this.miliseconds = miliseconds;
        this.callDepth = callDepth;
        this.subsequentEvents = new LinkedList<Event>();
    }

    public int getEventId() {
        return eventId;
    }

    public long getMiliseconds() {
        return miliseconds;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getCallDepth() {
        return callDepth;
    }

    @Override
    public void addEvent(Event event) {
        subsequentEvents.add(event);
    }
    
    @Override
    public int getAcceptedCallDepth() {
        return getCallDepth() + 1;
    }

    @Override
    public String toString() {
        String toReturn = "";
        for (int i = 1; i < getCallDepth(); i++) {
            toReturn += " ";
        }

        return toReturn + "Event{" + "recipient=" + recipient + ", eventId=" + eventId + ", miliseconds=" + miliseconds + ", callDepth=" + callDepth + '}';
    }
    
    
}
