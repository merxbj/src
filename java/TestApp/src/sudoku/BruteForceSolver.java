package sudoku;

public class BruteForceSolver implements SudokuSolver {
    
    public int[][] solve(int[][] rawMatrix) {
        Element[][] matrix = parseRawMatrix(rawMatrix);
        int sizeX = matrix[0].length;
        int sizeY = matrix.length;
        int x = 0;
        int y = 0;
        boolean rollbacking = false;
        while ((x < sizeX) && (y < sizeY)) {
            if (matrix[y][x].skip) {
                if (!rollbacking) { if (++x == sizeX) { x = 0; ++y; }}
                else { if (--x == -1) { x = sizeX - 1; --y; }}
            } else {
                if (matrix[y][x].value == 9) {
                    // we have exceeded all possible tries on this element
                        matrix[y][x].value = 0;
                        if (--x == -1) { x = sizeX - 1; --y; }
                        continue;
                } else {
                    matrix[y][x].value++;
                    rollbacking = false;
                }
                if (validateElement(matrix[y][x], matrix)) {
                    if (++x == sizeX) { x = 0; ++y; }
                } else {
                    if (matrix[y][x].value == 9) {
                        // we have exceeded all possible tries on this element
                        matrix[y][x].value = 0;
                        if (--x == -1) { x = sizeX - 1; --y; }
                        rollbacking = true;
                    }
                }
            }
        }

        return toRawMatrix(matrix);
    }

    private Element[][] parseRawMatrix(int[][] rawMatrix) {
        int sizeX = rawMatrix[0].length;
        int sizeY = rawMatrix.length;
        Element[][] elements = new Element[sizeY][sizeX];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeX; j++) {
                int value = rawMatrix[i][j];
                elements[i][j] = new Element(value, value != 0, j, i);
            }
        }
        return elements;
    }

    private int[][] toRawMatrix(Element[][] matrix) {
        int sizeX = matrix[0].length;
        int sizeY = matrix.length;
        int[][] rawMatrix = new int[sizeY][sizeX];
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                rawMatrix[y][x] = matrix[y][x].value;
            }
        }
        return rawMatrix;
    }

    private boolean validateElement(Element e, Element[][] m) {
        int sizeX = m[0].length;
        int sizeY = m.length;
        
        // verify row
        for (int x = 0; x < sizeX; x++) {
            if ((x != e.x) && (m[e.y][x].value == e.value)) {
                return false;
            }
        }

        // verify column
        for (int y = 0; y < sizeY; y++) {
            if ((y != e.y) && (m[y][e.x].value == e.value)) {
                return false;
            }
        }

        // verify box
        for (int y = (e.y / 3) * 3; y < ((e.y / 3) * 3) + 3; y++) {
            for (int x = (e.x / 3) * 3; x < ((e.x / 3) * 3) + 3; x++) {
                if ((y != e.y) && (x != e.x) && m[y][x].value == e.value) {
                    return false;
                }
            }
        }

        // we reached this point therefore we are valid
        return true;
    }

    private class Element {

        public int value;
        public boolean skip;
        public int x;
        public int y;

        public Element(int value, boolean skip, int x, int y) {
            this.value = value;
            this.skip = skip;
            this.x = x;
            this.y = y;
        }
    }
}
