/*
 * Player
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
@Table(name = "player")
@NamedQueries({
    @NamedQuery(name = "Player.findAll", query = "SELECT p FROM Player p"),
    @NamedQuery(name = "Player.findByPlayerId", query = "SELECT p FROM Player p WHERE p.playerId = :playerId"),
    @NamedQuery(name = "Player.findByNick", query = "SELECT p FROM Player p WHERE p.nick = :nick"),
    @NamedQuery(name = "Player.findByArmyCode", query = "SELECT p FROM Player p WHERE p.armyCode = :armyCode"),
    @NamedQuery(name = "Player.findByLevel", query = "SELECT p FROM Player p WHERE p.level = :level"),
    @NamedQuery(name = "Player.findBySkillAttack", query = "SELECT p FROM Player p WHERE p.skillAttack = :skillAttack"),
    @NamedQuery(name = "Player.findBySkillDefense", query = "SELECT p FROM Player p WHERE p.skillDefense = :skillDefense"),
    @NamedQuery(name = "Player.findByMaxMana", query = "SELECT p FROM Player p WHERE p.maxMana = :maxMana"),
    @NamedQuery(name = "Player.findByMaxHealth", query = "SELECT p FROM Player p WHERE p.maxHealth = :maxHealth"),
    @NamedQuery(name = "Player.findByMaxSpirit", query = "SELECT p FROM Player p WHERE p.maxSpirit = :maxSpirit"),
    @NamedQuery(name = "Player.findByCurrentMoney", query = "SELECT p FROM Player p WHERE p.currentMoney = :currentMoney"),
    @NamedQuery(name = "Player.findByCurrentMoneyDeposited", query = "SELECT p FROM Player p WHERE p.currentMoneyDeposited = :currentMoneyDeposited"),
    @NamedQuery(name = "Player.findByCurrentHealth", query = "SELECT p FROM Player p WHERE p.currentHealth = :currentHealth"),
    @NamedQuery(name = "Player.findByCurrentMana", query = "SELECT p FROM Player p WHERE p.currentMana = :currentMana"),
    @NamedQuery(name = "Player.findByCurrentSpirit", query = "SELECT p FROM Player p WHERE p.currentSpirit = :currentSpirit"),
    @NamedQuery(name = "Player.findByExperience", query = "SELECT p FROM Player p WHERE p.experience = :experience"),
    @NamedQuery(name = "Player.findBySimlePassword", query = "SELECT p FROM Player p WHERE p.simlePassword = :simlePassword")})
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "player_id")
    private Integer playerId;
    @Basic(optional = false)
    @Column(name = "nick")
    private String nick;
    @Basic(optional = false)
    @Column(name = "army_code")
    private String armyCode;
    @Basic(optional = false)
    @Column(name = "level")
    private short level;
    @Basic(optional = false)
    @Column(name = "skill_attack")
    private short skillAttack;
    @Basic(optional = false)
    @Column(name = "skill_defense")
    private short skillDefense;
    @Basic(optional = false)
    @Column(name = "max_mana")
    private short maxMana;
    @Basic(optional = false)
    @Column(name = "max_health")
    private short maxHealth;
    @Basic(optional = false)
    @Column(name = "max_spirit")
    private short maxSpirit;
    @Basic(optional = false)
    @Column(name = "current_money")
    private long currentMoney;
    @Basic(optional = false)
    @Column(name = "current_money_deposited")
    private long currentMoneyDeposited;
    @Basic(optional = false)
    @Column(name = "current_health")
    private short currentHealth;
    @Basic(optional = false)
    @Column(name = "current_mana")
    private short currentMana;
    @Basic(optional = false)
    @Column(name = "current_spirit")
    private short currentSpirit;
    @Basic(optional = false)
    @Column(name = "experience")
    private int experience;
    @Basic(optional = false)
    @Column(name = "simle_password")
    private String simlePassword;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private Collection<PlayerAlliance> playerAllianceCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private Collection<PlayerArms> playerArmsCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    private Collection<PlayerLand> playerLandCollection;

    public Player() {
    }

    public Player(Integer playerId) {
        this.playerId = playerId;
    }

    public Player(Integer playerId, String nick, String armyCode, short level, short skillAttack, short skillDefense, short maxMana, short maxHealth, short maxSpirit, long currentMoney, long currentMoneyDeposited, short currentHealth, short currentMana, short currentSpirit, int experience, String simlePassword) {
        this.playerId = playerId;
        this.nick = nick;
        this.armyCode = armyCode;
        this.level = level;
        this.skillAttack = skillAttack;
        this.skillDefense = skillDefense;
        this.maxMana = maxMana;
        this.maxHealth = maxHealth;
        this.maxSpirit = maxSpirit;
        this.currentMoney = currentMoney;
        this.currentMoneyDeposited = currentMoneyDeposited;
        this.currentHealth = currentHealth;
        this.currentMana = currentMana;
        this.currentSpirit = currentSpirit;
        this.experience = experience;
        this.simlePassword = simlePassword;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getArmyCode() {
        return armyCode;
    }

    public void setArmyCode(String armyCode) {
        this.armyCode = armyCode;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getSkillAttack() {
        return skillAttack;
    }

    public void setSkillAttack(short skillAttack) {
        this.skillAttack = skillAttack;
    }

    public short getSkillDefense() {
        return skillDefense;
    }

    public void setSkillDefense(short skillDefense) {
        this.skillDefense = skillDefense;
    }

    public short getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(short maxMana) {
        this.maxMana = maxMana;
    }

    public short getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(short maxHealth) {
        this.maxHealth = maxHealth;
    }

    public short getMaxSpirit() {
        return maxSpirit;
    }

    public void setMaxSpirit(short maxSpirit) {
        this.maxSpirit = maxSpirit;
    }

    public long getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(long currentMoney) {
        this.currentMoney = currentMoney;
    }

    public long getCurrentMoneyDeposited() {
        return currentMoneyDeposited;
    }

    public void setCurrentMoneyDeposited(long currentMoneyDeposited) {
        this.currentMoneyDeposited = currentMoneyDeposited;
    }

    public short getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(short currentHealth) {
        this.currentHealth = currentHealth;
    }

    public short getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(short currentMana) {
        this.currentMana = currentMana;
    }

    public short getCurrentSpirit() {
        return currentSpirit;
    }

    public void setCurrentSpirit(short currentSpirit) {
        this.currentSpirit = currentSpirit;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getSimlePassword() {
        return simlePassword;
    }

    public void setSimlePassword(String simlePassword) {
        this.simlePassword = simlePassword;
    }

    public Collection<PlayerAlliance> getPlayerAllianceCollection() {
        return playerAllianceCollection;
    }

    public void setPlayerAllianceCollection(Collection<PlayerAlliance> playerAllianceCollection) {
        this.playerAllianceCollection = playerAllianceCollection;
    }

    public Collection<PlayerArms> getPlayerArmsCollection() {
        return playerArmsCollection;
    }

    public void setPlayerArmsCollection(Collection<PlayerArms> playerArmsCollection) {
        this.playerArmsCollection = playerArmsCollection;
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
        hash += (playerId != null ? playerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Player)) {
            return false;
        }
        Player other = (Player) object;
        if ((this.playerId == null && other.playerId != null) || (this.playerId != null && !this.playerId.equals(other.playerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.entity.Player[playerId=" + playerId + "]";
    }

}
