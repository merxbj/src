/*
 * BinaryHeap
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

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class BinaryHeap<K extends Comparable, V> implements PriorityQueue<K,V> {

    Comparable[] heap; // will hold array of Element<K,V>
    int size;

    public BinaryHeap(int maxSize) {
        heap = new Comparable[maxSize];
        size = 0;
    }

    public void build(Element<K, V>[] a) {
        Element<K, V>[] clone = (Element<K, V>[]) a.clone();
        buildHeapBackwards(clone);
    }

    public void insert(Element<K, V> e) {
        if (heap.length == size) {
            throw new ArrayIndexOutOfBoundsException("Heap is full!");
        }
        heap[size] = e;
        siftUp(size++);
    }

    public Element<K, V> deleteMin() {
        if (size > 0) {
            Element<K, V> e = (Element<K, V>) heap[0];
            heap[0] = heap[--size];
            siftDown(0);
            return e;
        }
        return null;
    }

    public Element<K, V> min() {
        return (Element<K, V>) heap[0]; // ensured by the heap invariant
    }

    protected void siftUp(int i) {
        int parent = (int) Math.ceil(i/2.0) - 1;
        if ((i == 0) || heap[parent].compareTo(heap[i]) <= 0) {
            return; // this is the only heap element or invariant holds with root
        }
        swap(i, parent);
        siftUp(parent);
    }

    protected void siftDown(int i) {
        if ((2*i + 1) < size) {
            int m = 0;
            if (((2*i + 2) >= size) || (heap[2*i + 1].compareTo(heap[2*i + 2]) <= 0)) {
                m = 2*i + 1;
            } else {
                m = 2*i + 2;
            }
            if (heap[i].compareTo(heap[m]) >= 0) {
                swap(i, m);
                siftDown(m);
            }
        }
    }

    protected void buildHeapBackwards(Element<K, V>[] a) {
        heap = a;
        size = a.length;
        for (int i = (int) Math.floor(size/2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    protected void swap(int what, int with) {
        Comparable t = heap[what];
        heap[what] = heap[with];
        heap[with] = t;
    }

}
