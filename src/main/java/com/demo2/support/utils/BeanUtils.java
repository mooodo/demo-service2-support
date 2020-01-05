/*
 * created by 2019年7月23日 上午10:11:43
 */
package com.demo2.support.utils;

import java.io.Serializable;
import java.lang.reflect.Field;

import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;

/**
 * The utility for the bean.
 * @author fangang
 */
public class BeanUtils {
	
	/**
	 * create an entity by class name.
	 * @param className
	 * @return the entity
	 */
	public static Entity createEntity(String className) {
		try {
			Class<? extends Entity> clazz = Class.forName(className).asSubclass(Entity.class);
			return clazz.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new OrmException("error when instance the entity["+className+"]", e);
		}
	}
	
	public static Entity createEntity(Class<? extends Entity> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OrmException("error when instance the entity", e);
		}
	}
	
	/**
	 * get the value from a bean by field name.
	 * @param bean
	 * @param fieldName
	 * @return the value
	 */
	public static Object getValueByField(Object bean, String fieldName) {
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			boolean isAccessible = field.isAccessible();
			if(!isAccessible) field.setAccessible(true);
			Object value = field.get(bean);
			field.setAccessible(isAccessible);
			return (Serializable) value;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new OrmException("error when get value from the bean", e);
		}
	}
	
	/**
	 * set the value to the bean by the field name.
	 * @param bean
	 * @param fieldName
	 * @param value
	 */
	public static void setValueByField(Object bean, String fieldName, Object value) {
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			boolean isAccessible = field.isAccessible();
			if(!isAccessible) field.setAccessible(true);
			field.set(bean, value);
			field.setAccessible(isAccessible);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new OrmException("error when set the value to the bean", e);
		}
	}
	
	/**
	 * set the value to the bean by the field name.
	 * @param bean
	 * @param fieldName
	 * @param value
	 */
	public static void setValueByField(Object bean, String fieldName, BeanCallback callback) {
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			Class<?> clazz = field.getType();
			Object value = callback.getValue(clazz);
			
			setValueByField(bean, fieldName, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			throw new OrmException("error when set the value to the bean", e);
		}
	}
	
	public interface BeanCallback {
		public Object getValue(Class<?> clazz);
	}
}
