/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.merxbj.sudoku;

import junit.framework.Assert;
import java.util.List;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jmerxbauer
 */
public class SudokuTest {

    public SudokuTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void main() {

        int [][] hardestMatrix = new int [][] {
            {8,0,0,0,0,0,0,0,0},
            {0,0,3,6,0,0,0,0,0},
            {0,7,0,0,9,0,2,0,0},
            {0,5,0,0,0,7,0,0,0},
            {0,0,0,0,4,5,7,0,0},
            {0,0,0,1,0,0,0,3,0},
            {0,0,1,0,0,0,0,6,8},
            {0,0,8,5,0,0,0,1,0},
            {0,9,0,0,0,0,4,0,0}
        };

        int [][] hardestMatrixSolution = new int [][] {
            {8,1,2,7,5,3,6,4,9},
            {9,4,3,6,8,2,1,7,5},
            {6,7,5,4,9,1,2,8,3},
            {1,5,4,2,3,7,8,9,6},
            {3,6,9,8,4,5,7,2,1},
            {2,8,7,1,6,9,5,3,4},
            {5,2,1,9,7,4,3,6,8},
            {4,3,8,5,2,6,9,1,7},
            {7,9,6,3,1,8,4,5,2}
        };

        int [][] result;

        SudokuSolver s = new BruteForceSolver();
        result = s.solve(hardestMatrix);

        for (int row = 0; row < 9; row++) {
            assertArrayEquals(hardestMatrixSolution[row], result[row]);
        }

        printMatrix(result);
    }

    public static void printMatrix(int[][] matrix) {
        for (int [] rows : matrix) {
            for (int i : rows) {
                System.out.print(i + " ");
            }
            System.out.println("");
        }
    }

    public void matrixStorage() {

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

        int [][] insaneMatrix = new int [][] {
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,8,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0}
        };
    }

}