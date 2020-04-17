/***************************************************************************f******************u************zz*******y**
 * File: PojoListener.java
 * Course materials (20W) CST 8277
 *
 * @author (original) Mike Norman
 * @author Adam Mohr 040669681
 */
package com.algonquincollege.cst8277.models;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * Listener class used to audit updates in database.
 */
public class PojoListener {
    
    /**
     * @param p set created date and updated date
     */
    @PrePersist
    public void onPersist(PojoBase p) {
        LocalDateTime now = LocalDateTime.now();
        p.setCreatedDate(now);
        p.setUpdatedDate(now);
    }
    
    /**
     * @param p set updated date
     */
    @PreUpdate
    public void onUpdate(PojoBase p) {
        LocalDateTime now = LocalDateTime.now();
        p.setUpdatedDate(now);
    }

}