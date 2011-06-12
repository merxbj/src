/*
 * PlayerArms
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
@Table(name = "player_arms")
@NamedQueries({
    @NamedQuery(name = "PlayerArms.findAll", query = "SELECT p FROM PlayerArms p"),
    @NamedQuery(name = "PlayerArms.findByPlayerId", query = "SELECT p FROM PlayerArms p WHERE p.playerArmsPK.playerId = :playerId"),
    @NamedQuery(name = "PlayerArms.findByArmsId", query = "SELECT p FROM PlayerArms p WHERE p.playerArmsPK.armsId = :armsId"),
    @NamedQuery(name = "PlayerArms.findByQuantity", query = "SELECT p FROM PlayerArms p WHERE p.quantity = :quantity")})
public class PlayerArms implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PlayerArmsPK playerArmsPK;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Player player;
    @JoinColumn(name = "arms_id", referencedColumnName = "arms_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Arms arms;

    public PlayerArms() {
    }

    public PlayerArms(PlayerArmsPK playerArmsPK) {
        this.playerArmsPK = playerArmsPK;
    }

    public PlayerArms(PlayerArmsPK playerArmsPK, int quantity) {
        this.playerArmsPK = playerArmsPK;
        this.quantity = quantity;
    }

    public PlayerArms(int playerId, int armsId) {
        this.playerArmsPK = new PlayerArmsPK(playerId, armsId);
    }

    public PlayerArmsPK getPlayerArmsPK() {
        return playerArmsPK;
    }

    public void setPlayerArmsPK(PlayerArmsPK playerArmsPK) {
        this.playerArmsPK = playerArmsPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Arms getArms() {
        return arms;
    }

    public void setArms(Arms arms) {
        this.arms = arms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (playerArmsPK != null ? playerArmsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayerArms)) {
            return false;
        }
        PlayerArms other = (PlayerArms) object;
        if ((this.playerArmsPK == null && other.playerArmsPK != null) || (this.playerArmsPK != null && !this.playerArmsPK.equals(other.playerArmsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.PlayerArms[playerArmsPK=" + playerArmsPK + "]";
    }

}
