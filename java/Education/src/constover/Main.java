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

package constover;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class Main {
    public static void main(String[] args) {
        MujSuperSilnyAgent d = new MujSuperSilnyAgent();
    }
    
    private static abstract class Agent {
        
        public Agent() {
            onInit();
        }
        
        protected abstract void onInit();
        
    }
    
    private static class MujSuperSilnyAgent extends Agent {

        private Fucker fucker; // jsem super silny, proto budu mit sveho fuckera
        
        public MujSuperSilnyAgent() {
            fucker = new Fucker(); // tohle je muj construktor, tady si fuckera vyrobim
        }

        @Override
        protected void onInit() {
            fucker.fuck();
        }
        
    }
    
    private static class Fucker {
        public void fuck() {
            System.out.println("Fuckuju!!");
        }
    }
    
}
