/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.application;

import solver.actions.ActionInterpreter;
import solver.actions.ActionPrinter;
import solver.core.FrenchPrimaryEasyDeckFactory;
import solver.core.SpideretteSolitaire;
import solver.core.Table;
import solver.player.SolitairePlayer;

/**
 *
 * @author jmerxbauer
 */
public class SolitaireSolver {

    public static void main(String[] args) {
        
        SpideretteSolitaire game = new SpideretteSolitaire();
        Table table = game.initNewGame(new FrenchPrimaryEasyDeckFactory());
        
        SolitairePlayer player = new SolitairePlayer();
        ActionInterpreter printer = new ActionPrinter();

        try {
            if (player.play(table)) {
                printer.interpret(player.getActions());
            } else {
                System.out.println("Game was not ");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

}
