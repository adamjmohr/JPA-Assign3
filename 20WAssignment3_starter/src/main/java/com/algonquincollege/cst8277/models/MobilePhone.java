/***************************************************************************f******************u************zz*******y**
 * File: MobilePhone.java
 * Course materials (20W) CST 8277
 * @author Mike Norman
 * @author Adam Mohr 040669681
 * @date 2020 02
 *
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Model for a mobile phone.
 */
@Entity
@DiscriminatorValue(value = "M")
public class MobilePhone extends PhonePojo implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** mobile phone provider name */
    protected String provider;

    /**
     * JPA requires each @Entity class have a default constructor
     */
    public MobilePhone() {
        super();
    }

    /**
     * @return the value for provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider new value for provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

}