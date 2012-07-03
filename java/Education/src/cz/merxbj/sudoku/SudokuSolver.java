package cz.merxbj.sudoku;

public abstract class SudokuSolver {

    protected Element[][] parseRawMatrix(int[][] rawMatrix) {
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

    protected int[][] toRawMatrix(Element[][] matrix) {
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

    protected class Element {

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
    
    public abstract int[][] solve(int[][] matrix);

}
