/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.comparator;

import java.util.Date;

/**
 * Predek trid zajistujici metody porovnani pro p:datatable.
 * Metody teto tridy by mely byt vzdy vlaknove bezpecne!
 * @author Tomas Kadlec <tomas@tomaskadlec.net>
 */
public class Comparator {

    /**
     * Pomocna metoda - Porovna retezce lexikograficky
     * @param o1 - prvni retezec
     * @param o2 - druhy retezec
     * @return 1 pokud o1 > o2, -1 pokud o1 < o2, 0 pokud o1 == o2
     */
    protected int cmpStrings(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;
        return s1.compareTo(s2);
    }

    /**
     * Pomocna metoda pro porovnani cisel
     * @param o1 - prvni cislo
     * @param o2 - druhe cislo
     * @return 1 pokud o1 > o2, -1 pokud o1 < o2, 0 pokud o1 == o2
     */
    protected int cmpNumbers(Object o1, Object o2) {
        try {
            double d1 = this.castNumber(o1);
            double d2 = this.castNumber(o2);
            if (d1 > d2) {
                return 1;
            } else if (d1 < d2) {
                return -1;
            } else {
                return 0;
            }
        } catch (IllegalStateException e) {
            return -1;
        }
    }

    /**
     * Pomocna metoda pro pretypovani Integer, Double -> double
     * @param o - instance Integer nebo Double k prevedeni
     * @return double hodnota
     * @throws IllegalStateException - pokud o neni instance Integer nebo Double
     */
    protected double castNumber(Object o) throws IllegalStateException {
        if (o instanceof Double) {
            return ((Double) o).doubleValue();
        } else if (o instanceof Integer) {
            return (double) (((Integer) o).intValue());
        } else if (o instanceof Boolean) {
            if (((Boolean) o).booleanValue()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Porovna dve data.
     * @param o1 - prvni
     * @param o2 - druhe
     * @return 1 pokud o1 > o2, -1 pokud o1 < o2, 0 pokud o1 == o2
     */
    protected int cmpDatum(Object o1, Object o2) {
        Date d1 = (Date) o1;
        Date d2 = (Date) o2;
        return d1.compareTo(d2);
    }

}
