/**
 * 
 */
package com.demo2.support.dao.impl.factory;

/**
 * The relation of the value object.
 * @author fangang
 */
public class Join {
	private String name;
	private String joinKey;
	private String joinType;
	private String clazz;
	private boolean isAggregation;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the joinKey
	 */
	public String getJoinKey() {
		return joinKey;
	}
	/**
	 * @param joinKey the joinKey to set
	 */
	public void setJoinKey(String joinKey) {
		this.joinKey = joinKey;
	}
	/**
	 * @return the joinType
	 */
	public String getJoinType() {
		return joinType;
	}
	/**
	 * @param joinType the joinType to set
	 */
	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}
	/**
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	/**
	 * @return the isAggregation
	 */
	public boolean isAggregation() {
		return isAggregation;
	}
	/**
	 * @param isAggregation the isAggregation to set
	 */
	public void setAggregation(boolean isAggregation) {
		this.isAggregation = isAggregation;
	}
}
