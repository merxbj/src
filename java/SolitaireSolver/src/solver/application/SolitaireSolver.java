/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package solver.application;

import solver.core.GameProgress;
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
        Table table = game.initNewGame();
        
        SolitairePlayer player = new SolitairePlayer();
        GameProgress results = new GameProgress();
        
        try {
            player.play(table, results);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            results.print();
        }
        
    }

}
