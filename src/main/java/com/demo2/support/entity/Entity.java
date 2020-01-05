/**
 * 
 */
package com.demo2.support.entity;

import java.io.Serializable;

/**
 * The abstract class for all of entity.
 * @author fangang
 */
public abstract class Entity implements Serializable {

	private static final long serialVersionUID = 2554469201774584779L;
	/**
	 * @return id
	 */
	public abstract Serializable getId();
	/**
	 * @param id
	 */
	public abstract void setId(Serializable id);
}
