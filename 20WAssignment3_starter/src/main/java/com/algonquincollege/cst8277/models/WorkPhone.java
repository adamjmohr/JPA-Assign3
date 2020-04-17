/***************************************************************************f******************u************zz*******y**
 * File: WorkPhone.java
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
 * Model for a work phone.
 */
@Entity
@DiscriminatorValue(value = "W")
public class WorkPhone extends PhonePojo implements Serializable {
    /** explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;
    /** work phone department name */
    protected String department;

    /**
     * JPA requires each @Entity class have a default constructor
     */
    public WorkPhone() {
        super();
    }

    /**
     * @return the value for department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department new value for department
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
}