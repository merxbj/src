/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author merxbj
 */
class YearSummary {
    private int populationStarved;
    private int populationNew;
    private boolean plagueStruck;
    private int year;
    private int populationTotal;
    private int acresOwned;
    private int harvested;
    private int eatenByRates;
    private int bushelsInStock;

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
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("HAMURABI:  I BEG TO REPORT TO YOU,");
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
}
