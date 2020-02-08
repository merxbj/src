/*
 * QuestLootPK
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
public class QuestLootPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "quest_id")
    private int questId;
    @Basic(optional = false)
    @Column(name = "arms_id")
    private int armsId;

    public QuestLootPK() {
    }

    public QuestLootPK(int questId, int armsId) {
        this.questId = questId;
        this.armsId = armsId;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public int getArmsId() {
        return armsId;
    }

    public void setArmsId(int armsId) {
        this.armsId = armsId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) questId;
        hash += (int) armsId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuestLootPK)) {
            return false;
        }
        QuestLootPK other = (QuestLootPK) object;
        if (this.questId != other.questId) {
            return false;
        }
        if (this.armsId != other.armsId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.QuestLootPK[questId=" + questId + ", armsId=" + armsId + "]";
    }

}
