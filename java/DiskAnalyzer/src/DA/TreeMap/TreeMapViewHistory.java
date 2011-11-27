/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.TreeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author mrneo
 */
public class TreeMapViewHistory extends Observable {

    private static TreeMapViewHistory instance;
    private List<SimpleFile> history = new ArrayList<SimpleFile>();
    private int selected = 0;

    public static TreeMapViewHistory getInstance() {
        if (instance == null) {
            instance = new TreeMapViewHistory();
        }

        return instance;
    }

    public void addToHistory(SimpleFile file) {
        history.add(file);
        hasChanged();
        notifyObservers();
    }

    public SimpleFile getLastFromHistory() {
        return history.get(history.size());
    }

    public boolean hasHistory() {
        return history.size() > 1;
    }

    public void resetHistory() {
        history.clear();
        /*for (int i = 1; i < history.size(); i++) {
        history.remove(i);
        }*/
        hasChanged();
        notifyObservers();
    }
}
