package gameAccountSimulator;

import java.util.*;

public class PurchaseInfo {

    public PurchaseInfo() {
        this.purchases = new TreeMap<Land, LandPurchaseInfo>(new LandCurrentPriceComparator());
    }

    public void purchase(Land land) {
        if (purchases.containsKey(land)) {
            LandPurchaseInfo lpi = purchases.get(land);
            lpi.boughtOneMore();
        } else {
            LandPurchaseInfo lpi = new LandPurchaseInfo(land);
            purchases.put(land, lpi);
        }
    }

    public void printReceipt() {
        for (LandPurchaseInfo lpi : purchases.values()) {
            System.out.println(lpi.toString());
        }
    }

    public void clear() {
        purchases.clear();
    }
    
    private SortedMap<Land, LandPurchaseInfo> purchases;
}
