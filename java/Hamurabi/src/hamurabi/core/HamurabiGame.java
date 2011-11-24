/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

import java.util.Scanner;

/**
 *
 * @author jmerxbauer
 */
public class HamurabiGame {
    
    private int currentYear;
    private int population;
    private int bushelsInSilo;
    private int acresOwned;
    private int bushelsPerAcreRate;
    private Summary gameTotalSummary;
    
    public HamurabiGame() {
        gameTotalSummary = new Summary();
    }
    
    public void play() {
        printWelcomeMessage();
        Summary yearSummary = initGame();
        while (!gameOver() && (yearSummary != null)) {
            yearSummary.print();
            Plan plan = requestUserInteraction();
            yearSummary = updateGame(plan);
            gameTotalSummary.add(yearSummary);
        }
        printResults();
    }

    private void printWelcomeMessage() {
        System.out.println("TRY YOUR HAND AT GOVERNING ANCIENT SUMERIA");
        System.out.println("SUCCESSFULLY FOR A 10-YR TERM OF OFFICE.");
        System.out.println("");
    }

    private Summary initGame() {
        currentYear = 1;
        bushelsPerAcreRate = ((int) Math.round(10 * Math.random())) + 17;
        bushelsInSilo = 2800;
        acresOwned = 1000;
        Summary summary = new Summary();
        summary.setPlagueStruck(false);
        summary.setPopulationNew(5);
        summary.setPopulationStarved(0);
        summary.setYear(currentYear);
        return summary;
    }

    private boolean gameOver() {
        if (currentYear == 11) {
            return true;
        }
        return false;
    }

    private Plan requestUserInteraction() {
        Scanner sc = new Scanner(System.in);
        
        System.out.printf("LAND IS TRADING AT %d BUSHELS PER ACRE.\n\n", bushelsPerAcreRate);
        int bushelsAvailable = bushelsInSilo;
        int acresAvailable = acresOwned;
        
        try {
            int acresToBuy = requestAcresToBuy(sc, bushelsPerAcreRate, bushelsAvailable);
            acresAvailable += acresToBuy;
            bushelsAvailable -= acresToBuy * bushelsPerAcreRate;

            int acresToSell = requestAcresToSell(sc, acresAvailable);
            acresAvailable -= acresToSell;
            bushelsAvailable += acresToSell * bushelsPerAcreRate;

            int bushelsToFeed = requestBushelsToFeed(sc, bushelsAvailable);
            bushelsAvailable -= bushelsToFeed;

            int acresToSeed = requestAcresToSeed(sc, bushelsAvailable, acresAvailable, population);

            return new Plan(acresToBuy, acresToSell, bushelsToFeed, acresToSeed);

        } catch (InvalidInputException ex) {
            System.out.println("\n\nHAMURABI:  I CANNOT DO WHAT YOU WISH.");
            System.out.println("GET YOURSELF ANOTHER STEWARD!!!!!");
        }
        
        return null;
    }
    
    private int requestAcresToBuy(Scanner sc, int rate, int budget) {
        int acresToBuy = 0;
        
        do {
            System.out.println("HOW MANY ACRES DO YOU WISH TO BUY");
            acresToBuy = sc.nextInt();
            if (acresToBuy < 0) {
                throw new InvalidInputException();
            } else if ((acresToBuy * rate) > budget) {
                System.out.println("HAMURABI:  THINK AGAIN. YOU HAVE ONLY");
                System.out.printf("%d BUSHELS OF GRAIN.  NOW THEN,\n", budget);
            }
        } while ((acresToBuy * rate) > budget);
        
        return acresToBuy;
    }
    
    private int requestAcresToSell(Scanner sc, int acresAvailable) {
        int acresToSell = 0;

        do {
            System.out.println("HOW MANY ACRES DO YOU WISH TO SELL");
            acresToSell = sc.nextInt();
            if (acresToSell < 0) {
                throw new InvalidInputException();
            } else if (acresToSell > acresAvailable) {
                System.out.printf("HAMURABI:  THINK AGAIN. YOU OWN ONLY %d ACRES.  NOW THEN,\n", acresAvailable);
            }
        } while (acresToSell > acresAvailable);
        
        return acresToSell;
    }
    
    private int requestBushelsToFeed(Scanner sc, int bushelsAvailable) {
        int bushelsToFeed = 0;

        do {
            System.out.println("\nHOW MANY BUSHELS DO YOU WISH TO FEED YOUR PEOPLE");
            bushelsToFeed = sc.nextInt();
            if (bushelsToFeed < 0) {
                throw new InvalidInputException();
            } else if (bushelsToFeed > bushelsAvailable) {
                System.out.println("HAMURABI:  THINK AGAIN. YOU HAVE ONLY");
                System.out.printf("%d BUSHELS OF GRAIN.  NOW THEN,\n", bushelsAvailable);
            }
        } while (bushelsToFeed > bushelsAvailable);
        
        return bushelsToFeed;
    }
    
    private int requestAcresToSeed(Scanner sc, int bushelsAvailable, int acresAvailable, int peopleAvailable) {
        int acresToSeed = 0;

        do {
            System.out.println("\nHOW MANY ACRES DO YOU WISH TO PLANT WITH SEED");
            acresToSeed = sc.nextInt();
            if (acresToSeed < 0) {
                throw new InvalidInputException();
            } else if (acresToSeed > acresAvailable) {
                System.out.printf("HAMURABI:  THINK AGAIN. YOU OWN ONLY %d ACRES.  NOW THEN,\n", acresAvailable);
            } else if (acresToSeed / 2 >= bushelsAvailable) {
                System.out.println("HAMURABI:  THINK AGAIN. YOU HAVE ONLY");
                System.out.printf("%d BUSHELS OF GRAIN.  NOW THEN,\n", bushelsAvailable);
            } else if (acresToSeed >= 10 * peopleAvailable) {
                System.out.printf("BUT YOU HAVE ONLY %d PEOPLE TO TEND THE FIELDS. NOW THEN,\n", peopleAvailable);
            }
        } while (acresToSeed > acresAvailable);
        
        return acresToSeed;
    }

    private Summary updateGame(Plan plan) {
        if (plan == null) {
            return null;
        }
        Summary summary = new Summary();
        
        currentYear++;
        return summary;
    }

    private void printResults() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
