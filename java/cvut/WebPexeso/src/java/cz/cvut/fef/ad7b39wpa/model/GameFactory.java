/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model;

import java.io.Serializable;

/**
 *
 * @author eTeR
 */
public class GameFactory implements Serializable {

    public GameFactory() {
    }

    public static Game createNewGame() {
        Game game = new Game();
        game.createNewGame(CardDeckFactory.createBasicCardDeck());
        return game;
    }

}
