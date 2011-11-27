/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DA.common;

import DA.TreeMap.SimpleFile;
import java.util.Comparator;

/**
 *
 * @author mrneo
 */
public enum SimpleFileComparator implements Comparator<SimpleFile> {

    SORT_BY_SIZE {

        @Override
        public int compare(SimpleFile file1, SimpleFile file2) {
            Long size1 = file1.getSize();
            Long size2 = file2.getSize();
            return size2.compareTo(size1);
        }
    },
    SORT_BY_NAME {

        @Override
        public int compare(SimpleFile o1, SimpleFile o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}