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
        Derived d = new Derived();
    }
    
    private static class Base {
        
        protected String string;

        public Base() {
            init();
            if (this.string.compareTo("Base::init()") == 0) {
                System.out.println("Asi je vse OK!");
            }
        }
        
        protected void init() {
            string = "Base::init()";
        }

        public String getString() {
            return string;
        }
        
    }
    
    private static class Derived extends Base {

        public Derived() {
        }

        @Override
        protected void init() {
            string = null; // to sem ale svine!!!
        }
        
    }
    
}
