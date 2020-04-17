/***************************************************************************f******************u************zz*******y**
 * File: HomePhone.java
 * Course materials (20W) CST 8277
 * @author Mike Norman
 * @author Adam Mohr 040669681
 * @date 2020 02
 *
 */
package com.algonquincollege.cst8277.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Model for a home phone.
 */
@Entity
@DiscriminatorValue(value = "H")
public class HomePhone extends PhonePojo implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** home phone direction coordinates */
    protected String directions;
    
    /**
     * JPA requires each @Entity class have a default constructor
     */
    public HomePhone() {
        super();
    }

    /**
     * @return the value for directions
     */
    @Column(name = "MAP_COORDS")
    public String getDirections() {
        return directions;
    }

    /**
     * @param directions new value for directions
     */
    public void setDirections(String directions) {
        this.directions = directions;
    }
    
}