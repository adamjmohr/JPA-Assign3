package com.algonquincollege.cst8277.models;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2020-04-01T11:42:42.428-0400")
@StaticMetamodel(ProjectPojo.class)
public class ProjectPojo_ extends PojoBase_ {
	public static volatile SingularAttribute<ProjectPojo, String> description;
	public static volatile SingularAttribute<ProjectPojo, String> name;
	public static volatile ListAttribute<ProjectPojo, EmployeePojo> employees;
}
