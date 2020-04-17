/***************************************************************************f******************u************zz*******y**
 * File: Assignment3Driver.java
 * Course materials (20W) CST 8277
 * 
 * @author (original) Mike Norman
 * @author Adam Mohr 040669681 
 */
package com.algonquincollege.cst8277;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Driver class for program.
 */
public class Assignment3Driver {

    /** persistence name */
    public static final String ASSIGNMENT3_PU_NAME = "assignment3-employeeSystem-PU";

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(ASSIGNMENT3_PU_NAME);
        emf.close();
    }
}