package com.algonquincollege.cst8277.models;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-03-31T19:50:07.107-0400")
@StaticMetamodel(EmployeeTask.class)
public class EmployeeTask_ {
	public static volatile SingularAttribute<EmployeeTask, String> description;
	public static volatile SingularAttribute<EmployeeTask, LocalDateTime> taskStart;
	public static volatile SingularAttribute<EmployeeTask, LocalDateTime> taskEndDate;
	public static volatile SingularAttribute<EmployeeTask, Boolean> taskDone;
}
