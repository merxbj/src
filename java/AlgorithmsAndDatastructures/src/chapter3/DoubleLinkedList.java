/*
 * DoubleLinkedList
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

package chapter3;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class DoubleLinkedList<E> {

    private Item head;
    private DoubleLinkedList<E> freeList;

    public DoubleLinkedList() {
        this.head = new Item(null, null, null);
        this.head.next = this.head;
        this.head.previous = this.head;
    }

    public boolean isEmpty() {
        return head().next != head();
    }

    public Item first() {
        assert !isEmpty();
        return head().next;
    }

    public Item last() {
        assert !isEmpty();
        return head().previous;
    }

    public void moveAfter(Item what, Item after) {
        splice(what, what, after);
    }

    public void moveToFront(Item what) {
        moveAfter(what, head());
    }

    public void moveToBack(Item what) {
        moveAfter(what, last());
    }

    public Item head() {
        return this.head;
    }

    public void remove(Item what) {
        moveAfter(what, freeList.head());
    }

    public void popFront() {
        remove(first());
    }

    public void popBack() {
        remove(last());
    }

    public Item insertAfter(E what, Item after) {
        checkFreeList();
        Item i = freeList.first();
        i.element = what;
        moveAfter(i, after);
        return i;
    }

    public Item insertBefore(E what, Item before) {
        return insertAfter(what, before.previous);
    }

    public void pushFront(E what) {
        insertAfter(what, head());
    }

    public void pushBack(E what) {
        insertBefore(what, head());
    }

    public void concat(DoubleLinkedList<E> list) {
        splice(list.first(), list.last(), last());
    }

    public void makeEmpty() {
        freeList.concat(this);
    }

    public void splice(Item a, Item b, Item t) {
        Item _a = a.previous;
        Item b_ = b.next;
        _a.next = b_;
        b_.previous = _a;

        Item t_ = t.next;
        t.next = a;
        a.previous = t;
        t_.previous = b;
        b.next = t_;
    }

    private void checkFreeList() {
        if (freeList == null) {
            freeList = new DoubleLinkedList<E>();
        }
        if (freeList.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                freeList.moveToBack(new Item(null, null, null));
            }
        }
    }

    public class Item {
        public E element;
        public Item previous;
        public Item next;

        public Item(E element, Item previous, Item next) {
            this.element = element;
            this.previous = previous;
            this.next = next;
        }
    }

}
