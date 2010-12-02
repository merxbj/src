/*
 * Game
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

package ss.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Game {
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. YYYY");
    private Date date;
    private GameResult result;
    private int points;
    private Team teamA;
    private Team teamB;
    private Map<Player, Race> raceSelection;

    public Game(Date date, GameResult result, int points, Team teamA, Team teamB) {
        this.date = date;
        this.result = result;
        this.points = points;
        this.teamA = teamA;
        this.teamB = teamB;
        this.raceSelection = new Hashtable<Player, Race>();
    }

    public Date getDate() {
        return date;
    }

    public int getPoints() {
        return points;
    }

    public GameResult getResult() {
        return result;
    }

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public Map<Player, Race> getRaceSelection() {
        return raceSelection;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Team A: ").append(teamA.toString());
        builder.append("\nVS\n").append(teamB).append("\n");
        builder.append("On: ").append(dateFormat.format(date));
        builder.append(", Result: ").append(result);
        builder.append(", Team A awarded with: ").append(points).append(" points");
        return builder.toString();
    }

}
