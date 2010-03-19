package sudoku;

import java.util.ArrayList;

public class SolverMain {

    public static ArrayList<SudokuSolver> solvers;

    public static void main(String[] args) {
        
        solvers = new ArrayList<SudokuSolver>();
        solvers.add(new BruteForceSolver());
        solvers.add(new FindRemainingSolver());

        int [][] matrix = new int [][] {
            {1,0,6,0,0,0,7,0,0},
            {0,0,0,0,0,4,0,0,1},
            {0,9,4,0,0,0,3,0,8},
            {8,2,0,0,1,0,0,0,0},
            {0,3,9,2,0,8,4,1,0},
            {0,0,0,0,5,0,0,2,9},
            {9,0,7,0,0,0,1,3,0},
            {5,0,0,9,0,0,0,0,0},
            {0,0,8,0,0,0,9,0,4}
        };

        int [][] hardMatrix = new int [][] {
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,3,0,8,5},
            {0,0,1,0,2,0,0,0,0},
            {0,0,0,5,0,7,0,0,0},
            {0,0,4,0,0,0,1,0,0},
            {0,9,0,0,0,0,0,0,0},
            {5,0,0,0,0,0,0,7,3},
            {0,0,2,0,1,0,0,0,0},
            {0,0,0,0,4,0,0,0,9}
        };

        int [][] result;

        for (SudokuSolver s : solvers) {
            result = s.solve(matrix);
            printMatrix(result);
            /*result = s.solve(hardMatrix);
            printMatrix(result);*/
        }
    }

    public static void printMatrix(int[][] matrix) {
        for (int [] rows : matrix) {
            for (int i : rows) {
                System.out.print(i + " ");
            }
            System.out.println("");
        }
    }

}
