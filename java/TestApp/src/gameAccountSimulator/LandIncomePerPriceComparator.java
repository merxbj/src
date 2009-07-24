package gameAccountSimulator;

import java.util.Comparator;

public class LandIncomePerPriceComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Land l1 = (Land) o1;
        Land l2 = (Land) o2;

        return Double.compare(l1.getIncomePerPrice(), l2.getIncomePerPrice());
    }



}