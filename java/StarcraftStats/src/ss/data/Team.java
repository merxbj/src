/*
 * Team
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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Jaroslav Merxbauer
 * @version %I% %G%
 */
public class Team implements Comparable<Team> {
    private String id;
    private TeamType type;
    private boolean isRandom;
    private LeagueName league;
    private Set<Player> members;

    public Team(String id, TeamType type, boolean isRandom, LeagueName league) {
        this.id = id;
        this.type = type;
        this.isRandom = isRandom;
        this.league = league;
        this.members = new LinkedHashSet<Player>();
    }

    public String getId() {
        return id;
    }

    public boolean isIsRandom() {
        return isRandom;
    }

    public LeagueName getLeague() {
        return league;
    }

    public TeamType getType() {
        return type;
    }

    public Set<Player> getMembers() {
        return members;
    }

    public int compareTo(Team o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Team other = (Team) obj;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(type.toString()).append(" ");
        builder.append(isRandom ? "Random" : "Team").append(" ");
        builder.append(league).append("Members: {");
        for (Player p : members) {
            builder.append(p.getName()).append(",");
        }
        return builder.replace(builder.length() - 1, builder.length(), "}").toString();
    }

}
