/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.springboot.ws_wss.model.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author emiliano
 */
@Entity
@Table(name = "DATA_LDAP_EMAIL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DataEmail.findAll", query = "SELECT l FROM DataEmail l")
    , @NamedQuery(name = "DataEmail.fetchBySycnState", query = "SELECT l FROM DataEmail l WHERE l.syncState = :syncState")})
public class DataEmail implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "DATA_LDAP_EMAIL_ID")
    private Integer dataLdapEmailId;
    private String typeDocument;
    private String document;
    private String email;
    private String syncState; //UPD|SYC|ERR
    @Temporal(TemporalType.TIMESTAMP)
    private Date modDate;
    
    
	public Integer getDataLdapEmailId() {
		return dataLdapEmailId;
	}
	public void setDataLdapEmailId(Integer dataLdapEmailId) {
		this.dataLdapEmailId = dataLdapEmailId;
	}
	public String getTypeDocument() {
		return typeDocument;
	}
	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Date getModDate() {
		return modDate;
	}
	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getSyncState() {
		return syncState;
	}
	public void setSyncState(String syncState) {
		this.syncState = syncState;
	}
    
    
    
}
