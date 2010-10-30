package pal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

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
            sit();
        } catch (Exception ex) {
            //System.out.print(ex);
            logException(ex);
            System.out.flush();
        }
    }

    public static void sit() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(reader.readLine());//pocet budov
        int I = Integer.parseInt(reader.readLine());//pocet pripojeni
        List<Hrana> pole = new ArrayList<Hrana>(4000000);
        int odkud = -1;
        int kam = -1;
        int cena = -1;

        while (odkud != 0 || kam != 0 || cena != 0) {
            StringTokenizer st = new StringTokenizer(reader.readLine());
            odkud = Integer.parseInt(st.nextToken());
            kam = Integer.parseInt(st.nextToken());
            cena = Integer.parseInt(st.nextToken());
            if (odkud != 0 || kam != 0 || cena != 0) {
                if (odkud > kam) {
                    Hrana h = new Hrana(kam, odkud, cena);
                    pole.add(h);
                } else {
                    Hrana h = new Hrana(odkud, kam, cena);
                    pole.add(h);
                }
            }
        }

        Collections.sort(pole);
        for (Hrana h : pole) {
            //System.out.println(h);
        }
        //kruskal
        //Nastavim do poleVrcholu vsem vrcholum koren na -1, vytvorim tedy N koster, ty pak budu spojovat
        List<Vrchol> poleVrcholu = new ArrayList<Vrchol>(N);
        for (int j = 1; j <= N; j++) { // Vrcholy se cisluji od 1ky
            poleVrcholu.add(new Vrchol(j, null));
        }

        int pocStruktur = N;
        int i = 0;

        int cenaCelkem = 0;
        while (pocStruktur > I) {
            Vrchol koren1 = najdiKoren(poleVrcholu.get(pole.get(i).odkud - 1));
            Vrchol koren2 = najdiKoren(poleVrcholu.get(pole.get(i).kam - 1));
            if (koren1.cislo != koren2.cislo) {
                if (koren1.cislo < koren2.cislo) {
                    koren2.rodic = koren1;
                } else {
                    koren1.rodic = koren2;
                }
                cenaCelkem = cenaCelkem + pole.get(i).cena;
                System.out.println(pole.get(i));
                pocStruktur--;
            }
            i++;
        }
        System.out.println(cenaCelkem);

    }

    public static Vrchol najdiKoren(Vrchol bod) {
        while (bod.rodic != null) {
            bod = bod.rodic;
        }
        return bod;
    }

    public static class Hrana implements Comparable<Hrana> {

        Integer odkud;
        Integer kam;
        Integer cena;

        @Override
        public int compareTo(Hrana o) {
            int compare = cena.compareTo(o.cena);
            if (compare != 0) {
                return compare;
            }

            compare = odkud.compareTo(o.odkud);
            if (compare != 0) {
                return compare;
            }

            return kam.compareTo(o.kam);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Hrana other = (Hrana) obj;
            if (this.odkud != other.odkud) {
                return false;
            }
            if (this.kam != other.kam) {
                return false;
            }
            if (this.cena != other.cena) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.odkud;
            hash = 71 * hash + this.kam;
            hash = 71 * hash + this.cena;
            return hash;
        }

        public Hrana(int odkud, int kam, int cena) {
            this.odkud = odkud;
            this.kam = kam;
            this.cena = cena;
        }

        @Override
        public String toString() {
            return String.format("%4d %4d %4d", odkud, kam, cena);
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

    static private String formatException(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.toString());
        sb.append("\n");
        for (StackTraceElement ste : ex.getStackTrace()) {
            sb.append("\t at ");
            sb.append(ste.toString());
            sb.append("\n");
        }

        Throwable innerException = ex.getCause();
        while (innerException != null) {
            sb.append("\t caused by ");
            sb.append(innerException.toString());
            sb.append("\n");
            innerException = innerException.getCause();
        }

        return sb.toString();
    }

    public static void logException(Throwable exception) {
        log(formatException(exception));
    }

    private static void log(String message) {
        Date date = Calendar.getInstance().getTime();
        System.out.println(String.format("%25s | [%s] | %s", date, "PALY", message));
    }
}
