/*
 * MergeSort
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
public class MergeSort {

    public <T extends Comparable<T>> void sort(T[] dst) {
        T[] src = (T[]) dst.clone();
        mergeSort(src, dst, 0, dst.length);
    }

    private <T extends Comparable<T>> void mergeSort(T[] src, T[] dst, int from, int to) {
        if (to - from > 1) {
            int mid = (to + from) / 2;
            mergeSort(dst, src, from, mid);
            mergeSort(dst, src, mid, to);
            merge(src, dst, from, mid, to);
        }
    }

    private <T extends Comparable<T>> void merge(T[] src, T[] dst, int from, int mid, int to) {
        int i = from;
        int j = mid;
        for (int k = from; k < to; k++) {
            if (j == to || (i < mid && (src[i].compareTo(src[j]) == -1))) {
                dst[k] = src[i++];
            } else {
                dst[k] = src[j++];
            }
        }
    }

}
