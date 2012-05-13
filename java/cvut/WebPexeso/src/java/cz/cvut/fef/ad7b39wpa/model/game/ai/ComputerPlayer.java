/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.ai;

import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.core.GameTurn;
import cz.cvut.fef.ad7b39wpa.model.game.core.Card;
import cz.cvut.fef.ad7b39wpa.model.game.core.Player;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author eTeR
 */
public class ComputerPlayer extends Player {

    ArtificialIntelligence ai;

    public ComputerPlayer(String name, int attempts, int score, ArtificialIntelligence ai) {
        super(name, attempts, score);
        this.ai = ai;
    }

    /**
     * CPU player has to do his own observation of the game!
     * @param observable
     * @param arg
     */
    @Override
    public void update(Observable observable, Object arg) {
        if ((observable instanceof Game) && (arg instanceof GameTurn)) {
            ai.gameUpdatedByTurn((Game) observable, (GameTurn) arg);
        }
    }

    @Override
    public GameTurn getNextTurn() {
        List<Card> cards = ai.getNextCardsToTurn();
        return new GameTurn(this, cards.get(0), cards.get(1));
    }

    @Override
    public String getName() {
        return "PC: " + super.getName();
    }

    @Override
    public void setGame(Game game) {
        super.setGame(game);
        ai.setGame(game);
    }

}
