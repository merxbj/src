/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author merxbj
 */
public class Summary {
    protected int populationStarved;
    protected int populationNew;
    protected boolean plagueStruck;
    protected int year;
    protected int populationTotal;
    protected int acresOwned;
    protected int harvested;
    protected int eatenByRates;
    protected int bushelsInStock;

    public boolean isPlagueStruck() {
        return plagueStruck;
    }

    public void setPlagueStruck(boolean plagueStruck) {
        this.plagueStruck = plagueStruck;
    }

    public int getPopulationNew() {
        return populationNew;
    }

    public void setPopulationNew(int populationNew) {
        this.populationNew = populationNew;
    }

    public int getPopulationStarved() {
        return populationStarved;
    }

    public void setPopulationStarved(int populationStarved) {
        this.populationStarved = populationStarved;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
    public void print() {
        System.out.println("\n\n\nHAMURABI:  I BEG TO REPORT TO YOU,");
        System.out.printf("IN YEAR %d, %d PEOPLE STARVED, %d CAME TO THE CITY.\n", year, populationStarved, populationNew);
        if (isPlagueStruck()) {
            System.out.println("A HORRIBLE PLAGUE STRUCK!  HALF THE PEOPLE DIED.");
        }
        System.out.printf("POPULATION IS NOW %d\n", populationTotal);
        System.out.printf("THE CITY NOW OWNS %d ACRES.\n", acresOwned);
        System.out.printf("YOU HARVESTED %d BUSHELS PER ACRE.\n", harvested);
        System.out.printf("RATS ATE %d BUSHELS.\n", eatenByRates);
        System.out.printf("YOU NOW HAVE %d BUSHELS IN STORE.\n", bushelsInStock);
        System.out.println("");
    }

    public int getAcresOwned() {
        return acresOwned;
    }

    public void setAcresOwned(int acresOwned) {
        this.acresOwned = acresOwned;
    }

    public int getBushelsInStock() {
        return bushelsInStock;
    }

    public void setBushelsInStock(int bushelsInStock) {
        this.bushelsInStock = bushelsInStock;
    }

    public int getEatenByRates() {
        return eatenByRates;
    }

    public void setEatenByRates(int eatenByRates) {
        this.eatenByRates = eatenByRates;
    }

    public int getHarvested() {
        return harvested;
    }

    public void setHarvested(int harvested) {
        this.harvested = harvested;
    }

    public int getPopulationTotal() {
        return populationTotal;
    }

    public void setPopulationTotal(int populationTotal) {
        this.populationTotal = populationTotal;
    }
}
