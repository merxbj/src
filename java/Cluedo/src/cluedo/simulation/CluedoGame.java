/*
 * CluedoGame
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

import java.util.*;
import org.w3c.dom.*;
import cluedo.core.*;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
public class CluedoGame {
    
    private Set<Player> players;
    private Queue<Turn> turns;

    public CluedoGame() {
        players = new TreeSet<Player>();
        turns = new LinkedList<Turn>();
    }
    
    /**
     * Assuming valid XML game transcription accroding to the XSD
     * @param root
     * @throws CluedoGameParseException 
     */
    public void parse(Element cluedoGameElement) throws CluedoGameParseException {
        NodeList playerElements = ((Element) cluedoGameElement.getElementsByTagName("Players").item(0)).getElementsByTagName("Player");
        for (int p = 0; p < playerElements.getLength(); p++) {
            Element playerElement = (Element) (playerElements.item(p));
            Player player = new Player();
            player.parse(playerElement);
            players.add(player);
        }
        
        NodeList turnElements = ((Element) cluedoGameElement.getElementsByTagName("Turns").item(0)).getElementsByTagName("Turn");
        for (int t = 0; t < turnElements.getLength(); t++) {
            Element turnElement = (Element) (turnElements.item(t));
            Turn turn = new Turn();
            turn.parse(turnElement);
            turns.add(turn);
        }
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> players) {
        this.players = players;
    }

    public Queue<Turn> getTurns() {
        return turns;
    }

    public void setTurns(Queue<Turn> turns) {
        this.turns = turns;
    }
    
    public class Player implements Comparable<Player> {

        private String name;

        public Player() {
            name = "unnamed";
        }

        public void parse(Element playerElement) {
            name = playerElement.getAttribute("name");
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(Player o) {
            return name.compareTo(o.name);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Player other = (Player) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

    }
    
    public class Turn {

        private String player;
        private Solution solution;
        private Set<Answer> answers;

        public Turn() {
            solution = new Solution();
            answers = new LinkedHashSet<Answer>();
            player = "unnamed";
        }
        
        public void parse(Element turnElement) throws CluedoGameParseException {
            player = turnElement.getAttribute("player");
            NodeList solutionNodes = turnElement.getElementsByTagName("Solution");
            if (solutionNodes.getLength() == 1) {
                solution.parse((Element) solutionNodes.item(0));
            }
            
            NodeList answerNodes = turnElement.getElementsByTagName("Answer");
            for (int a = 0; a < answerNodes.getLength(); a++) {
                Answer answer = new Answer();
                answer.parse((Element) answerNodes.item(a));
                answers.add(answer);
            }
        }

        public String getPlayer() {
            return player;
        }

        public Set<Answer> getAnswers() {
            return answers;
        }

        public Solution getSolution() {
            return solution;
        }

    }
    
    public class Solution {
        private SolutionType type;
        private Room room;
        private Weapon weapon;
        private Suspect suspect;

        public Solution() {
            type = null;
            room = null;
            weapon = null;
            suspect = null;
        }
        
        public void parse(Element solutionElement) throws CluedoGameParseException {
            type = SolutionType.parse(solutionElement.getAttribute("type"));
            room = RoomParser.parse(solutionElement.getAttribute("room"));
            weapon = WeaponParser.parse(solutionElement.getAttribute("weapon"));
            suspect = SuspectParser.parse(solutionElement.getAttribute("suspect"));
        }

        public Suspect getSuspect() {
            return suspect;
        }

        public Room getRoom() {
            return room;
        }

        public SolutionType getType() {
            return type;
        }

        public Weapon getWeapon() {
            return weapon;
        }

    }
    
    public enum SolutionType {
        
        PROPOSAL, ACCUSATION;
        
        public static SolutionType parse(String typeAttr) throws CluedoGameParseException {
            if (typeAttr.equals("proposal")) {
                return PROPOSAL;
            } else if (typeAttr.equals("accusation")) {
                return ACCUSATION;
            } else {
                throw new CluedoGameParseException("Unexpected solution type attribute: %s", typeAttr);
            }
        }
    }
    
    public class Answer implements Comparable<Answer> {

        private String player;
        private GameCard card;
        private boolean hidden;

        public Answer() {
            player = "unnamed";
            card = null;
            hidden = false;
        }
        
        public void parse(Element answerElement) throws CluedoGameParseException {
            String roomAttr = answerElement.getAttribute("room");
            String weaponAttr = answerElement.getAttribute("weapon");
            String suspectAttr = answerElement.getAttribute("suspect");
            
            if ((roomAttr != null) && !roomAttr.equals("")) {
                card = new GameCard(RoomParser.parse(roomAttr));
            } else if ((weaponAttr != null) && !weaponAttr.equals("")) {
                card = new GameCard(WeaponParser.parse(weaponAttr));
            } else if ((suspectAttr != null) && !suspectAttr.equals("")) {
                card = new GameCard(SuspectParser.parse(suspectAttr));
            }
            
            player = answerElement.getAttribute("player");
            
            String attrHidden = answerElement.getAttribute("hidden");
            hidden = ((attrHidden != null) && (attrHidden.equals("true")));
        }

        /**
         * This migh look kinda weird but it is based on the definition. Everything
         * must be null except the concrete answer.
         * @param o
         * @return 
         */
        @Override
        public int compareTo(Answer o) {
            if (o == null) {
                return -1;
            } else if (card.equals(o.getCard()) && (player == null ? o.player == null : player.equals(o.player))) {
                return 0;
            } else {
                return -1;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Answer other = (Answer) obj;
            if (this.card != other.card) {
                return false;
            }
            if (this.player == null ? other.player != null : !this.player.equals(other.player)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 29 * hash + (this.card != null ? this.card.hashCode() : 0);
            hash = 29 * hash + (this.player != null ? this.player.hashCode() : 0);
            return hash;
        }

        public GameCard getCard() {
            return card;
        }

        public String getPlayer() {
            return player;
        }

        public boolean isHidden() {
            return hidden;
        }

    }
    
    private static class RoomParser {

        public static Room parse(String roomAttr) throws CluedoGameParseException {
            if (roomAttr.equals("Hall")) {
                return Room.HALL;
            } else if (roomAttr.equals("Lounge")) {
                return Room.LOUNGE;
            } else if (roomAttr.equals("Dining Room")) {
                return Room.DINING_ROOM;
            } else if (roomAttr.equals("Kitchen")) {
                return Room.KITCHEN;
            } else if (roomAttr.equals("Ball Room")) {
                return Room.BALL_ROOM;
            } else if (roomAttr.equals("Greenhouse")) {
                return Room.GREENHOUSE;
            } else if (roomAttr.equals("Game Room")) {
                return Room.GAME_ROOM;
            } else if (roomAttr.equals("Library")) {
                return Room.LIBRARY;
            } else if (roomAttr.equals("Study")) {
                return Room.STUDY;
            } else {
                throw new CluedoGameParseException("Unexpected room attribute: %s", roomAttr);
            }
        }

    }
    
    private static class WeaponParser {
        
        public static Weapon parse(String weaponAttr) throws CluedoGameParseException {
            if (weaponAttr.equals("Dagger")) {
                return Weapon.DAGGER;
            } else if (weaponAttr.equals("Candlestick")) {
                return Weapon.CANDLESTICK;
            } else if (weaponAttr.equals("Revolver")) {
                return Weapon.REVOLVER;
            } else if (weaponAttr.equals("Rope")) {
                return Weapon.ROPE;
            } else if (weaponAttr.equals("Crank")) {
                return Weapon.CRANK;
            } else if (weaponAttr.equals("Wrench")) {
                return Weapon.WRENCH;
            } else {
                throw new CluedoGameParseException("Unexpected weapon attribute: %s", weaponAttr);
            }
        }

    }
    
    private static class SuspectParser {
        
        public static Suspect parse(String suspectAttr) throws CluedoGameParseException {
            if (suspectAttr.equals("Mustard")) {
                return Suspect.MUSTARD;
            } else if (suspectAttr.equals("Plum")) {
                return Suspect.PLUM;
            } else if (suspectAttr.equals("Green")) {
                return Suspect.GREEN;
            } else if (suspectAttr.equals("Peacock")) {
                return Suspect.PEACOCK;
            } else if (suspectAttr.equals("Scarlett")) {
                return Suspect.SCARLETT;
            } else if (suspectAttr.equals("White")) {
                return Suspect.WHITE;
            } else {
                throw new CluedoGameParseException("Unexpected character attribute: %s", suspectAttr);
            }
        }
        
    }

}
