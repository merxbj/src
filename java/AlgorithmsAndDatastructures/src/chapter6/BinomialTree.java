/*
 * BinomialTree
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package chapter6;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class BinomialTree<K extends Comparable, V> implements AddressablePriorityQueue<K, V>{

    private APQItem<K, V> min;
    private List<APQItem<K, V>> roots;

    public BinomialTree() {
        roots = new LinkedList<APQItem<K, V>>();
    }

    public void decreaseKey(APQItem<K, V> item, K key) {
        item.e.key = key;
        if (item.parent != null) {
            cut(item);
        }
    }

    public APQItem<K, V> insertItem(Element<K, V> e) {
        APQItem<K, V> item = new APQItem<K, V>(e);
        newTree(item);
        return item;
    }

    public void merge(AddressablePriorityQueue<K, V> other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(APQItem<K, V> item) {
        // there min.e.key stands for negative infinity
        decreaseKey(item, min.e.key);
        // will remove the item based on assumption that the last minimum was 
        // replaced by the item with key that is less or EQUALS than it
        deleteMin();
    }

    public void build(Element<K, V>[] a) {
        for (Element<K, V> e : a) {
            insert(e);
        }
    }

    public Element<K, V> deleteMin() {
        if (min != null) {
            Element<K, V> e = min.e;
            for (APQItem<K,V> ch : min.children) {
                cut(ch);
            }
            return e;
        }
        return null;
    }

    public void insert(Element<K, V> e) {
        insertItem(e);
    }

    public Element<K, V> min() {
        return min.e;
    }

    private void newTree(APQItem<K,V> item) {
        roots.add(item);
        if (min == null || item.e.compareTo(min.e) <= 0) {
            min = item;
        }
    }

    private void cut(APQItem<K,V> item) {
        if (item.parent != null) {
            item.parent.children.remove(item);
        }
        newTree(item);
    }

}
