package com.ghw.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Utilitary class used in AOP 
 * 
 * @author ifernandez
 * 
 */
public interface IEntityEditable extends Serializable, IEntity {

	// to add in log system detail after the toString
	public String getFieldAdicional();

	// used to add date updated in AOP method, before the entity be updated
	public void setUpdateDate(Date updateDate);

	// used to add user updated in AOP method, before the entity be updated
	public void setUserUpdated(User userUpdated);

	// used to add date created in AOP method, before the entity be updated
	public void setCreatedDate(Date date);

	// used to add user created in AOP method, before the entity be updated
	public void setUserCreated(User user);

	// to compare 2 object and see the difference before update, add the modify
	// field to FieldAdicional
	public int compare(Object oldObj);

}
