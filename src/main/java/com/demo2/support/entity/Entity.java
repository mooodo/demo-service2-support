/**
 * 
 */
package com.demo2.support.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.demo2.support.utils.BeanUtils;

/**
 * The abstract class for all of entity.
 * @author fangang
 */
public abstract class Entity<T extends Serializable> implements Serializable, Cloneable {

	private static final long serialVersionUID = 2554469201774584779L;
	/**
	 * @return id
	 */
	public abstract T getId();
	/**
	 * @param id
	 */
	public abstract void setId(T id);
	
	/**
	 * override the method that it is true when both of objects: 
	 * 1) is instance of same class;  
	 * 2) each of properties contain same data, 
	 * instead of them is a same instance. 
	 * All of it's children can use this method to compare each other, 
	 * especially when test. 
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!(this.getClass().equals(obj.getClass()))) return false;
		Field[] fields = this.getClass().getDeclaredFields();
		Field[] targetFields = obj.getClass().getDeclaredFields();
		for(int i=0; i<fields.length; i++) {
			if(isExclude(fields[i].getName())) continue;
			Object source = getValue(this,fields[i]);
			Object target = getValue(obj,targetFields[i]);
			if(source==null&&target!=null) return false;
			if(source==null&&target==null) continue;
			if(!(source.equals(target))) return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		Field[] fields = this.getClass().getDeclaredFields();
		StringBuffer buffer = new StringBuffer("{");
		for(int i=0; i<fields.length; i++) {
			String name = fields[i].getName();
			if("serialVersionUID".equals(name)||"this$0".equals(name)) continue;
			Object value = getValue(this,fields[i]);
			buffer.append(name).append(":").append(value).append(", ");
		}
		return buffer.substring(0, buffer.length()-2)+"}";
	}
	/**
	 * @param obj
	 * @param field
	 * @return the value of the field of the object.
	 */
	private Object getValue(Object obj, Field field) {
		return BeanUtils.getValueByField(obj, field.getName());
	}
	
	private String[] exclude = new String[] {};
	/**
	 * @return the exclude
	 */
	public String[] getExclude() {
		return exclude;
	}
	/**
	 * @param exclude the exclude to set
	 */
	public void setExclude(String[] exclude) {
		List<String> listOfTarget = Arrays.asList(exclude);
		List<String> listOfResource = Arrays.asList(this.exclude);
		List<String> list = new ArrayList<>();
		list.addAll(listOfResource);
		list.addAll(listOfTarget);
		this.exclude = list.toArray(new String[list.size()]);
	}
	
	private boolean isExclude(String field) {
		for(int i=0; i<exclude.length ;i++)
			if(exclude[i].equals(field)||"serialVersionUID".equals(field)) return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entity<T> clone() {
		try {
			return (Entity<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
