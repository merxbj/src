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

package pal0;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {

    public static void main(String[] args) {
        try {
            delej();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void delej() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int pocetKulu = Integer.parseInt(reader.readLine());
        if (pocetKulu <= 0) {
            System.out.println(String.format("Spatny, nebo zadny pocet kulu: %d", pocetKulu));
            return;
        }

        int nactenoCelkem = 0;
        String radek = reader.readLine();
        Souradnice[] souradnice = new Souradnice[pocetKulu];

        while ((radek != null) && nactenoCelkem < pocetKulu) {
            String[] temp = radek.split(" ");
            Souradnice s = new Souradnice();

            s.x = Integer.parseInt(temp[0]);
            s.y = Integer.parseInt(temp[1]);

            souradnice[nactenoCelkem++] = s;
            radek = reader.readLine();
        }

        double plot = 0.0;
        for (int i = 0; i < souradnice.length; i++) {
            Souradnice s1 = souradnice[i % nactenoCelkem];
            Souradnice s2 = souradnice[(i+1) % nactenoCelkem];

            plot = plot + Math.sqrt(Math.pow(s1.x - s2.x, 2) + Math.pow(s1.y - s2.y, 2));
        }

        System.out.println((int) Math.ceil(plot * 5));
    }

    static class Souradnice {
        int x;
        int y;
    }


}
