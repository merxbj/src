package cz.merxbj.sudoku;

public class BruteForceSolver extends SudokuSolver {
    
    @Override
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

    protected boolean validateElement(Element e, Element[][] m) {
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
}
