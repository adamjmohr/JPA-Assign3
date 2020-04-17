/***************************************************************************f******************u************zz*******y**
 * File: PhonePojo.java
 * Course materials (20W) CST 8277
 * @author Mike Norman
 * @author Adam Mohr 040669681
 * (Modified) @date 2020 02
 *
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Original @authors dclarke, mbraeuer
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

/**
 * Base Phone class that subclasses will use.
 */
@MappedSuperclass
@Entity(name = "Phone")
@Table(name = "PHONE")
@AttributeOverride(name = "id", column = @Column(name = "PHONE_ID"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PHONE_TYPE", length = 1)
public abstract class PhonePojo extends PojoBase implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** phone areacode value */
    protected String areaCode;
    /** phone number value */
    protected String phoneNumber;
    /** employee phone owner */
    protected EmployeePojo owningEmployee;

    /**
     * JPA requires each @Entity class have a default constructor
     */
    public PhonePojo() {
        super();
    }

    /**
     * @return the value for areaCode
     */
    @Column(name = "AREACODE")
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * @param areaCode new value for areaCode
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * @return the value for phoneNumber
     */
    @Column(name = "PHONENUMBER")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber new value for phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the value for owningEmployee
     */
    @ManyToOne
    @JoinColumn(name = "OWNING_EMP_ID")
    public EmployeePojo getOwningEmployee() {
        return owningEmployee;
    }

    /**
     * @param owningEmployee new value for owningEmployee
     */
    public void setOwningEmployee(EmployeePojo owningEmployee) {
        this.owningEmployee = owningEmployee;
    }

}