package xpather;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.NodeList;

public class CountResults implements Iterable<String> {

    private Collection<String> results;

    public CountResults(NodeList nl) {
        results = new ArrayList<String>(1);
        results.add(String.format("\t%d nodes returned by query.", nl.getLength()));
    }

    public Iterator<String> iterator() {
        return this.results.iterator();
    }
}
