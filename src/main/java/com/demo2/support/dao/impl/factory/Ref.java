/**
 * 
 */
package com.demo2.support.dao.impl.factory;

/**
 * The reference of the value object.
 * @author fangang
 */
public class Ref {
	private String name;
	private String refKey;
	private String refType;
	private String bean;
	private String method;
	private String listMethod;
	/**
	 * @return the listMethod
	 */
	public String getListMethod() {
		return listMethod;
	}
	/**
	 * @param listMethod the listMethod to set
	 */
	public void setListMethod(String listMethod) {
		this.listMethod = listMethod;
	}
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
	 * @return the refKey
	 */
	public String getRefKey() {
		return refKey;
	}
	/**
	 * @param refKey the refKey to set
	 */
	public void setRefKey(String refKey) {
		this.refKey = refKey;
	}
	/**
	 * @return the refType
	 */
	public String getRefType() {
		return refType;
	}
	/**
	 * @param refType the refType to set
	 */
	public void setRefType(String refType) {
		this.refType = refType;
	}
	/**
	 * @return the bean
	 */
	public String getBean() {
		return bean;
	}
	/**
	 * @param bean the bean to set
	 */
	public void setBean(String bean) {
		this.bean = bean;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
}
