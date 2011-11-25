/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author jmerxbauer
 */
public class TooManyPeopleStarvedException extends HamurabiMismanagementException {

    private int peopleStarved;
    
    public TooManyPeopleStarvedException(int peopleStarved) {
        this.peopleStarved = peopleStarved;
    }

    public int getPeopleStarved() {
        return peopleStarved;
    }
}
