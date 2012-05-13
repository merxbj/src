/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cvut.fef.ad7b39wpa.model.game.score;

import cz.cvut.fef.ad7b39wpa.model.game.core.Game;
import cz.cvut.fef.ad7b39wpa.model.game.core.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author eTeR
 */
public class ScoreBoard implements Serializable {
    private Map<String, PlayerScore> playerScores;

    public ScoreBoard() {
        playerScores = new HashMap<String, PlayerScore>();
    }

    public Collection<PlayerScore> getPlayerScores() {
        return playerScores.values();
    }

    public void analyzeGame(Game game) {
        List<Player> gamePlayers = new ArrayList<Player>(game.getPlayers());
        List<Player> winners = getWinners(gamePlayers);
        for (Player player : gamePlayers) {
            PlayerScore ps = playerScores.get(player.getName());
            if (ps == null) {
                ps = new PlayerScore(player.getName(), 0, 0);
                playerScores.put(player.getName(), ps);
            }

            ps.incGames();
            if (winners.contains(player)) {
                ps.incWins();
            }
        }
    }

    private List<Player> getWinners(List<Player> players) {
        List<Player> winners = new ArrayList<Player>();
        for (Player player : players) {
            if (winners.isEmpty()) {
                winners.add(player);
            } else if (player.getScore() > winners.get(0).getScore()) {
                winners.clear();
                winners.add(player);
            } else if (player.getScore() == winners.get(0).getScore()) {
                winners.add(player);
            }
        }
        return winners;
    }

}
