package com.ghw.model;

import java.io.Serializable;

/**
 * Utilitary class used in Generic classes
 * 
 * @author ifernandez
 * 
 */
public interface IEntity extends Serializable {

	// to get the entity ID
	public String getIdentity();

}
