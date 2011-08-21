/*
 * CluedoEngine
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
package cluedo.engine;

import cluedo.core.GameCard;
import cluedo.core.SolutionTripplet;
import java.util.Set;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CluedoEngine implements InteractionProvider, AnswerResultSink {
    private InteractionHandler interact;
    private boolean initialized;
    private SolutionTripplet proposal;
    private SolutionTripplet accusation;

    public CluedoEngine() {
        this.initialized = false;
        this.interact = new InteractionHandler() {

            @Override
            public void onTurnStart(String player) {
                System.out.printf("Default interaction handler: on player %s turn start!", player);
            }

            @Override
            public void onPlayerTurn(String player, SolutionSink solution) {
                System.out.printf("Default interaction handler: on player %s turn!", player);
            }

            @Override
            public void onPlayerRequestForAnswer(String player, AnswerResultSink answer) {
                System.out.printf("Default interaction handler: on player %s request for answer!", player);
            }

            @Override
            public void onTurnEnd(String player) {
                System.out.printf("Default interaction handler: on player %s turn end!", player);
            }
            
        };
    }

    @Override
    public void setInteractionHandler(InteractionHandler interact) {
        if (interact != null) {
            this.interact = interact;
        }
    }
    
    @Override
    public void init(Set<String> players) {
        initialized = true;
    }
    
    public void run() {
        if (!initialized) {
            throw new RuntimeException("Called run() before init()!");
        }
        
        while () {
            
        }
        
    }

    @Override
    public void cannotProvideAnswer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showHiddenCard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showCard(GameCard card) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
