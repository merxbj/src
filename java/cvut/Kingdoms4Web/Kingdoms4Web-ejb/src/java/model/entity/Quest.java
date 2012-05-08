/*
 * Quest
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
@Table(name = "quest")
@NamedQueries({
    @NamedQuery(name = "Quest.findAll", query = "SELECT q FROM Quest q"),
    @NamedQuery(name = "Quest.findByQuestId", query = "SELECT q FROM Quest q WHERE q.questId = :questId"),
    @NamedQuery(name = "Quest.findByLocation", query = "SELECT q FROM Quest q WHERE q.location = :location"),
    @NamedQuery(name = "Quest.findByName", query = "SELECT q FROM Quest q WHERE q.name = :name"),
    @NamedQuery(name = "Quest.findByManaCost", query = "SELECT q FROM Quest q WHERE q.manaCost = :manaCost"),
    @NamedQuery(name = "Quest.findByMinIncome", query = "SELECT q FROM Quest q WHERE q.minIncome = :minIncome"),
    @NamedQuery(name = "Quest.findByMaxIncome", query = "SELECT q FROM Quest q WHERE q.maxIncome = :maxIncome"),
    @NamedQuery(name = "Quest.findByExperienceIncome", query = "SELECT q FROM Quest q WHERE q.experienceIncome = :experienceIncome"),
    @NamedQuery(name = "Quest.findByRequiredLevel", query = "SELECT q FROM Quest q WHERE q.requiredLevel = :requiredLevel")})
public class Quest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "quest_id")
    private Integer questId;
    @Basic(optional = false)
    @Column(name = "location")
    private String location;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "mana_cost")
    private int manaCost;
    @Basic(optional = false)
    @Column(name = "min_income")
    private int minIncome;
    @Basic(optional = false)
    @Column(name = "max_income")
    private int maxIncome;
    @Basic(optional = false)
    @Column(name = "experience_income")
    private int experienceIncome;
    @Basic(optional = false)
    @Column(name = "required_level")
    private int requiredLevel;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quest")
    private Collection<QuestLoot> questLootCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "quest")
    private Collection<QuestRequirement> questRequirementCollection;

    public Quest() {
    }

    public Quest(Integer questId) {
        this.questId = questId;
    }

    public Quest(Integer questId, String location, String name, int manaCost, int minIncome, int maxIncome, int experienceIncome, int requiredLevel) {
        this.questId = questId;
        this.location = location;
        this.name = name;
        this.manaCost = manaCost;
        this.minIncome = minIncome;
        this.maxIncome = maxIncome;
        this.experienceIncome = experienceIncome;
        this.requiredLevel = requiredLevel;
    }

    public Integer getQuestId() {
        return questId;
    }

    public void setQuestId(Integer questId) {
        this.questId = questId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getManaCost() {
        return manaCost;
    }

    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public int getMinIncome() {
        return minIncome;
    }

    public void setMinIncome(int minIncome) {
        this.minIncome = minIncome;
    }

    public int getMaxIncome() {
        return maxIncome;
    }

    public void setMaxIncome(int maxIncome) {
        this.maxIncome = maxIncome;
    }

    public int getExperienceIncome() {
        return experienceIncome;
    }

    public void setExperienceIncome(int experienceIncome) {
        this.experienceIncome = experienceIncome;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
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
        hash += (questId != null ? questId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Quest)) {
            return false;
        }
        Quest other = (Quest) object;
        if ((this.questId == null && other.questId != null) || (this.questId != null && !this.questId.equals(other.questId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Quest[questId=" + questId + "]";
    }

}
