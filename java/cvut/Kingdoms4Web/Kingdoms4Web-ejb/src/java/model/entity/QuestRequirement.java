/*
 * QuestRequirement
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
@Table(name = "quest_requirement")
@NamedQueries({
    @NamedQuery(name = "QuestRequirement.findAll", query = "SELECT q FROM QuestRequirement q"),
    @NamedQuery(name = "QuestRequirement.findByQuestId", query = "SELECT q FROM QuestRequirement q WHERE q.questRequirementPK.questId = :questId"),
    @NamedQuery(name = "QuestRequirement.findByArmsId", query = "SELECT q FROM QuestRequirement q WHERE q.questRequirementPK.armsId = :armsId"),
    @NamedQuery(name = "QuestRequirement.findByQuantity", query = "SELECT q FROM QuestRequirement q WHERE q.quantity = :quantity")})
public class QuestRequirement implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected QuestRequirementPK questRequirementPK;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @JoinColumn(name = "arms_id", referencedColumnName = "arms_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Arms arms;
    @JoinColumn(name = "quest_id", referencedColumnName = "quest_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Quest quest;

    public QuestRequirement() {
    }

    public QuestRequirement(QuestRequirementPK questRequirementPK) {
        this.questRequirementPK = questRequirementPK;
    }

    public QuestRequirement(QuestRequirementPK questRequirementPK, int quantity) {
        this.questRequirementPK = questRequirementPK;
        this.quantity = quantity;
    }

    public QuestRequirement(int questId, int armsId) {
        this.questRequirementPK = new QuestRequirementPK(questId, armsId);
    }

    public QuestRequirementPK getQuestRequirementPK() {
        return questRequirementPK;
    }

    public void setQuestRequirementPK(QuestRequirementPK questRequirementPK) {
        this.questRequirementPK = questRequirementPK;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
        hash += (questRequirementPK != null ? questRequirementPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuestRequirement)) {
            return false;
        }
        QuestRequirement other = (QuestRequirement) object;
        if ((this.questRequirementPK == null && other.questRequirementPK != null) || (this.questRequirementPK != null && !this.questRequirementPK.equals(other.questRequirementPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.QuestRequirement[questRequirementPK=" + questRequirementPK + "]";
    }

}
