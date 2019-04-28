/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.springboot.ws_wss.model.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author esaenz
 */
@Embeddable
public class GroupAuthoritiesPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "GROUP_ID")
    private int groupId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "AUTHORITY")
    private String authority;

    public GroupAuthoritiesPK() {
    }

    public GroupAuthoritiesPK(int groupId, String authority) {
        this.groupId = groupId;
        this.authority = authority;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) groupId;
        hash += (authority != null ? authority.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupAuthoritiesPK)) {
            return false;
        }
        GroupAuthoritiesPK other = (GroupAuthoritiesPK) object;
        if (this.groupId != other.groupId) {
            return false;
        }
        if ((this.authority == null && other.authority != null) || (this.authority != null && !this.authority.equals(other.authority))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.example.springboot.demospringboot.model.GroupAuthoritiesPK[ groupId=" + groupId + ", authority=" + authority + " ]";
    }
    
}
