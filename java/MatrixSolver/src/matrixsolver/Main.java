package matrixsolver;

public class Main {

    public static void main(String[] args) {

        try {
            //Matrix m = new Matrix(3,2);
            //int [][] matrix = {{2,-5,16},{-1,2,-7}};
            Matrix m = new Matrix(6,4);
            int [][] matrix = {{- 4,  4,- 1,  1,- 7,-11},
                               {  2,- 2,  1,  0,  3,  4},
                               {  4,- 4,  5,  1,  7,- 3},
                               {- 6,  6,- 4,  1,-12,- 7}};
            m.FillMatrix(matrix);
            m.SolveMatrix();
            m.Print();
        } catch (Exception ex){
            System.out.println(ex.toString());
        }
    }
}
