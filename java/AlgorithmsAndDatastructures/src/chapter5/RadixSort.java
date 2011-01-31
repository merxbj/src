/*
 * RadixSort
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

package chapter5;

import java.util.HashMap;
import java.util.LinkedList;


/**
 * Not really working now. But almost. The basic concept has been caught!
 *
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class RadixSort {

    public void sort(Integer[] a) {
        HashMap<Integer, LinkedList<Integer>> buckets = new HashMap<Integer, LinkedList<Integer>>();
        int d = 1;
        int updates = 0;
        do {
            updates = 0;
            int pos = (int) Math.pow(10, d);

            for (int i = 0; i < a.length; i++) {
                if (pos <= a[i]) {
                    int digit = (a[i] % pos);
                    LinkedList<Integer> bucket;
                    if (!buckets.containsKey(digit)) {
                        bucket = new LinkedList<Integer>();
                        buckets.put(digit, bucket);
                    } else {
                        bucket = buckets.get(digit);
                    }
                    bucket.offer(a[i]);
                    updates++;
                }
            }

            int j = 0;
            for (int i = 0; i < 10; i++) {
                if (buckets.containsKey(i)) {
                    LinkedList<Integer> bucket = buckets.get(i);
                    Integer n = bucket.poll();
                    while (n != null) {
                        a[j++] = n;
                        n = bucket.poll();
                    }
                }
            }
            d++;
        } while (updates > 0 && d < 6);
    }

}
