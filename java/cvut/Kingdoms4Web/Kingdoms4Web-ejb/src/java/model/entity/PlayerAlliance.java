/*
 * PlayerAlliance
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
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
@Entity
@Table(name = "player_alliance")
@NamedQueries({
    @NamedQuery(name = "PlayerAlliance.findAll", query = "SELECT p FROM PlayerAlliance p"),
    @NamedQuery(name = "PlayerAlliance.findByPlayerId", query = "SELECT p FROM PlayerAlliance p WHERE p.playerAlliancePK.playerId = :playerId"),
    @NamedQuery(name = "PlayerAlliance.findByAlliedPlayerId", query = "SELECT p FROM PlayerAlliance p WHERE p.playerAlliancePK.alliedPlayerId = :alliedPlayerId")})
public class PlayerAlliance implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PlayerAlliancePK playerAlliancePK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "playerAlliance")
    private Collection<PlayerAlliance> playerAllianceCollection;
    @JoinColumn(name = "allied_player_id", referencedColumnName = "player_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private PlayerAlliance playerAlliance;
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Player player;

    public PlayerAlliance() {
    }

    public PlayerAlliance(PlayerAlliancePK playerAlliancePK) {
        this.playerAlliancePK = playerAlliancePK;
    }

    public PlayerAlliance(int playerId, int alliedPlayerId) {
        this.playerAlliancePK = new PlayerAlliancePK(playerId, alliedPlayerId);
    }

    public PlayerAlliancePK getPlayerAlliancePK() {
        return playerAlliancePK;
    }

    public void setPlayerAlliancePK(PlayerAlliancePK playerAlliancePK) {
        this.playerAlliancePK = playerAlliancePK;
    }

    public Collection<PlayerAlliance> getPlayerAllianceCollection() {
        return playerAllianceCollection;
    }

    public void setPlayerAllianceCollection(Collection<PlayerAlliance> playerAllianceCollection) {
        this.playerAllianceCollection = playerAllianceCollection;
    }

    public PlayerAlliance getPlayerAlliance() {
        return playerAlliance;
    }

    public void setPlayerAlliance(PlayerAlliance playerAlliance) {
        this.playerAlliance = playerAlliance;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (playerAlliancePK != null ? playerAlliancePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayerAlliance)) {
            return false;
        }
        PlayerAlliance other = (PlayerAlliance) object;
        if ((this.playerAlliancePK == null && other.playerAlliancePK != null) || (this.playerAlliancePK != null && !this.playerAlliancePK.equals(other.playerAlliancePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.PlayerAlliance[playerAlliancePK=" + playerAlliancePK + "]";
    }

}
