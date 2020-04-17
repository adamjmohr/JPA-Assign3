/***************************************************************************f******************u************zz*******y**
 * File: EmployeeTask.java
 * Course materials (20W) CST 8277
 * @author Mike Norman
 * @author Adam Mohr 040669681
 * @date 2020 02
 *
 */
package com.algonquincollege.cst8277.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Employee's work tasks assigned.
 */
@Embeddable
public class EmployeeTask {

    /** task description */
    protected String description;
    /** task start date */
    protected LocalDateTime taskStart;
    /** task end date */
    protected LocalDateTime taskEndDate;
    /** is this task done? */
    protected boolean taskDone;

    /**
     * JPA requires each @Entity class have a default constructor
     */
    public EmployeeTask() {
    }

    /**
     * @return the value for description
     */
    @Column(name = "TASK_DESCRIPTION")
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description new value for description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the value for taskStart
     */
    public LocalDateTime getTaskStart() {
        return taskStart;
    }

    /**
     * @param taskStart new value for taskStart
     */
    public void setTaskStart(LocalDateTime taskStart) {
        this.taskStart = taskStart;
    }

    /**
     * @return the value for taskEndDate
     */
    public LocalDateTime getTaskEndDate() {
        return taskEndDate;
    }

    /**
     * @param taskEndDate new value for taskEndDate
     */
    public void setTaskEndDate(LocalDateTime taskEndDate) {
        this.taskEndDate = taskEndDate;
    }

    /**
     * @return the boolean value for taskDone
     */
    public boolean isTaskDone() {
        return taskDone;
    }

    /**
     * @param taskDone new boolean value for taskDone
     */
    public void setTaskDone(boolean taskDone) {
        this.taskDone = taskDone;
    }
    
}