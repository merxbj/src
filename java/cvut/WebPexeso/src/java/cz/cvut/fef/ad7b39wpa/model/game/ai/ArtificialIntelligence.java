/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.ai;

import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.core.GameTurn;
import cz.cvut.fef.ad7b39wpa.model.game.core.Card;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eTeR
 */
public interface ArtificialIntelligence extends Serializable {

    public void setGame(Game game);
    public void gameUpdatedByTurn(Game updatedGame, GameTurn turn);
    public List<Card> getNextCardsToTurn();

    public enum Difficulty {
        Low(0), Medium(1), High(2), Godlike(3);

        private int difficultyId;
        private static final Map<Integer, Difficulty> idToDifficulty;

        static {
            idToDifficulty = new HashMap<Integer, Difficulty>();
            for (int i = 0; i < Difficulty.values().length; i++) {
                idToDifficulty.put(i, Difficulty.values()[i]);
            }
        }

        private Difficulty(int difficultyId) {
            this.difficultyId = difficultyId;
        }

        public int getStateId() {
            return difficultyId;
        }

        public static Difficulty fromDifficultyId(int difficultyId) {
            return idToDifficulty.get(difficultyId);
        }

    }

}
