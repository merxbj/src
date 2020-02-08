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
    private GameTotalSummary gameTotalSummary;
    
    public HamurabiGame() {
        gameTotalSummary = new GameTotalSummary();
    }
    
    public void play() {
        try {
            printWelcomeMessage();
            Summary yearSummary = initGame();
            while (!gameOver() && (yearSummary != null)) {
                yearSummary.print();
                Plan plan = requestUserInteraction();
                yearSummary = updateGame(plan);
                gameTotalSummary.add(yearSummary);
            }
            gameTotalSummary.print();
            printGoodByeMessage();
        } catch (TooManyPeopleStarvedException ex) {
            printStarvedImpeachmentMessage(ex.getPeopleStarved());
        } catch (HamurabiMismanagementException ex) {
            printImpeachmentMessage();
        } catch (InvalidInputException ex) {
            printStewardComplaint();
        }
    }

    private void printWelcomeMessage() {
        System.out.println("TRY YOUR HAND AT GOVERNING ANCIENT SUMERIA");
        System.out.println("SUCCESSFULLY FOR A 10-YR TERM OF OFFICE.");
        System.out.println("");
    }

    private Summary initGame() {
        currentYear = 1;
        bushelsPerAcreRate = (int)(10 * Math.random()) + 17;
        bushelsInSilo = 2800;
        acresOwned = 1000;
        population = 100;
        Summary summary = new Summary();
        summary.setPlagueStruck(false);
        summary.setPopulationNew(5);
        summary.setPopulationStarved(0);
        summary.setYear(currentYear);
        summary.setPopulationTotal(population);
        summary.setAcresOwned(acresOwned);
        summary.setHarvestedPerAcre(3);
        summary.setEatenByRates(200);
        summary.setBushelsInSilo(bushelsInSilo);
        
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
        
        currentYear++;
        
        acresOwned += (plan.getAcresToBuy() - plan.getAcresToSell());
        bushelsInSilo += ((plan.getAcresToSell() * bushelsPerAcreRate) - (plan.getAcresToBuy() * bushelsPerAcreRate));
        bushelsInSilo -= (plan.getBushelsToFeed() + (plan.getAcresToSeed() / 2));
        
        int harvestPerAcre = (int) (5 * Math.random()) + 1;
        int harvested = harvestPerAcre * plan.getAcresToSeed();
        
        int consumedByRats = 0;
        int ratsConsumptionFactor = (int) (5 * Math.random()) + 1;
        if ((ratsConsumptionFactor % 2) == 0) {
            consumedByRats = bushelsInSilo / ratsConsumptionFactor;
        }

        bushelsInSilo += harvested - consumedByRats;
        
        int newPopulationFactor = (int) (5 * Math.random()) + 1;
        int newPopulation = (int) (newPopulationFactor * (20 * acresOwned + bushelsInSilo) / population / 100 + 1);
        
        int peopleFeeded = plan.getBushelsToFeed() / 20;
        int peopleStarved = population - peopleFeeded;
        if (peopleStarved > (0.45 * population)) {
            throw new TooManyPeopleStarvedException(peopleStarved);
        }

        boolean plagueStruck = ((int) 10 * (2 * Math.random()- 0.3)) <= 0;

        population += newPopulation;
        if (plagueStruck) {
            population /= 2;
        }

        Summary summary = new Summary();
        summary.setPlagueStruck(plagueStruck);
        summary.setPopulationNew(newPopulation);
        summary.setPopulationStarved(peopleStarved);
        summary.setYear(currentYear);
        summary.setAcresOwned(acresOwned);
        summary.setBushelsInSilo(bushelsInSilo);
        summary.setEatenByRates(consumedByRats);
        summary.setHarvestedPerAcre(harvestPerAcre);
        summary.setPopulationTotal(population);

        return summary;
    }

    private void printStarvedImpeachmentMessage(int peopleStarved) {
        System.out.printf("\n\nYOU STARVED %d PEOPLE IN ONE YEAR!!!\n", peopleStarved);
        printImpeachmentMessage();
    }

    private void printImpeachmentMessage() {
        System.out.println("DUE TO THIS EXTREME MISMANAGEMENT YOU HAVE NOT ONLY");
        System.out.println("BEEN IMPEACHED AND THROWN OUT OF OFFICE BUT YOU HAVE");
        System.out.println("ALSO BEEN DECLARED 'NATIONAL FINK' !!");
    }

    private void printGoodByeMessage() {
        System.out.println("SO LONG FOR NOW.\n");
    }

    private void printStewardComplaint() {
        System.out.println("\n\nHAMURABI:  I CANNOT DO WHAT YOU WISH.");
        System.out.println("GET YOURSELF ANOTHER STEWARD!!!!!");
    }
}
