/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.merxbj.logic.application;

import cz.merxbj.logic.core.LogicGame;
import cz.merxbj.logic.core.Solution;
import cz.merxbj.logic.core.Solver;

/**
 *
 * @author jmerxbauer
 */
public class Application {

    public static void main(String[] args) {
        
        int positions = 4;
        boolean repetitions = true;
        
        LogicGame game = new LogicGame(positions, repetitions);
        game.init();
        
        Solver solver = game.createSolver();
        Solution solution = solver.solve(game);
        
        System.out.println(solution);
    }
}
