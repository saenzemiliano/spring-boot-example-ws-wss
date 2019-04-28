/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.springboot.ws_wss.model.db;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author esaenz
 */
@Entity
@Table(name = "GROUP_AUTHORITIES")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GroupAuthorities.findAll", query = "SELECT g FROM GroupAuthorities g")
    , @NamedQuery(name = "GroupAuthorities.findByGroupId", query = "SELECT g FROM GroupAuthorities g WHERE g.groupAuthoritiesPK.groupId = :groupId")
    , @NamedQuery(name = "GroupAuthorities.findByAuthority", query = "SELECT g FROM GroupAuthorities g WHERE g.groupAuthoritiesPK.authority = :authority")})
public class GroupAuthorities implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GroupAuthoritiesPK groupAuthoritiesPK;

    public GroupAuthorities() {
    }

    public GroupAuthorities(GroupAuthoritiesPK groupAuthoritiesPK) {
        this.groupAuthoritiesPK = groupAuthoritiesPK;
    }

    public GroupAuthorities(int groupId, String authority) {
        this.groupAuthoritiesPK = new GroupAuthoritiesPK(groupId, authority);
    }

    public GroupAuthoritiesPK getGroupAuthoritiesPK() {
        return groupAuthoritiesPK;
    }

    public void setGroupAuthoritiesPK(GroupAuthoritiesPK groupAuthoritiesPK) {
        this.groupAuthoritiesPK = groupAuthoritiesPK;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupAuthoritiesPK != null ? groupAuthoritiesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GroupAuthorities)) {
            return false;
        }
        GroupAuthorities other = (GroupAuthorities) object;
        if ((this.groupAuthoritiesPK == null && other.groupAuthoritiesPK != null) || (this.groupAuthoritiesPK != null && !this.groupAuthoritiesPK.equals(other.groupAuthoritiesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.example.springboot.demospringboot.model.GroupAuthorities[ groupAuthoritiesPK=" + groupAuthoritiesPK + " ]";
    }
    
}
