package matrixsolver;

public class Main {

    public static void main(String[] args) {

        try {
            //Matrix m = new Matrix(3,2);
            //int [][] matrix = {{2,-5,16},{-1,2,-7}};
            Matrix m = new Matrix(4, 4);
            int [][] matrix = {{  3,  1,  1,  2},
                               {  4,  2,  1,- 2},
                               {  5,  3,- 2,  0},
                               {  4,  3,- 1, -6}};
            m.FillMatrix(matrix);
            m.SolveMatrix();
            m.Print();
        } catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
}
