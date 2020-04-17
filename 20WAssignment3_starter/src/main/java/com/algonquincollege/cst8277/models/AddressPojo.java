/***************************************************************************f******************u************zz*******y**
 * File: AddressPojo.java
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
 *
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Simple Address class
 */
@Entity(name = "Address")
@Table(name = "ADDRESS")
@Access(AccessType.PROPERTY)
@AttributeOverride(name = "id", column = @Column(name = "ADDR_ID"))
public class AddressPojo extends PojoBase implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** city name */
    protected String city;
    /** country name */
    protected String country;
    /** postal code */
    protected String postal;
    /** state code */
    protected String state;
    /** street name */
    protected String street;

    /**
     * JPA requires each @Entity class have a default constructor
     */
    public AddressPojo() {
        super();
    }

    /**
     * @return city name
     */
    public String getCity() {
        return city;
    }
    
    /**
     * @param city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return postal name
     */
    public String getPostal() {
        return postal;
    }

    /**
     * @param postal code
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }

    /**
     * @return state code
     */
    public String getState() {
        return state;
    }

    /**
     * @param state code
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return street name
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street name
     */
    public void setStreet(String street) {
        this.street = street;
    }

}