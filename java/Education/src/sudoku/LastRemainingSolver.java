package sudoku;

import java.util.ArrayList;
import java.util.Hashtable;

public class LastRemainingSolver extends SudokuSolver {

    private Element[][] elements;

    public int[][] solve(int[][] matrix) {

        elements = new Element[matrix.length][matrix[0].length];
        for (int y = 0; y < matrix.length; y++) {
             for (int x = 0; x < matrix[0].length; x++) {
                elements[y][x] = new Element(x, y, matrix[y][x]);
            }
        }

        for (int y = 0; y < matrix.length; y++) {
             for (int x = 0; x < matrix[0].length; x++) {
                registerRowNeighbors(elements[y][x], y);
                registerColumnNeighbors(elements[y][x], x);
                registerBoxNeighbors(elements[y][x], x, y);
            }
        }

        int needsToSolve = 0;
        int lastNeedsToSolve = 0;
        do {
            needsToSolve = 0;
            for (int y = 0; y < matrix.length; y++) {
                for (int x = 0; x < matrix[0].length; x++) {
                    Element e = elements[y][x];
                    if ((e.value == 0) && !e.trySolve()) {
                        needsToSolve++;
                    }
                }
            }
            System.out.println(String.format("Remaining %d to solve", needsToSolve));
            if (lastNeedsToSolve == needsToSolve) {
                // next iteration changes nothing! give up!
                break;
            } else {
                lastNeedsToSolve = needsToSolve;
            }
        } while (needsToSolve > 0);

        int[][] output = new int[matrix.length][matrix[0].length];
        for (int y = 0; y < matrix.length; y++) {
             for (int x = 0; x < matrix[0].length; x++) {
                output[y][x] = elements[y][x].getValue();
            }
        }

        return output;
    }

    private void registerRowNeighbors(Element element, int y) {
        for (int i = 0; i < elements[y].length; i++) {
            if (!element.equals(elements[y][i])) {
                elements[y][i].registerNeighbor(element);
            }
        }
    }

    private void registerColumnNeighbors(Element element, int x) {
        for (int j = 0; j < elements.length; j++) {
            if (!element.equals(elements[j][x])) {
                elements[j][x].registerNeighbor(element);
            }
        }
    }

    private void registerBoxNeighbors(Element element, int x, int y) {
        for (int j = (y / 3) * 3; j < ((y / 3) * 3) + 3; j++) {
            for (int i = (x / 3) * 3; i < ((x / 3) * 3) + 3; i++) {
                if (!elements[j][i].equals(element)) {
                    elements[j][i].registerNeighbor(element);
                }
            }
        }
    }

    private class Element {
        private Coordinate cor;
        private int value;
        private Hashtable<Integer, Boolean> possibles;
        private ArrayList<Element> neighbors;

        public Element(int x, int y, int value) {
            this.cor = new Coordinate(x,y);
            this.value = value;
            this.possibles = new Hashtable<Integer, Boolean>();
            this.neighbors = new ArrayList<Element>();
            
            // this should be more generic
            for (int i = 1; i < 10; i++) {
                possibles.put(i, Boolean.TRUE);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Element)) { return false; }
            Element e2 = (Element) obj;
            return (this.cor.equals(e2.cor));
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 43 * hash + (this.cor != null ? this.cor.hashCode() : 0);
            return hash;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
            for (Element e : neighbors ) {
                e.neighborValueChanged(value);
            }
        }

        public void registerNeighbor(Element neighbor) {
            neighbors.add(neighbor);
            if ((neighbor.getValue() > 0) && (this.value == 0)) {
                possibles.put(neighbor.getValue(), Boolean.FALSE);
            }
        }

        public void neighborValueChanged(int newValue) {
            this.possibles.put(newValue, Boolean.FALSE);
        }

        public boolean trySolve() {
            int lastPossible = 0;
            for (int i = 1; i <= possibles.size(); i++) {
                if (possibles.get(i) && lastPossible > 0) {
                    return false;
                } else if (possibles.get(i)) {
                    lastPossible = i;
                }
            }
            
            if (lastPossible > 0) {
                this.setValue(lastPossible);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d : (", value));
            for (int i = 1; i < possibles.size(); i++) {
                if (possibles.get(i)) {
                    sb.append(i);
                    sb.append(",");
                }
            }
            return sb.toString();
        }
    }

    private class Coordinate {
        public int x = 0;
        public int y = 0;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coordinate)) { return false; }
            Coordinate cor2 = (Coordinate) obj;
            return ((cor2.x == this.x) && (cor2.y == this.y));
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + this.x;
            hash = 89 * hash + this.y;
            return hash;
        }
    }
}
