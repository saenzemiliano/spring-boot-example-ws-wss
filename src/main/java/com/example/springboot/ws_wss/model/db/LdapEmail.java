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
    @NamedQuery(name = "LdapEmail.findAll", query = "SELECT l FROM LdapEmail l")
    , @NamedQuery(name = "LdapEmail.fetchBySycnState", query = "SELECT l FROM LdapEmail l WHERE l.sycnState = :sycnState")})
public class LdapEmail implements Serializable {


    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "CUSTOMER_ID")
    private Integer customerId;
    private String fullName;
    private String typeDocument;
    private String document;
    private String businnessDeparment;
    private String telephone;
    private String email;
    private String emailSecunadary;
    private String otherDescription;
    private String sycnState; //UPD|SYC|ERR
    @Temporal(TemporalType.TIMESTAMP)
    private Date modDate;


    
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getBusinnessDeparment() {
        return businnessDeparment;
    }

    public void setBusinnessDeparment(String businnessDeparment) {
        this.businnessDeparment = businnessDeparment;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOtherDescription() {
        return otherDescription;
    }

    public void setOtherDescription(String otherDescription) {
        this.otherDescription = otherDescription;
    }
    
    
    public String getSycnState() {
        return sycnState;
    }

    public void setSycnState(String sycnState) {
        this.sycnState = sycnState;
    }
    
    public Date getModDate() {
        return modDate;
    }

    public void setModDate(Date modDate) {
        this.modDate = modDate;
    }
    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailSecunadary() {
        return emailSecunadary;
    }

    public void setEmailSecunadary(String emailSecunadary) {
        this.emailSecunadary = emailSecunadary;
    }
}
