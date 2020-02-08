package matrixsolver;

public class Matrix {

    public Matrix(int x, int y) {
        this.x = x;
        this.y = y;

        SimpleMatrix = new double[y][x];
    }

    public void FillMatrix(int[][] Matrix) throws IllegalArgumentException {
        try {
            for (int r = 0; r < y; r++) {
                for (int c = 0; c < x; c++) {
                    SimpleMatrix[r][c] = Matrix[r][c];
                }
            }
        } catch (Exception ex){
            throw new IllegalArgumentException("Ilegal Matrix!");
        }
    }

    public void SolveMatrix() {
        RemoveZeroRows();
        int r = 0;
        int c = 0;
        while ((r < y) && (c < x)) {
            double a = SimpleMatrix[r][c];
            if (a == 0) {
                int nonZeroRow = FindNonZeroElementInColumn(c, r);
                if (nonZeroRow == -1) {
                    c++;
                    continue;
                } else {
                    SwapRows(r, nonZeroRow);
                }
            }
            ZeroOutColumnUnderRow(c, r);
            RemoveZeroRows();
            r++;
            c++;
        }
    }

    private int FindNonZeroElementInColumn(int column, int underRow) {
        int nonZeroRow = -1;
        for (int r = underRow; r < y; r++) {
            if (SimpleMatrix[r][column] != 0) {
                nonZeroRow = r;
                break;
            }
        }
        return nonZeroRow;
    }

    private void SwapRows(int row1, int row2) {
        for (int c = 0; c < y; c++) {
            double temp = SimpleMatrix[row1][c];
            SimpleMatrix[row1][c] = SimpleMatrix[row2][c];
            SimpleMatrix[row2][c] = temp;
        }
    }

    private void ZeroOutColumnUnderRow(int column, int underRow) {
        double a = SimpleMatrix[underRow][column];
        for (int r = underRow + 1; r < y; r++) {
            double koef = (-SimpleMatrix[r][column])/a;
            JoinRows(underRow, r, koef);
        }
    }

    private void JoinRows(int mainRow, int row, double koef) {
        for (int c = 0; c < x; c++) {
            double delta = SimpleMatrix[mainRow][c] * koef;
            SimpleMatrix[row][c] += delta;
        }
    }

    private void RemoveZeroRows() {
        boolean nonZero = false;
        boolean found = false;
        do {
            found = false;
            for (int r = 0; r < y; r++) {
                for (int c = 0; c < x; c++) {
                    if (SimpleMatrix[r][c] != 0) {
                        nonZero = true;
                        break;
                    }
                }
                if (!nonZero) {
                    RemoveRow(r);
                    found = true;
                }
                nonZero = false;
            }
        } while (found);
    }

    private void RemoveRow(int row) {
        for (int r = row + 1; r < y; r++) {
            for (int c = 0; c < x; c++) {
                SimpleMatrix[r-1][c] = SimpleMatrix[r][c];
            }
        }
        y--;
    }

    public void Print() {
        for (int r = 0; r < y; r++) {
            System.out.print("(");
            for (int c = 0; c < x; c++) {
                System.out.print(String.format("%5.1f",SimpleMatrix[r][c]));
            }
            System.out.println(")");
        }
    }

    private double[][] SimpleMatrix;

    private int x;
    private int y;

}
