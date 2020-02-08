/*
 * QuestLoot
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
@Table(name = "quest_loot")
@NamedQueries({
    @NamedQuery(name = "QuestLoot.findAll", query = "SELECT q FROM QuestLoot q"),
    @NamedQuery(name = "QuestLoot.findByQuestId", query = "SELECT q FROM QuestLoot q WHERE q.questLootPK.questId = :questId"),
    @NamedQuery(name = "QuestLoot.findByArmsId", query = "SELECT q FROM QuestLoot q WHERE q.questLootPK.armsId = :armsId"),
    @NamedQuery(name = "QuestLoot.findByProbabilty", query = "SELECT q FROM QuestLoot q WHERE q.probabilty = :probabilty")})
public class QuestLoot implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected QuestLootPK questLootPK;
    @Basic(optional = false)
    @Column(name = "probabilty")
    private float probabilty;
    @JoinColumn(name = "arms_id", referencedColumnName = "arms_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Arms arms;
    @JoinColumn(name = "quest_id", referencedColumnName = "quest_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Quest quest;

    public QuestLoot() {
    }

    public QuestLoot(QuestLootPK questLootPK) {
        this.questLootPK = questLootPK;
    }

    public QuestLoot(QuestLootPK questLootPK, float probabilty) {
        this.questLootPK = questLootPK;
        this.probabilty = probabilty;
    }

    public QuestLoot(int questId, int armsId) {
        this.questLootPK = new QuestLootPK(questId, armsId);
    }

    public QuestLootPK getQuestLootPK() {
        return questLootPK;
    }

    public void setQuestLootPK(QuestLootPK questLootPK) {
        this.questLootPK = questLootPK;
    }

    public float getProbabilty() {
        return probabilty;
    }

    public void setProbabilty(float probabilty) {
        this.probabilty = probabilty;
    }

    public Arms getArms() {
        return arms;
    }

    public void setArms(Arms arms) {
        this.arms = arms;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (questLootPK != null ? questLootPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuestLoot)) {
            return false;
        }
        QuestLoot other = (QuestLoot) object;
        if ((this.questLootPK == null && other.questLootPK != null) || (this.questLootPK != null && !this.questLootPK.equals(other.questLootPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.QuestLoot[questLootPK=" + questLootPK + "]";
    }

}
