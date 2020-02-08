/*
 * PlayerLand
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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author eTeR
 * @version %I% %G%
 */
@Entity
@Table(name = "player_land")
@NamedQueries({
    @NamedQuery(name = "PlayerLand.findAll", query = "SELECT p FROM PlayerLand p"),
    @NamedQuery(name = "PlayerLand.findByPlayerId", query = "SELECT p FROM PlayerLand p WHERE p.playerLandPK.playerId = :playerId"),
    @NamedQuery(name = "PlayerLand.findByLandId", query = "SELECT p FROM PlayerLand p WHERE p.playerLandPK.landId = :landId"),
    @NamedQuery(name = "PlayerLand.findByQuantity", query = "SELECT p FROM PlayerLand p WHERE p.quantity = :quantity")})
public class PlayerLand implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PlayerLandPK playerLandPK;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "land_id", referencedColumnName = "land_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Land land;
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Player player;

    public PlayerLand() {
    }

    public PlayerLand(PlayerLandPK playerLandPK) {
        this.playerLandPK = playerLandPK;
    }

    public PlayerLand(PlayerLandPK playerLandPK, int quantity) {
        this.playerLandPK = playerLandPK;
        this.quantity = quantity;
    }

    public PlayerLand(int playerId, int landId) {
        this.playerLandPK = new PlayerLandPK(playerId, landId);
    }

    public PlayerLandPK getPlayerLandPK() {
        return playerLandPK;
    }

    public void setPlayerLandPK(PlayerLandPK playerLandPK) {
        this.playerLandPK = playerLandPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Land getLand() {
        return land;
    }

    public void setLand(Land land) {
        this.land = land;
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
        hash += (playerLandPK != null ? playerLandPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayerLand)) {
            return false;
        }
        PlayerLand other = (PlayerLand) object;
        if ((this.playerLandPK == null && other.playerLandPK != null) || (this.playerLandPK != null && !this.playerLandPK.equals(other.playerLandPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.PlayerLand[playerLandPK=" + playerLandPK + "]";
    }

}
