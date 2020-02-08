/*
 * SyncTest
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

package sync;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class SyncTest {
    
    public static void main(String[] args) throws InterruptedException {
        Counter c = new Counter();
        Thread t1 = new Thread(new IncWorker(c));
        Thread t2 = new Thread(new DecWorker(c));

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println(String.format("final counter = %d", c.counter));
    }

    private static class IncWorker implements Runnable {

        private Counter c;

        public IncWorker(Counter c) {
            this.c = c;
        }

        public void run() {
            for (int i = 0; i < 10; i++) {
                c.inc();
            }
        }

        
    }

    private static class DecWorker implements Runnable {

        private Counter c;

        public DecWorker(Counter c) {
            this.c = c;
        }

        public void run() {
            for (int i = 0; i < 10; i++) {
                c.dec();
            }
        }
    }

    private static class Counter {

        private int counter = 0;

        public void inc() {
            synchronized (this) {
                counter++;
                double d = 321456987;
                for (long i = 0; i < 100000; i++) {
                    d = Math.sqrt(d);
                    d = Math.pow(d, d / 2);
                }
                System.out.println("Result = " + d);
                System.out.println(String.format("%d: counter++ = %d", Thread.currentThread().getId(), counter));
            }
        }

        public void dec() {
            synchronized (this) {
                counter--;
                System.out.println(String.format("%d: counter-- = %d", Thread.currentThread().getId(), counter));
            }
        }

    }

}
