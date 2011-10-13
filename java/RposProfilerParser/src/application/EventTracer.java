/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

/**
 *
 * @author merxbj
 */
public interface EventTracer {
    public void addEvent(Event event);
    public int getAcceptedCallDepth();
}
