package xpather;

import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class DistinctResults implements Iterable<String> {

    private Collection<String> results;

    public DistinctResults(NodeList nl) {
        results = new TreeSet<String>();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            results.add(String.format("\t%s", n.toString()));
        }
    }

    public Iterator<String> iterator() {
        return this.results.iterator();
    }
}
