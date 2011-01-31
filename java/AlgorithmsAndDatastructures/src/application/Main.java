package application;

import chapter5.InsertionSort;
import chapter5.MergeSort;
import chapter5.QuickSort;
import chapter5.RadixSort;
import chapter6.AddressablePriorityQueue;
import chapter6.BinaryHeap;
import chapter6.BinomialTree;
import chapter6.Element;
import chapter6.HeapSorter;
import chapter6.PriorityQueue;
import java.util.Arrays;

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

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Main {
    public static void main(String[] args) {


        Element arr[] = {new Element(4, "Ctyri"),
            new Element(3, "Tri"),
            new Element(2, "Dva"),
            new Element(1, "Jedna"),
            new Element(11, "Jedenact"),
            new Element(5, "Pet"),
            new Element(18, "Osumnact")};
        
        AddressablePriorityQueue<Integer, String> apq = new BinomialTree<Integer, String>();
        apq.build(arr);

        System.out.println(apq.deleteMin());
        System.out.println(apq.deleteMin());
        System.out.println(apq.deleteMin());
        System.out.println(apq.deleteMin());
        System.out.println(apq.deleteMin());
        System.out.println(apq.deleteMin());

    }

}
