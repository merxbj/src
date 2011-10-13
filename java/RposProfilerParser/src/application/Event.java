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
public class Event {
    private List<Event> subsequentEvents;
    private String recipient;
    private int eventId;
    private long miliseconds;
    private int callDepth;
    private Event parent;

    public Event(String recipient, int eventId, long miliseconds, int callDepth) {
        this.recipient = recipient;
        this.eventId = eventId;
        this.miliseconds = miliseconds;
        this.callDepth = callDepth;
        this.subsequentEvents = new LinkedList<Event>();
        this.parent = null;
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

    public void addEvent(Event event) {
        subsequentEvents.add(event);
        event.parent = this;
    }

    public Event getParent() {
        return parent;
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
