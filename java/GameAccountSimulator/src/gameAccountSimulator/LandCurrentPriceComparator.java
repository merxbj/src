package gameAccountSimulator;

import java.util.Comparator;

public class LandCurrentPriceComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Land l1 = (Land) o1;
        Land l2 = (Land) o2;

        return Double.compare(l2.getCurrentPrice(), l1.getCurrentPrice());
    }



}
