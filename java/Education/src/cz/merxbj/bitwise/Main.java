/*
 * Main
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package cz.merxbj.bitwise;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {
    public static void main(String[] args) {

        int bilyKral = 45;
        int cernyKral = 26;
        int bilyPesec = 15;
        int bilaVez = 11;
        int cernaVez = 48;
        int bilyJezdec = 1;
        int bilaDama = 0;

        int sachovnice = 0;

        sachovnice |= (bilyKral << 26);
        ukazBity(sachovnice);
        
        sachovnice |= (cernyKral << 20);
        ukazBity(sachovnice);
        
        sachovnice |= (bilyPesec << 14);
        ukazBity(sachovnice);
        
        sachovnice |= (bilaVez << 8);
        ukazBity(sachovnice);
        
        sachovnice |= (bilyJezdec << 1);
        ukazBity(sachovnice);

        sachovnice |= bilaDama;
        ukazBity(sachovnice);

    }

    private static void ukazBity(int cislo) {
        int [] bity = new int[32];
        for (int i = 0; i < 32; i++) {
            int bit = cislo & 1; // jen posledni bit
            bity[i] = bit; // ulozime si posledni bit (1 / 0)
            cislo = cislo >> 1; // posledni bit "vysuneme"
        }

        for (int i = 31; i >= 0; i--) {
            if ( i != 31 && (31 - i) % 6 == 0) {
                System.out.print(" ");
            }

            System.out.print(bity[i]);
        }

        System.out.println("");
    }
}
