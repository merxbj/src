/*
 * PlayerLandPK
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
public class PlayerLandPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "player_id")
    private int playerId;
    @Basic(optional = false)
    @Column(name = "land_id")
    private int landId;

    public PlayerLandPK() {
    }

    public PlayerLandPK(int playerId, int landId) {
        this.playerId = playerId;
        this.landId = landId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getLandId() {
        return landId;
    }

    public void setLandId(int landId) {
        this.landId = landId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) playerId;
        hash += (int) landId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayerLandPK)) {
            return false;
        }
        PlayerLandPK other = (PlayerLandPK) object;
        if (this.playerId != other.playerId) {
            return false;
        }
        if (this.landId != other.landId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.PlayerLandPK[playerId=" + playerId + ", landId=" + landId + "]";
    }

}
