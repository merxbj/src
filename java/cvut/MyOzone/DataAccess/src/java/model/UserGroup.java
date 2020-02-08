/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

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
 */
@Entity
@Table(name = "user_group")
@NamedQueries({
    @NamedQuery(name = "UserGroup.findAll", query = "SELECT u FROM UserGroup u"),
    @NamedQuery(name = "UserGroup.findByUserId", query = "SELECT u FROM UserGroup u WHERE u.userGroupPK.userId = :userId"),
    @NamedQuery(name = "UserGroup.findByUsername", query = "SELECT u FROM UserGroup u WHERE u.username = :username"),
    @NamedQuery(name = "UserGroup.findByGroupname", query = "SELECT u FROM UserGroup u WHERE u.userGroupPK.groupname = :groupname")})
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserGroupPK userGroupPK;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public UserGroup() {
    }

    public UserGroup(UserGroupPK userGroupPK) {
        this.userGroupPK = userGroupPK;
    }

    public UserGroup(UserGroupPK userGroupPK, String username) {
        this.userGroupPK = userGroupPK;
        this.username = username;
    }

    public UserGroup(int userId, String groupname) {
        this.userGroupPK = new UserGroupPK(userId, groupname);
    }

    public UserGroupPK getUserGroupPK() {
        return userGroupPK;
    }

    public void setUserGroupPK(UserGroupPK userGroupPK) {
        this.userGroupPK = userGroupPK;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userGroupPK != null ? userGroupPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroup)) {
            return false;
        }
        UserGroup other = (UserGroup) object;
        if ((this.userGroupPK == null && other.userGroupPK != null) || (this.userGroupPK != null && !this.userGroupPK.equals(other.userGroupPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.UserGroup[userGroupPK=" + userGroupPK + "]";
    }

}
