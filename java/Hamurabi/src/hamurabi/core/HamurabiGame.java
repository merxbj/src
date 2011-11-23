/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hamurabi.core;

/**
 *
 * @author jmerxbauer
 */
public class HamurabiGame {
    
    private int currentYear;
    private int population;
    
    public HamurabiGame() {
    }
    
    public void play() {
        printWelcomeMessage();
        YearSummary summary = initGame();
        while (!gameOver()) {
            summary.print();
            YearPlan plan = requestUserInteraction();
            summary = updateGame(plan);
        }
        printResults();
    }

    private void printWelcomeMessage() {
        System.out.println("TRY YOUR HAND AT GOVERNING ANCIENT SUMERIA");
        System.out.println("SUCCESSFULLY FOR A 10-YR TERM OF OFFICE.");
        System.out.println("");
    }

    private YearSummary initGame() {
        currentYear = 1;
        YearSummary summary = new YearSummary();
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

    private YearPlan requestUserInteraction() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private YearSummary updateGame(YearPlan plan) {
        YearSummary summary = new YearSummary();
        currentYear++;
        return summary;
    }

    private void printResults() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
