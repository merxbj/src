/*
 * CluedoGameInterpreter
 *
 * Copyright (C) 2010  Jaroslav Merxbauer
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package cluedo.simulation;

import cluedo.engine.*;
import cluedo.simulation.CluedoGame.Answer;
import cluedo.simulation.CluedoGame.Player;
import cluedo.simulation.CluedoGame.Solution;
import cluedo.simulation.CluedoGame.Turn;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CluedoGameInterpreter implements InteractionHandler {
    
    private CluedoGame game;
    private InteractionProvider interact;
    private Turn currentTurn;

    public CluedoGameInterpreter(CluedoGame game, InteractionProvider interact) {
        this.game = game;
        this.interact = interact;
    }

    @Override
    public void onTurnStart(String player) {
        currentTurn = game.getTurns().poll();
    }

    @Override
    public void onPlayerRequestForAnswer(String player, AnswerResultSink answerSink) {
        for (Answer answer : currentTurn.getAnswers()) {
            if (answer.getPlayer().equals(player)) {
                if (answer.getRoom() != null) {
                    answerSink.showRoom(answer.getRoom());
                } else if (answer.getSuspect() != null) {
                    answerSink.showSuspect(answer.getSuspect());
                } else if (answer.getWeapon() != null) {
                    answerSink.showWeapon(answer.getWeapon());
                }
            }
        }
    }

    @Override
    public void onPlayerTurn(String player, SolutionSink solutionSink) {
        if (currentTurn.getPlayer().equals(player)) {
            Solution solution = currentTurn.getSolution();
            if (solution != null) {
                if (solution.getType() == CluedoGame.SolutionType.PROPOSAL) {
                    solutionSink.ProposeSolution(solution.getRoom(), solution.getSuspect(), solution.getWeapon());
                } else {
                    solutionSink.MakeAccusation(solution.getRoom(), solution.getSuspect(), solution.getWeapon());
                }
            }
        }
    }

    @Override
    public void onTurnEnd(String player) {
    }
    
    public void init() {
        interact.setInteractionHandler(this);
        interact.init(getPlayerNames());
    }
    
    private Set<String> getPlayerNames() {
        Set<String> names = new TreeSet<String>();
        for (Player player : game.getPlayers()) {
            names.add(player.getName());
        }
        return names;
    }
}
