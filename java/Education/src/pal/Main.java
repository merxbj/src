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

package pal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Jaroslav Merxbauer
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            sit();
        }
        catch (Exception ex) {
            System.out.print("-1");
            System.out.flush();
        }
    }

    public static void sit() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int N = Integer.parseInt(reader.readLine());//pocet budov
        int I = Integer.parseInt(reader.readLine());//pocet pripojeni
        List<Hrana> pole = new ArrayList<Hrana>(1000);
        int odkud =-1;
        int kam =-1;
        int cena =-1;

        while (odkud !=0 || kam!=0 || cena!=0) {
            StringTokenizer st = new StringTokenizer(reader.readLine());
            odkud = Integer.parseInt(st.nextToken());
            kam = Integer.parseInt(st.nextToken());
            cena = Integer.parseInt(st.nextToken());
            if (odkud!=0 || kam!=0 || cena!=0) {
                if (odkud > kam){
                    Hrana h = new Hrana(kam,odkud,cena);
                    pole.add(h);
                } else {
                    Hrana h = new Hrana(odkud,kam,cena);
                    pole.add(h);
                }
            }
        }

        Collections.sort(pole);
        for(Hrana h : pole) {
            System.out.println(h);
        }

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

}
