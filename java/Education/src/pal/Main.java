package pal;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 *
 * @author Jiri Fric
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            String out = sit();
            System.out.print(out);
            System.out.flush();
        } catch (Exception ex) {
            System.out.print("-1");
            System.out.flush();
        }
    }

    public static String sit() throws Exception {
        StringBuilder builder = new StringBuilder(4000000);
        BufferedInputStream stream = new BufferedInputStream(System.in, 50 * 1024 * 1024); // 50 MB of file size?
        BufferedReader reader = new BufferedReader(stream);

        int N = reader.nextInt(); //pocet budov
        int I = reader.nextInt(); //pocet pripojeni
        Hrana[] poleHran = new Hrana[4000000];
        int poleHranNacteno = 0;
        int odkud = -1;
        int kam = -1;
        int cena = -1;
        while (odkud != 0 || kam != 0 || cena != 0) {
            odkud = reader.nextInt();
            kam = reader.nextInt();
            cena = reader.nextInt();
            if (odkud != 0 || kam != 0 || cena != 0) {
                if (odkud > kam) {
                    poleHran[poleHranNacteno++] = new Hrana(kam, odkud, cena);
                } else {
                    poleHran[poleHranNacteno++] = new Hrana(odkud, kam, cena);
                }
            }
        }

        Arrays.sort(poleHran, 0, poleHranNacteno);
        //quicksort(poleHran, 0, poleHranNacteno - 1);

        //kruskal
        //Nastavim do poleVrcholu vsem vrcholum koren na -1, vytvorim tedy N koster, ty pak budu spojovat
        Vrchol[] poleVrcholu = new Vrchol[N];
        int poleVrcholuNacteno = 0;
        for (int j = 1; j <= N; j++) { // Vrcholy se cisluji od 1ky
            poleVrcholu[poleVrcholuNacteno++] = new Vrchol(j, null);
        }

        int pocetKoster = N;
        int i = 0;

        int cenaCelkem = 0;
        while (pocetKoster > I) {
            Hrana hrana = poleHran[i];
            Vrchol koren1 = najdiKoren(poleVrcholu[hrana.odkud - 1]);
            Vrchol koren2 = najdiKoren(poleVrcholu[hrana.kam - 1]);
            if (koren1.cislo != koren2.cislo) {
                if (koren1.cislo < koren2.cislo) {
                    koren2.rodic = koren1;
                } else {
                    koren1.rodic = koren2;
                }
                cenaCelkem += hrana.cena;
                builder.append(hrana);
                pocetKoster--;
            }
            i++;
        }

        StringBuilder builder2 = new StringBuilder().append(cenaCelkem).append("\n");
        builder2.append(formatujKoreny(poleVrcholu, I));

        builder = builder2.append("\n").append(builder).append("0 0 0");
        return builder.toString();
    }

    public static Vrchol najdiKoren(Vrchol bod) {
        while (bod.rodic != null) {
            bod = bod.rodic;
        }
        return bod;
    }

    public static String formatujKoreny(Vrchol[] vrcholy, int maxKorenu) {
        StringBuilder builder = new StringBuilder(vrcholy.length * 10);
        int korenuCelkem = 0;

        for (Vrchol k : vrcholy) {
            if (k.rodic == null) {
                if (korenuCelkem > 0) {
                    builder.append(" ");
                }
                builder.append(k.cislo);
                korenuCelkem++;

                if (korenuCelkem == maxKorenu) {
                    break;
                }
            }
        }
        return builder.toString();
    }

    public static class Hrana implements Comparable<Hrana> {

        Integer odkud;
        Integer kam;
        Integer cena;

        @Override
        public int compareTo(Hrana o) {
            int compare = cena.compareTo(o.cena);
            if (compare == 0) {
                compare = odkud.compareTo(o.odkud);
            }

            if (compare == 0) {
                compare = kam.compareTo(o.kam);
            }

            return compare;
        }

        public Hrana(int odkud, int kam, int cena) {
            this.odkud = odkud;
            this.kam = kam;
            this.cena = cena;
        }

        @Override
        public String toString() {
            return String.format("%d %d %d\n", odkud, kam, cena);
        }
    }

    public static class Vrchol {
        Integer cislo;
        Vrchol rodic;

        public Vrchol(int cislo, Vrchol rodic) {
            this.cislo = cislo;
            this.rodic = rodic;
        }
    }

    private static void quicksort(Hrana[] hrany, int low, int high) {
        int i = low, j = high;
        Hrana pivot = hrany[low + (high - low) / 2];

        while (i <= j) {
            while (hrany[i].compareTo(pivot) < 0) {
                i++;
            }
            while (hrany[j].compareTo(pivot) > 0) {
                j--;
            }
            if (i <= j) {
                prohod(hrany, i, j);
                i++;
                j--;
            }
        }
        if (low < j) {
            quicksort(hrany, low, j);
        }
        if (i < high) {
            quicksort(hrany, i, high);
        }
    }

    private static void prohod(Hrana[] hrany, int i, int j) {
        Hrana temp = hrany[i];
        hrany[i] = hrany[j];
        hrany[j] = temp;
    }

    private static class BufferedReader {
        private byte [] buffer = null;
        private int bufferPos = 0;
        private int bufferSize = 0;
        private InputStream stream;

        public BufferedReader(InputStream stream) {
            this.stream = stream;
            this.buffer = new byte[50 * 1024 * 1024];
        }

        public int nextInt() throws Exception {
            byte ascii = read();
            int cislo = 0;
            while ((ascii != '\n') && (ascii != '\r') && (ascii != ' ')) {
                cislo *= 10;
                cislo += asciiToInt(ascii);
                ascii = read();
            }
            return cislo;
        }

        public byte read() throws Exception{
            if (bufferSize <= bufferPos) {
                int available = stream.available();
                bufferSize = stream.read(buffer, 0, available);
                bufferPos = 0;
            }
            return buffer[bufferPos++];
        }

        private int asciiToInt(byte ascii) throws Exception {
            int cislice = ascii - 48;
            if (cislice < 0) {
                throw new Exception("Invalid input");
            }
            return cislice;
        }


    }
}
