/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * https://cw.felk.cvut.cz/lib/exe/fetch.php/courses/a4m33pal/cviceni/chessassignment2.pdf
 */
package cz.merxbj.pal4;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Jirí Fric
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            chess();
        } catch (Exception ex) {
            System.out.print(" ERROR");
            System.out.flush();
        }
    }

    public static void chess() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String pocatecni_rozestaveni = reader.readLine();
        int pocet_pultahu = Integer.parseInt(reader.readLine());

        int sachovnice = vstupDoInt(pocatecni_rozestaveni);
        System.out.println("-----Pocatecni Sachovnice-----");
        System.out.println(sachovnice);
        System.out.println("-----Pozice figury K-----");

        System.out.println(ziskejPoziciZsachovnice(sachovnice, 'K'));

        playChess(sachovnice, pocet_pultahu);
    }

    public static int vstupDoInt(String vstup) {
        //System.out.println(vstup);
        String[] figury = vstup.split(" ");
        System.out.println(figury.length);
        int sachovnice = -1;
        for (int i = 0; i < figury.length; i++) {
            if (figury[i].charAt(0) == 'K') {
                if (i == 0) {//uloz bileho krale
                    sachovnice = ulozFiguru(sachovnice, vypoctiPozici(figury[i].charAt(1), Integer.valueOf(figury[i].charAt(2)) - 48), 'K');
                } else {//uloz cerneho krale
                    sachovnice = ulozFiguru(sachovnice, vypoctiPozici(figury[i].charAt(1), Integer.valueOf(figury[i].charAt(2)) - 48), 'A');//A...Nenapadlo me nic jineho
                }
            }

            if (figury[i].charAt(0) == 'V') {
                if (i < 2) {//uloz bilou vez
                    sachovnice = ulozFiguru(sachovnice, vypoctiPozici(figury[i].charAt(1), Integer.valueOf(figury[i].charAt(2)) - 48), 'V');
                } else { //uloz cernou vez
                    sachovnice = ulozFiguru(sachovnice, vypoctiPozici(figury[i].charAt(1), Integer.valueOf(figury[i].charAt(2)) - 48), 'T');//T .. Tower
                }
            }

            if ((figury[i].charAt(0) == 'a') || (figury[i].charAt(0) == 'b') || (figury[i].charAt(0) == 'c') || (figury[i].charAt(0) == 'd') || (figury[i].charAt(0) == 'e') || (figury[i].charAt(0) == 'f') || (figury[i].charAt(0) == 'g' || (figury[i].charAt(0) == 'h'))) {
                //uloz bileho pesce
                sachovnice = ulozFiguru(sachovnice, vypoctiPozici(figury[i].charAt(0), Integer.valueOf(figury[i].charAt(1)) - 48), 'P');
            }

        }


        return sachovnice;
    }

    public static int ulozFiguru(int sachovnice, int pozice, char typ) {
        //ulozim si do int figurku, VSTUP: pozice, kde se dana figura nachazi a typ figury
        //K - Bily Kral, A - Cerny Kral, P - Bily pesec, V - Bila vez, T - Cerna vez, R - Bily jezdec, D - Bila dama
        //KKKKKK VVVVVV AAAAAA TTTTTT PPPPPP R D ... 32
        //-4...111111 111111 111111 111111 111111 0 0
        //00 pinda, 01 jezdec, 10 dama
        System.out.println("----Uloz Figuru-----");
        //   System.out.println(sachovnice);
        System.out.println(pozice);
        System.out.println(typ);
        switch (typ) {
            case 'K': {
                sachovnice &= 67108863;
                sachovnice |= (pozice << 26);
            }
            break;
            case 'V': {
                sachovnice &= -66060289;
                sachovnice |= (pozice << 20);
            }
            break;
            case 'P': {
                sachovnice &= -253;
                sachovnice |= (pozice << 2);
                sachovnice &= -4;
            }
            break;
            case 'A': {
                sachovnice &= -1032193;
                sachovnice |= (pozice << 14);
            }
            break;
            case 'T': {
                sachovnice &= -16129;
                sachovnice |= (pozice << 8);
            }
            break;
            case 'R': {
                sachovnice &= -253;
                sachovnice |= (pozice << 2);
                sachovnice = sachovnice & -4;
                sachovnice = sachovnice | 1;
            }
            break;
            case 'D': {
                sachovnice &= -253;
                sachovnice |= (pozice << 2);
                sachovnice &= -4;
                sachovnice |= 2;
            }
            break;
        }
        System.out.println(sachovnice);
        return sachovnice;

    }

    public static int vypoctiPozici(char pismeno, int cislo) {
        //dostanu pismeno a cislo a vratim cislo na sachovnici 0..48
        return (pismeno - 97) * 7 + cislo - 1;
    }

    public static int vypoctiPozici2(int cislo1, int cislo2) {
        //dostanu pismeno a cislo a vratim cislo na sachovnici 0..48
        return cislo1 * 7 + cislo2;
    }

    public static int ziskejPoziciZsachovnice(int sachovnice, char typ) {
        /*Funkce pocita pozici figury z sachovnice
        VSTUP: Sachovnice
         *       Typ figury {K,A,V,T,P,D,R}
        VYSTUP: Pozice figury;
        KKKKKK VVVVVV AAAAAA TTTTTT PPPPPP R D ... 32
         */
        int pozice = -1;
        switch (typ) {
            case 'K': {
                pozice = sachovnice >>> 26;
            }
            break;
            case 'A': {
                sachovnice = sachovnice << 12;
                pozice = sachovnice >>> 26;
            }
            break;
            case 'P':
            case 'R':
            case 'D': {
                sachovnice = sachovnice << 24;
                pozice = sachovnice >>> 26;
            }
            break;
            case 'V': {
                sachovnice = sachovnice << 6;
                pozice = sachovnice >>> 26;
            }
            break;
            case 'T': {
                sachovnice = sachovnice << 18;
                pozice = sachovnice >>> 26;
            }
            break;
        }
        return pozice;
    }

    public static int getx(int pozice) {
        //Vrati pozici x 0...6
        return pozice / 7;
    }

    public static int gety(int pozice) {
        //Vrati pozici y 0...6
        return pozice % 7;
    }

    public static class figurka {

        int x;
        int y;
        int pozice;
        char typ;
        char typ2;

        public figurka(char typ, int pozice, int x, int y) {
            this.typ = typ;
            this.pozice = pozice;
            this.x = x;
            this.y = y;
        }
    }

    public static figurka urciPesce(int sachovnice, figurka P) {
        //00 pinda, 01 jezdec, 10 dama
        P.typ2 = 'P';
        sachovnice = sachovnice << 30;
        sachovnice = sachovnice >> 30;
        switch (sachovnice) {
            case 0:
                P.typ2 = 'P';
                break;
            case 1:
                P.typ2 = 'R';
                break;
            case 2:
                P.typ2 = 'D';
                break;
        }
        return P;
    }

    public static boolean bilyVsachu() {
        return true;
    }

    public static boolean cernyVmatu() {
        return true;
    }

    public static void playChess(int sachovnice, int pocetPultahuBileho) throws Exception {
        int hloubka = 1;
        int i;

        int novaSachovnice = 0;
        boolean hrajeBily = true;
        //spustGenerator(sachovnice, true, 0, pocetPultahuBileho);
        if (hloubka % 2 == 0) {
            hrajeBily = false;
        }
        int pozice = ziskejPoziciZsachovnice(sachovnice, 'K');
        figurka K = new figurka('K', pozice, getx(pozice), gety(pozice));
        pozice = ziskejPoziciZsachovnice(sachovnice, 'V');
        figurka V = new figurka('V', pozice, getx(pozice), gety(pozice));
        pozice = ziskejPoziciZsachovnice(sachovnice, 'A');
        figurka A = new figurka('A', pozice, getx(pozice), gety(pozice));
        pozice = ziskejPoziciZsachovnice(sachovnice, 'T');
        figurka T = new figurka('T', pozice, getx(pozice), gety(pozice));
        pozice = ziskejPoziciZsachovnice(sachovnice, 'P');
        figurka P = new figurka('P', pozice, getx(pozice), gety(pozice));
        P = urciPesce(sachovnice, P);

        if (hrajeBily) {
            //Bila vez
            if (V.x < 7 && V.x > -1 && V.y < 7 && V.y > -1) {//Figura tam je...
                novaSachovnice = sachovnice;
                boolean postup = true;
                //Tahy vodorovne
                for (i = (V.x - 1); i > -1; i--) {
                    pozice = vypoctiPozici2(i, V.y);
                    if (pozice == P.pozice || pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == T.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                    }
                }
                postup = true;
                for (i = (V.x + 1); i < 7; i++) {
                    pozice = vypoctiPozici2(i, V.y);
                    if (pozice == P.pozice || pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == T.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                    }

                }
                //Tahy Svisle
                postup = true;
                for (i = (V.y + 1); i < 7; i++) {
                    pozice = vypoctiPozici2(V.x, i);
                    if (pozice == P.pozice || pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == T.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                    }
                }
                postup = true;
                for (i = (V.y - 1); i > -1; i--) {
                    pozice = vypoctiPozici2(V.x, i);
                    if (pozice == P.pozice || pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == T.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, V.typ);
                    }

                }
            }
            //Bile Cosi
            if (P.x < 7 && P.x > -1 && P.y < 7 && P.y > 0) {//Figura tam je...
                switch (P.typ2) {
                    case 'P': {
                        novaSachovnice = sachovnice;
                        if (P.y + 1 == 6) {
                           novaSachovnice = ulozFiguru(novaSachovnice, pozice, 'R');
                           novaSachovnice = ulozFiguru(novaSachovnice, pozice, 'D');
                        } else {
                            pozice = vypoctiPozici2(P.x, P.y + 1);
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice || pozice != T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        if (P.x != 6) {
                            pozice = vypoctiPozici2(P.x + 1, P.y + 1);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                        }

                        if (P.x != 0) {
                            pozice = vypoctiPozici2(P.x - 1, P.y + 1);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                        }

                    }
                    break;
                    case 'R': {
                        //  <-|
                        //    |
                        //    x
                        if (P.x > 0 && P.y < 5) {
                            pozice = vypoctiPozici2(P.x - 1, P.y + 2);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        //    |->
                        //    |
                        //    x
                        if (P.x < 6 && P.y < 5) {
                            pozice = vypoctiPozici2(P.x + 1, P.y + 2);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        // <--|
                        //    x
                        if (P.x > 1 && P.y < 6) {
                            pozice = vypoctiPozici2(P.x - 2, P.y + 1);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        //    |-->
                        //    x
                        if (P.x < 5 && P.y < 6) {
                            pozice = vypoctiPozici2(P.x + 2, P.y + 1);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        //    x
                        //    |
                        //  <-|
                        if (P.x > 0 && P.y > 1) {
                            pozice = vypoctiPozici2(P.x - 1, P.y - 2);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        //    x
                        //    |
                        //    |->
                        if (P.x < 6 && P.y > 1) {
                            pozice = vypoctiPozici2(P.x + 1, P.y - 2);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        //    x
                        //    |-->
                        if (P.x < 5 && P.y > 0) {
                            pozice = vypoctiPozici2(P.x + 2, P.y - 1);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        //    x
                        // <--|
                        if (P.x > 1 && P.y > 0) {
                            pozice = vypoctiPozici2(P.x - 2, P.y - 1);
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                            }
                            if (pozice != V.pozice || pozice != K.pozice || pozice != A.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                    }
                    break;
                    case 'D': {
                        novaSachovnice = sachovnice;
                        boolean postup = true;
                        //Tahy vodorovne
                        for (i = (P.x - 1); i > -1; i--) {
                            pozice = vypoctiPozici2(i, P.y);
                            if (pozice == V.pozice || pozice == K.pozice || pozice == A.pozice) {
                                postup = false;
                            }
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                                postup = false;
                            }
                            if (postup) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        postup = true;
                        for (i = (P.x + 1); i < 7; i++) {
                            pozice = vypoctiPozici2(i, P.y);
                            if (pozice == V.pozice || pozice == K.pozice || pozice == A.pozice) {
                                postup = false;
                            }
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                                postup = false;
                            }
                            if (postup) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }

                        }
                        //Tahy Svisle
                        postup = true;
                        for (i = (P.y + 1); i < 7; i++) {
                            pozice = vypoctiPozici2(V.x, i);
                            if (pozice == V.pozice || pozice == K.pozice || pozice == A.pozice) {
                                postup = false;
                            }
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                                postup = false;
                            }
                            if (postup) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }
                        }
                        postup = true;
                        for (i = (P.y - 1); i > -1; i--) {
                            pozice = vypoctiPozici2(V.x, i);
                            if (pozice == V.pozice || pozice == K.pozice || pozice == A.pozice) {
                                postup = false;
                            }
                            if (pozice == T.pozice) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                                novaSachovnice = ulozFiguru(novaSachovnice, 63, T.typ);//Smaz Cernou Vez
                                postup = false;
                            }
                            if (postup) {
                                novaSachovnice = ulozFiguru(novaSachovnice, pozice, P.typ);
                            }

                        }
                        //Dama Tahy Uhlopricne
                    }
                }
            }
            //Bily Kral
            if (K.x < 7 && K.x > -1 && K.y < 7 && K.y > 0) {//Figura tam je...
            }
        } //Hraje Cerny
        else {
            //Cerny Kral
            if (A.x < 7 && A.x > -1 && A.y < 7 && A.y > 0) {//Figura tam je...
            }
            //Cerna Vez
            if (T.x < 7 && T.x > -1 && T.y < 7 && T.y > 0) {//Figura tam je...
                novaSachovnice = sachovnice;
                boolean postup = true;
                //Tahy vodorovne
                for (i = (T.x - 1); i > -1; i--) {
                    pozice = vypoctiPozici2(i, T.y);
                    if (pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == V.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, V.typ);//Smaz Bilou Vez
                        postup = false;
                    }
                    if (pozice == P.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, P.typ);//Smaz Bile cosi
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                    }
                }
                postup = true;
                for (i = (T.x + 1); i < 7; i++) {
                    pozice = vypoctiPozici2(i, T.y);
                    if (pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == V.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, V.typ);//Smaz Bilou Vez
                        postup = false;
                    }
                    if (pozice == P.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, P.typ);//Smaz Bile cosi
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                    }

                }
                //Tahy Svisle
                postup = true;
                for (i = (T.y + 1); i < 7; i++) {
                    pozice = vypoctiPozici2(i, T.y);
                    if (pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == V.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, V.typ);//Smaz Bilou Vez
                        postup = false;
                    }
                    if (pozice == P.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, P.typ);//Smaz Bile cosi
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                    }
                }
                postup = true;
                for (i = (T.y - 1); i > -1; i--) {
                    pozice = vypoctiPozici2(i, T.y);
                    if (pozice == K.pozice || pozice == A.pozice) {
                        postup = false;
                    }
                    if (pozice == V.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, V.typ);//Smaz Bilou Vez
                        postup = false;
                    }
                    if (pozice == P.pozice) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                        novaSachovnice = ulozFiguru(novaSachovnice, 63, P.typ);//Smaz Bile cosi
                        postup = false;
                    }
                    if (postup) {
                        novaSachovnice = ulozFiguru(novaSachovnice, pozice, T.typ);
                    }

                }
            }
        }

    }

    static int spustGenerator(int sachovnice, boolean hrajeBily, int hloubka, int pocetPultahuBileho) {


        if (hrajeBily) {
            if (hloubka <= pocetPultahuBileho) {
            }
        }
        return 1;
    }
}
/*
Map<Integer, Double> mape = new HashMap<Integer, Double>();
double evaluatePosition(int sachovnice) {

// calculates the actual position on the board
//   positive number means that white is favored (1.0 means that white is one pawn ahead)
//   negative number means that black is favored (-1.0 means that black is one pawn ahead)
//  returns some kind of magic number to indicate staleMate or checkMate

}

void getMoves(int sachovnice, int level) {

if ((level == MAX_LEVEL) || (sachovnice is checkMate) || (sachovnice is staleMate)) {
double val = evaluatePosition(sachovnice);
positionToValueMap.put(sachovnice, val);
return;
}

for each valid move {
getMoves(sachovnice, level + 1);
}
}
 * *
 */
