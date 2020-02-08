/*
 * Arms
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
@Table(name = "arms")
@NamedQueries({
    @NamedQuery(name = "Arms.findAll", query = "SELECT a FROM Arms a"),
    @NamedQuery(name = "Arms.findByArmsId", query = "SELECT a FROM Arms a WHERE a.armsId = :armsId"),
    @NamedQuery(name = "Arms.findByType", query = "SELECT a FROM Arms a WHERE a.type = :type"),
    @NamedQuery(name = "Arms.findByName", query = "SELECT a FROM Arms a WHERE a.name = :name"),
    @NamedQuery(name = "Arms.findByRequiredLevel", query = "SELECT a FROM Arms a WHERE a.requiredLevel = :requiredLevel"),
    @NamedQuery(name = "Arms.findByAttack", query = "SELECT a FROM Arms a WHERE a.attack = :attack"),
    @NamedQuery(name = "Arms.findByDefense", query = "SELECT a FROM Arms a WHERE a.defense = :defense"),
    @NamedQuery(name = "Arms.findByPrice", query = "SELECT a FROM Arms a WHERE a.price = :price"),
    @NamedQuery(name = "Arms.findByUpkeep", query = "SELECT a FROM Arms a WHERE a.upkeep = :upkeep"),
    @NamedQuery(name = "Arms.findByLootOnly", query = "SELECT a FROM Arms a WHERE a.lootOnly = :lootOnly")})
public class Arms implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "arms_id")
    private Integer armsId;
    @Basic(optional = false)
    @Column(name = "type")
    private String type;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "required_level")
    private int requiredLevel;
    @Basic(optional = false)
    @Column(name = "attack")
    private int attack;
    @Basic(optional = false)
    @Column(name = "defense")
    private int defense;
    @Basic(optional = false)
    @Column(name = "price")
    private int price;
    @Basic(optional = false)
    @Column(name = "upkeep")
    private int upkeep;
    @Basic(optional = false)
    @Column(name = "loot_only")
    private boolean lootOnly;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "arms")
    private Collection<PlayerArms> playerArmsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "arms")
    private Collection<QuestLoot> questLootCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "arms")
    private Collection<QuestRequirement> questRequirementCollection;

    public Arms() {
    }

    public Arms(Integer armsId) {
        this.armsId = armsId;
    }

    public Arms(Integer armsId, String type, String name, int requiredLevel, int attack, int defense, int price, int upkeep, boolean lootOnly) {
        this.armsId = armsId;
        this.type = type;
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.attack = attack;
        this.defense = defense;
        this.price = price;
        this.upkeep = upkeep;
        this.lootOnly = lootOnly;
    }

    public Integer getArmsId() {
        return armsId;
    }

    public void setArmsId(Integer armsId) {
        this.armsId = armsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public void setUpkeep(int upkeep) {
        this.upkeep = upkeep;
    }

    public boolean getLootOnly() {
        return lootOnly;
    }

    public void setLootOnly(boolean lootOnly) {
        this.lootOnly = lootOnly;
    }

    public Collection<PlayerArms> getPlayerArmsCollection() {
        return playerArmsCollection;
    }

    public void setPlayerArmsCollection(Collection<PlayerArms> playerArmsCollection) {
        this.playerArmsCollection = playerArmsCollection;
    }

    public Collection<QuestLoot> getQuestLootCollection() {
        return questLootCollection;
    }

    public void setQuestLootCollection(Collection<QuestLoot> questLootCollection) {
        this.questLootCollection = questLootCollection;
    }

    public Collection<QuestRequirement> getQuestRequirementCollection() {
        return questRequirementCollection;
    }

    public void setQuestRequirementCollection(Collection<QuestRequirement> questRequirementCollection) {
        this.questRequirementCollection = questRequirementCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (armsId != null ? armsId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Arms)) {
            return false;
        }
        Arms other = (Arms) object;
        if ((this.armsId == null && other.armsId != null) || (this.armsId != null && !this.armsId.equals(other.armsId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Arms[armsId=" + armsId + "]";
    }

}
