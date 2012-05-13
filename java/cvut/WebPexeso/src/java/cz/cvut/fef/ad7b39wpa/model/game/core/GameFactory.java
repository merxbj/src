/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.core;

import cz.cvut.fef.ad7b39wpa.model.game.ai.ArtificialIntelligence;
import cz.cvut.fef.ad7b39wpa.model.game.ai.BasicAi;
import cz.cvut.fef.ad7b39wpa.model.game.ai.ComputerPlayer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eTeR
 */
public class GameFactory implements Serializable {

    public static Game createNewGame() {
        Field field = new Field(CardDeckFactory.createBasicCardDeck());

        List<Player> players = new ArrayList<Player>();
        //players.add(new Player("Player 1", 0, 0));
        players.add(new ComputerPlayer("Player 1", 0, 0, new BasicAi(ArtificialIntelligence.Difficulty.Godlike)));
        players.add(new ComputerPlayer("Player 2", 0, 0, new BasicAi(ArtificialIntelligence.Difficulty.Godlike)));
        
        Game newGame = new Game(players, field);
        newGame.initialize();

        return newGame;
    }

    public static Game createNewGame(String[] types, String[] names) {
        Field field = new Field(CardDeckFactory.createBasicCardDeck());

        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < Math.min(types.length, names.length); i++) {
            int playerType = Integer.parseInt(types[i]);
            if (playerType > -1) {
                ArtificialIntelligence.Difficulty diff = ArtificialIntelligence.Difficulty.fromDifficultyId(playerType);
                players.add(new ComputerPlayer(names[i], 0, 0, new BasicAi(diff)));
            } else {
                players.add(new Player(names[i], 0, 0));
            }
        }
        
        Game newGame = new Game(players, field);
        newGame.initialize();

        return newGame;
    }

}
