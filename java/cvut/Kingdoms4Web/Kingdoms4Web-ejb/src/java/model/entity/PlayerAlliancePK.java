/*
 * PlayerAlliancePK
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

package model.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
@Embeddable
public class PlayerAlliancePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "player_id")
    private int playerId;
    @Basic(optional = false)
    @Column(name = "allied_player_id")
    private int alliedPlayerId;

    public PlayerAlliancePK() {
    }

    public PlayerAlliancePK(int playerId, int alliedPlayerId) {
        this.playerId = playerId;
        this.alliedPlayerId = alliedPlayerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getAlliedPlayerId() {
        return alliedPlayerId;
    }

    public void setAlliedPlayerId(int alliedPlayerId) {
        this.alliedPlayerId = alliedPlayerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) playerId;
        hash += (int) alliedPlayerId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayerAlliancePK)) {
            return false;
        }
        PlayerAlliancePK other = (PlayerAlliancePK) object;
        if (this.playerId != other.playerId) {
            return false;
        }
        if (this.alliedPlayerId != other.alliedPlayerId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.PlayerAlliancePK[playerId=" + playerId + ", alliedPlayerId=" + alliedPlayerId + "]";
    }

}
