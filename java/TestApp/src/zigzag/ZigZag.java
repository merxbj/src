package zigzag;

public class ZigZag {
    public static void main(String[] args) {
        int [][] matrix1 = {
            {1,2,3,4,5},
            {1,2,3,4,5},
            {1,2,3,4,5},
            {1,2,3,4,5}};
        
        int [][] matrix2 = {
            {1,2,3,4,5,6},
            {1,2,3,4,5,6},
            {1,2,3,4,5,6},
            {1,2,3,4,5,6},
            {1,2,3,4,5,6}};

        int [] list1 = zigZag(matrix1);
        int [] list2 = zigZag(matrix2);
        for (int i : list1) {
            System.out.print(i+",");
        }
        System.out.println("");

        for (int i : list2) {
            System.out.print(i+",");
        }
        System.out.println("");
    }
    public static int[] zigZag(int[][] matrix) {
        int x = 0; // aktualni souradnice x
        int y = 0; // aktualni souradnice y
        int cols = matrix[0].length; // pocet sloupcu
        int rows = matrix.length; // pocet radku
        int px =  1; // aktualni posun po x
        int py = -1; // aktualni posun po y
        int idx = 0; // index zig-zag seznamu
        boolean diag = true; // jdeme po diagonale?
        int[] list = new int[cols * rows]; // zig-zag seznam

        while ((x < cols) && (y < rows)) {
            list[idx++] = matrix[y][x]; // vybereme si prvek z matice

            // zjistime, kam se posuneme
            if ((y == 0) && (x != cols - 1) && diag) {
                py =  0; px =  1; diag = false;
            }
            else if ((x == 0) && (y != rows - 1) &&  diag) {
                py =  1; px =  0; diag = false;
            }
            else if ((x == 0) && !diag) {
                py = -1; px =  1; diag =  true;
            }
            else if ((y == 0) && !diag) {
                py =  1; px = -1; diag =  true;
            }
            else if ((x == cols - 1) && !diag) {
                py = 1; px =  -1; diag =  true;
            }
            else if ((y == rows - 1) && !diag) {
                py =  -1; px = 1; diag =  true;
            }
            else if ((x == cols - 1) && diag) {
                py = 1; px =  0; diag =  false;
            }
            else if ((y == rows - 1) && diag) {
                py =  0; px = 1; diag =  false;
            }
            else {
                /*
                 * Predchozi IFy resi zmeny pohybu, ktere nastavaji pri dotyku
                 * s hranou, kdy dochazi k ortogonalnimu pohybu
                 * Jinak dochazi k diagonalnimu pohybu, ktery se nijak nelomi
                 */
            }

            // posuneme se
            x = x + px;
            y = y + py;
        }

        return list; // vysledny seznam
    }
}
