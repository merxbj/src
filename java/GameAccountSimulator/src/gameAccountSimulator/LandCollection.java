package gameAccountSimulator;

import java.util.*;

public class LandCollection {

    LandCollection() {
        factory = new LandFactory();
        lands = new ArrayList<Land>();
        recentlyBought = new ArrayList<Land>();
    }

    public void createCommonLandCollection() {
        lands.add(factory.newLand(LandType.FARM));
        lands.add(factory.newLand(LandType.BARRAKS));
        lands.add(factory.newLand(LandType.BLACKSMITH));
        lands.add(factory.newLand(LandType.CASTLE_KEEP));
        lands.add(factory.newLand(LandType.LUMBER_MILL));
        lands.add(factory.newLand(LandType.ROYAL_CASTLE));
        lands.add(factory.newLand(LandType.ROYAL_TRADE_ROUTE));
        lands.add(factory.newLand(LandType.SHIPYARD));
        lands.add(factory.newLand(LandType.STONE_QUARRY));
        lands.add(factory.newLand(LandType.TAVERN));
        lands.add(factory.newLand(LandType.TEMPLE));
        lands.add(factory.newLand(LandType.VILLAGE));
        lands.add(factory.newLand(LandType.GOLD_MINE));
    }

    public void createCustomizedLandCollection(int [] counts) {
        for (int i = 0; i < counts.length; i++) {
            lands.add(factory.newLand(LandType.values()[i], counts[i]));
        }
    }

    public void printLandList() {

        for (Iterator iter = lands.iterator(); iter.hasNext();) {

            Land land = (Land) iter.next();

            System.out.println(land.toString());
        }

    }

    public Land getBestLand() {
        Collections.sort(lands, new LandIncomePerPriceComparator());
        
        return lands.get(0);
    }

    public double buyLand(Land land, double budget) {
        double balance = budget - land.getCurrentPrice();
        
        if (balance > 0.0) {
            land.incQuantity();
            recentlyBought.add(land);
        }

        return balance;
    }

    public double getTotalIncome() {
        double income = 0.0;

        for (Iterator iter = lands.iterator(); iter.hasNext();) {

            Land land = (Land) iter.next();

            income += land.getTotalIncome();
        }

        return income;
    }

    public Land getEffortableLand(double budget) {
        Land effortableLand = null;
        Collections.sort(lands, new LandCurrentPriceComparator());

        Iterator iter = lands.iterator();
        while (iter.hasNext()) {
            Land land = (Land) iter.next();
            if (land.getCurrentPrice() <= budget) {
                effortableLand = land;
                break;
            }

        }

        return effortableLand;
    }

    public void printRecentlyBought() {
        Collections.sort(recentlyBought, new LandCurrentPriceComparator());
        for (Land l : recentlyBought) {
            System.out.println(String.format("Bought land! %s", l.toString()));
        }
        System.out.println("-------------------------------------------------");
        recentlyBought.clear();
    }
    
    private ArrayList<Land> lands;
    private LandFactory factory;
    private ArrayList<Land> recentlyBought;

}
