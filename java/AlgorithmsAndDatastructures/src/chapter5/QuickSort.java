/*
 * QuickSort
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

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class QuickSort {

    public <T extends Comparable<T>> void sort(T[] dst) {
        quickSort(dst, 0, dst.length - 1);
    }

    private <T extends Comparable<T>> void quickSort(T[] dst, int from, int to) {
        T p = dst[(from + to) / 2];
        int i = from;
        int j = to;
        while (i <= j) {
            while (dst[i].compareTo(p) == -1) {
                i++;
            }
            while (dst[j].compareTo(p) == 1) {
                j--;
            }
            if (i <= j) {
                swap(dst, i++, j--);
            }
        }
        if (from < j) {
            quickSort(dst, from, j);
        }
        if (to > i) {
            quickSort(dst, i, to);
        }
    }

    private <T> void swap(T[] a, int what, int with) {
        T tmp = a[what];
        a[what] = a[with];
        a[with] = tmp;
    }

}
