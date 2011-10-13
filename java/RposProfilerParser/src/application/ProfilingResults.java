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
public class ProfilingResults {

    private Event rootEvent;

    public ProfilingResults() {
        this.rootEvent = new Event("Root", 0, 0, 0);
    }
    
    void toXml(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Event getRootEvent() {
        return rootEvent;
    }

    public void setRootEvent(Event rootEvent) {
        this.rootEvent = rootEvent;
    }

    public int getCallDepth() {
        return rootEvent.getCallDepth();
    }
}
