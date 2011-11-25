/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author jmerxbauer
 */
public class GameTotalSummary extends Summary {

    protected int populationStarvedAvarage;
    
    @Override
    public void print() {
        
        int acresPerPerson = acresOwned / populationTotal;
        System.out.printf("IN YOUR 10-YEAR TERM OF OFFICE, %d PERCENT OF THE", populationStarvedAvarage);
        System.out.println("POPULATION STARVED PER YEAR ON AVERAGE, I.E., A TOTAL OF");
        System.out.printf("%d PEOPLE DIED!!", populationStarved);
        System.out.println("YOU STARTED WITH 10 ACRES PER PERSON AND ENDED WITH");
        System.out.printf("%d ACRES PER PERSON.\n\n", acresPerPerson);
        if (populationStarvedAvarage > 33 || acresPerPerson < 7) {
            throw new HamurabiMismanagementException();
        } else if (populationStarvedAvarage > 10 || acresPerPerson < 9) {
            System.out.println("YOUR HEAVY-HANDED PERFORMANCE SMACKS OF NERO AND IVAN IV.");
            System.out.println("THE PEOPLE (REMAINING) FIND YOU AN UNPLEASANT RULER, AND,");
            System.out.println("FRANKLY, HATE YOUR GUTS!");
        } else if (populationStarvedAvarage > 3 || acresPerPerson < 10) {
            System.out.println("YOUR PERFORMANCE COULD HAVE BEEN SOMEWHAT BETTER, BUT");
            System.out.println("REALLY WASN'T TOO BAD AT ALL. ");
            System.out.printf("%d PEOPLE WOULD\n", (int) (populationTotal * 0.8 * Math.random()));
            System.out.println("DEARLY LIKE TO SEE YOU ASSASSINATED BUT WE ALL HAVE OUR");
            System.out.println("TRIVIAL PROBLEMS.");
        } else {
            System.out.println("A FANTASTIC PERFORMANCE!!!  CHARLEMANGE, DISRAELI, AND");
            System.out.println("JEFFERSON COMBINED COULD NOT HAVE DONE BETTER!");
        }
    }

    public void add(Summary summary) {       
        int originalPopulation = summary.populationTotal - summary.populationNew + summary.populationStarved;
        populationStarvedAvarage = ((summary.year - 2) * populationStarvedAvarage + summary.populationStarved * 100 / originalPopulation) / (summary.year - 1);
        populationStarved += summary.populationStarved;
        acresOwned = summary.acresOwned;
        populationTotal = summary.populationTotal;
    }

    public int getPopulationStarvedAvarage() {
        return populationStarvedAvarage;
    }

    public void setPopulationStarvedAvarage(int populationStarvedAvarage) {
        this.populationStarvedAvarage = populationStarvedAvarage;
    }

}
