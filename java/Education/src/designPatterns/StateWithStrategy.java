/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package designPatterns;

import java.util.Dictionary;
import java.util.LinkedList;

/**
 *
 * @author jmerxbauer
 */
public class StateWithStrategy {

    interface ArrayStrategy {
        void set(int index, int value);
        int get(int index);
        boolean passRequirements();
    }

    class ArrayImplementationStrategy implements ArrayStrategy {
        private int[] array;

        public int get(int index) {
            // do stuff
            return 0;
        }

        public void set(int index, int value) {
            // do stuff
        }

        public void initArray(LinkedList<Dictionary<Integer, Integer>> list) {
            // do stuff
        }

        public boolean passRequirements() {
            // validation stuff
            return true;
        }
    }

    class LinkedListImplementationStrategy implements ArrayStrategy {
        private LinkedList<Dictionary<Integer, Integer>> list;

        public int get(int index) {
            // do stuff
            return 0;
        }

        public void set(int index, int value) {
            // do stuff
        }

        public void initList(int[] array) {
            // do stuff
        }

        public boolean passRequirements() {
            // validation stuff
            return true;
        }
    }

    interface ArrayState {
        ArrayStrategy getArrayStrategy(ArrayManager array);
    }

    class SuitableForArrayState implements ArrayState {
        public ArrayStrategy getArrayStrategy(ArrayManager manager) {
            if (!manager.currentStrategy.passRequirements()) {
                LinkedListImplementationStrategy linked = new LinkedListImplementationStrategy();
                linked.initList(((ArrayImplementationStrategy) manager.currentStrategy).array);
                manager.currentState = new SuitableForLinkedListState();
                return linked;
            }
            return manager.currentStrategy;
        }
    }

    class SuitableForLinkedListState implements ArrayState {
        public ArrayStrategy getArrayStrategy(ArrayManager manager) {
            if (!manager.currentStrategy.passRequirements()) {
                ArrayImplementationStrategy array = new ArrayImplementationStrategy();
                array.initArray(((LinkedListImplementationStrategy) manager.currentStrategy).list);
                manager.currentState = new SuitableForArrayState();
                return array;
            }
            return manager.currentStrategy;
        }
    }

    class ArrayManager {
        ArrayState currentState = new SuitableForLinkedListState();
        ArrayStrategy currentStrategy;

        public void set(int index, int value) {
            currentStrategy = currentState.getArrayStrategy(this);
            currentStrategy.set(index, value);
        }

        public int get(int index) {
            currentStrategy = currentState.getArrayStrategy(this);
            return currentStrategy.get(index);
        }
    }

    class InfiniteArray {

        private ArrayManager arrayManager = new ArrayManager();

        void set(int index, int value)
        {
            arrayManager.set(index, value);
        }

        int get(int index)
        {
            return arrayManager.get(index);
        }


    }
}
