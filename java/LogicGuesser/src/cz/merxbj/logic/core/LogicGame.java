/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.logic.core;

import java.util.*;

/**
 *
 * @author jmerxbauer
 */
public class LogicGame {

    private Solution solution;
    private int positions;
    private boolean repetitions;

    public LogicGame(int positions, boolean repetitions) {
        this.solution = new Solution();
        this.positions = positions;
        this.repetitions = repetitions;
    }
    
    public void init() {
        generateRandomSolution();
    }

    public Evaluation evaluate(Solution guess) {
        return solution.evaluate(guess);
    }

    private void generateRandomSolution() {
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        List<Marble> availableMarbles = new LinkedList<>(Arrays.asList(Marble.values()));
        for (int pos = 0; pos < positions; pos++) {
            Marble m = availableMarbles.get(random.nextInt(availableMarbles.size()));
            if (!repetitions) {
                availableMarbles.remove(m);
            }
            solution.set(pos, m);
        }
    }

    public Solver createSolver() {
        return new Solver(positions, repetitions);
    }
    
}
