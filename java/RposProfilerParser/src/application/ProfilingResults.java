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
public class ProfilingResults implements EventTracer {

    private List<Event> eventTrace;

    public ProfilingResults() {
        this.eventTrace = new LinkedList<Event>();
    }
    
    void toXml(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public void addEvent(Event event) {
        this.eventTrace.add(event);
    }

    public int getCallDepth() {
        return 0;
    }
    
    public int getAcceptedCallDepth() {
        return getCallDepth() + 1;
    }
}
