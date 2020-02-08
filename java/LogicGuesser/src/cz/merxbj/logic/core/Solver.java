/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.logic.core;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author jmerxbauer
 */
public class Solver {

    private int positions;
    private boolean repetitions;
    
    public Solver(int positions, boolean repetitions) {
        this.positions = positions;
        this.repetitions = repetitions;
    }
    
    public Solution solve(LogicGame game) {
        Deque<Solution> solutions = getAllPossibleSolutions();
        
        Solution correctSolution = null;
        while (correctSolution == null) {
            
            System.out.printf("List of all possible solutions is now %d large.\n", solutions.size());
            
            if (solutions.size() == 0) {
                break;
            }
            Solution guess = solutions.getFirst(); // random - hehe
            Evaluation eval = game.evaluate(guess);
            if (eval.getWellplaced() == positions) {
                correctSolution = guess; // we got it right!
            } else {
                removeAllImpossibleSolutions(solutions, guess, eval);
            }
        }
        
        return correctSolution;
    }

    private Deque<Solution> getAllPossibleSolutions() {
        Deque<Solution> solutions = new LinkedList<>();
        
        fillSolutions(solutions, Arrays.asList(Marble.values()), null, 0);
            
        return solutions;
    }

    private void fillSolutions(Deque<Solution> solutions, List<Marble> marbles, Solution currentSolution, int currentPosition) {
        
        if (currentSolution == null) {
            currentSolution = new Solution();
        }
        
        if (currentPosition == positions) {
            Solution solution = new Solution(currentSolution);
            solutions.add(solution);
            return;
        }

        for (Marble m : marbles)
        {
            currentSolution.set(currentPosition, m);
            
            List<Marble> availableMarbles;
            if (repetitions) {
                availableMarbles = marbles;
            } else {
                availableMarbles = new LinkedList<>(marbles);
                availableMarbles.remove(m);
            }

            fillSolutions(solutions, availableMarbles, currentSolution, currentPosition + 1);
        }
    }

    private void removeAllImpossibleSolutions(Deque<Solution> solutions, Solution guess, Evaluation evaluation) {
        Deque<Solution> impossible = new LinkedList<>();
        for (Solution solution : solutions) {
            Evaluation eval = solution.evaluate(guess);
            if (!evaluation.equals(eval)) {
                impossible.add(solution);
            }
        }
        solutions.removeAll(impossible);
        System.out.printf("Discarded %d impossible solutions. ", impossible.size());
    }
    
}
