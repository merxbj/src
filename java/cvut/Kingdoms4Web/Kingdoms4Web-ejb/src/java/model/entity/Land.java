/*
 * Land
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
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "land")
@NamedQueries({
    @NamedQuery(name = "Land.findAll", query = "SELECT l FROM Land l"),
    @NamedQuery(name = "Land.findByLandId", query = "SELECT l FROM Land l WHERE l.landId = :landId"),
    @NamedQuery(name = "Land.findByName", query = "SELECT l FROM Land l WHERE l.name = :name"),
    @NamedQuery(name = "Land.findByPrice", query = "SELECT l FROM Land l WHERE l.price = :price"),
    @NamedQuery(name = "Land.findByIncome", query = "SELECT l FROM Land l WHERE l.income = :income"),
    @NamedQuery(name = "Land.findByRequiredLevel", query = "SELECT l FROM Land l WHERE l.requiredLevel = :requiredLevel")})
public class Land implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "land_id")
    private Integer landId;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "price")
    private int price;
    @Basic(optional = false)
    @Column(name = "income")
    private int income;
    @Basic(optional = false)
    @Column(name = "required_level")
    private int requiredLevel;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "land")
    private Collection<PlayerLand> playerLandCollection;

    public Land() {
    }

    public Land(Integer landId) {
        this.landId = landId;
    }

    public Land(Integer landId, String name, int price, int income, int requiredLevel) {
        this.landId = landId;
        this.name = name;
        this.price = price;
        this.income = income;
        this.requiredLevel = requiredLevel;
    }

    public Integer getLandId() {
        return landId;
    }

    public void setLandId(Integer landId) {
        this.landId = landId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public Collection<PlayerLand> getPlayerLandCollection() {
        return playerLandCollection;
    }

    public void setPlayerLandCollection(Collection<PlayerLand> playerLandCollection) {
        this.playerLandCollection = playerLandCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (landId != null ? landId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Land)) {
            return false;
        }
        Land other = (Land) object;
        if ((this.landId == null && other.landId != null) || (this.landId != null && !this.landId.equals(other.landId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Land[landId=" + landId + "]";
    }

}
