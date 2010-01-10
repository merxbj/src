package gameAccountSimulator;

public class GameAccountSimulator {

    public GameAccountSimulator() {
        lands = new LandCollection();
        accountBalance = 0;
        ect = EndingConditionType.TARGET_INCOME;
        targetIncome = 0;
        totalTimeElapsed = 0;
        tickDuration = 50;
    }

    public void createCommonLandList() {
        lands.createCommonLandCollection();
    }

    public void createCustomizedLandList(int[] counts) {
        lands.createCustomizedLandCollection(counts);
    }

    public void setEndingConditionType(EndingConditionType ect) {
        this.ect = ect;
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

    public void updateAccount() {
        // update balance
        accountBalance += lands.getTotalIncome();

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
                    ect = EndingConditionType.FORCED;
            }
        }

        lands.printRecentlyBought();
        totalTimeElapsed += tickDuration;

    }

    private void printResults() {
        System.out.println("Simulation ended ...");
        System.out.println(String.format("Total time elapsed: %d", totalTimeElapsed));
        
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
        
        switch (ect) {
            case TARGET_INCOME:
                passed = (targetIncome <= lands.getTotalIncome());
                break;
            case FORCED:
                passed = true;
                break;
            default:
                passed = false;
        }

        return passed;
    }

    private LandCollection lands;
    private double accountBalance;
    private long totalTimeElapsed;
    private int tickDuration;

    private EndingConditionType ect;
    private double targetIncome;
}
