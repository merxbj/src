package gameAccountSimulator;

import java.util.ArrayList;

public class GameAccountSimulator {

    public GameAccountSimulator() {
        lands = new LandCollection();
        accountBalance = 0;
        endingConditions = new ArrayList<EndingConditionType>();
        targetIncome = 0;
        totalTimeElapsed = 0;
        tickDuration = 50;
        targetCyclesCount = Integer.MAX_VALUE;
    }

    public void createCommonLandList() {
        lands.createCommonLandCollection();
    }

    public void createCustomizedLandList(int[] counts) {
        lands.createCustomizedLandCollection(counts);
    }

    public void addEndingConditionType(EndingConditionType ect) {
        this.endingConditions.add(ect);
    }

    public void removeEndingConditionType(EndingConditionType ect) {
        this.endingConditions.remove(ect);
    }

    public void setTargetIncome(double balance) {
        this.targetIncome = balance;
    }

    public void setStartingAmount(double amount) {
        this.accountBalance = amount;
    }

    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getTargetCyclesCount() {
        return targetCyclesCount;
    }

    public void setTargetCyclesCount(int targetCyclesCount) {
        this.targetCyclesCount = targetCyclesCount;
    }

    public void updateAccount() {

        // buy new land(s)
        boolean buyMore = true;

        while (buyMore) {
            buyMore = false;

            if (lands.getTotalIncome() > 0) {
                double balance = lands.buyLand(lands.getBestLand(), accountBalance);

                if (balance > 0) {
                    accountBalance = balance;
                    buyMore = true;
                }
            }
            else {
                Land land = lands.getEffortableLand(accountBalance);
                if (land != null)
                    accountBalance = lands.buyLand(land, accountBalance);
                else
                    // ouch! we haven't enough money to buy first land!
                    endingConditions.add(EndingConditionType.FORCED);
            }
        }

        lands.printRecentlyBought();
        totalTimeElapsed += tickDuration;
        accountBalance += lands.getTotalIncome();
    }

    private void printResults() {
        System.out.println("Simulation ended ...");
        System.out.println(String.format("Total time elapsed: %d days %d hours and %d minutes", totalTimeElapsed / 1440, (totalTimeElapsed % 1440) / 60, (totalTimeElapsed % 1440) % 60));
        
    }

    public void run() {
        System.out.println("Simulation started ...");
        while (!passedEndingCondition()) {
            updateAccount();
        }
        printResults();
    }

    private boolean passedEndingCondition() {
        boolean passed = false;
        for (EndingConditionType ect : endingConditions) {
            switch (ect) {
                case TARGET_INCOME:
                    passed = (targetIncome <= lands.getTotalIncome());
                    break;
                case CYCLES_PASSED:
                    passed = (totalTimeElapsed / tickDuration) == targetCyclesCount;
                    break;
                case FORCED:
                    passed = true;
                default:
                    passed = false;
            }
            if (passed)
                break;
        }

        return passed;
    }

    private LandCollection lands;
    private double accountBalance;
    private long totalTimeElapsed;
    private int tickDuration;

    private ArrayList<EndingConditionType> endingConditions;
    private double targetIncome;
    private int targetCyclesCount;
}
