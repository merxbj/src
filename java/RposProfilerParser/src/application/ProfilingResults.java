/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author merxbj
 */
public class ProfilingResults {

    private Event rootEvent;

    public ProfilingResults() {
        this.rootEvent = new Event("Root", -1, 0, 0);
    }
    
    void toXml(String outputPath) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = (Element) doc.appendChild(doc.createElement("RposProfilingResults"));
        eventTraceToXml(doc, rootElement, rootEvent);
        
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(new File(outputPath)));
    }
    
    private void eventTraceToXml(Document doc, Element parentElement, Event parentEvent) {
        for (Event event : parentEvent.getSubsequentEvents()) {
            Element currentEventElement = (Element) parentElement.appendChild(doc.createElement("Event"));
            currentEventElement.setAttribute("recipient", event.getRecipient());
            currentEventElement.setAttribute("eventId", String.format("%d", event.getEventId()));
            currentEventElement.setAttribute("miliseconds", String.format("%d", event.getMiliseconds()));
            currentEventElement.setAttribute("callDepth", String.format("%d", event.getCallDepth()));
            
            eventTraceToXml(doc, currentEventElement, event);
        }
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

    public ProfilingResults aggregate() {
        Event newRootEvent = new Event("ROOT", -1, 0, 0);
        aggregateEvent(rootEvent, newRootEvent);
        return this;
    }
    
    private void aggregateEvent(Event parrentEvent, Event targetEvent) {
        for (Event event : parrentEvent.getSubsequentEvents()) {
            
        }
    }
}
