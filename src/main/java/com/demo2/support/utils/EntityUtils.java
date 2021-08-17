/*
 * Created by 2021-01-04 11:13:31 
 */
package com.demo2.support.utils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;

/**
 * @author fangang
 */
public class EntityUtils {
	/**
	 * create an entity by class name.
	 * @param className
	 * @return the entity
	 */
	public static <S extends Serializable> Entity<S> createEntity(String className) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Entity<S>> clazz = (Class<? extends Entity<S>>) Class.forName(className).asSubclass(Entity.class);
			Entity<S> entity = createEntity(clazz);
			return entity;
		} catch (ClassNotFoundException e) {
			throw new OrmException("error because the entity["+className+"] must exits and extends the class [Entity]", e);
		}
	}
	
	/**
	 * create an entity by class name.
	 * @param className
	 * @return the entity
	 */
	public static <S extends Serializable> Entity<S> createEntity(String className, S id) {
		Entity<S> entity = createEntity(className);
		entity.setId(id);
		return entity;
	}
	
	/**
	 * create an entity by class
	 * @param clazz
	 * @return the entity
	 */
	public static <T extends Entity<S>, S extends Serializable> T createEntity(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OrmException("error when instance the entity["+clazz.getName()+"]", e);
		}
	}
	/**
	 * @param entity
	 * @return
	 */
	public static <T extends Entity<S>, S extends Serializable> T cloneEntity(T entity) {
		@SuppressWarnings("unchecked")
		T clone = (T)entity.clone();
		Field[] fields = entity.getClass().getDeclaredFields();
		for(int i=0; i<fields.length; i++) {
			Object value = BeanUtils.getValueByField(entity, fields[i].getName());
			BeanUtils.setValueByField(clone, fields[i].getName(), value);
		}
		return clone;
	}
	/**
	 * @param clazz
	 * @return whether the clazz is an entity
	 */
	public static boolean isEntity(Class<?> clazz) {
		if(Entity.class.isAssignableFrom(clazz)) return true;
		return false;
	}
	/**
	 * create an entity and set values in it.
	 * @param clazz the class of the entity
	 * @param json the map of values
	 * @return the entity with values
	 */
	public static <T extends Entity<S>, S extends Serializable> T createEntity(Class<T> clazz, Map<String, Object> json) {
		if(clazz==null) throw new OrmException("please give the class of the entity");
		T entity = createEntity(clazz);
		if(json!=null&&!json.isEmpty())
			for(String fieldName : json.keySet()) 
				setValueToEntity(entity, fieldName, json.get(fieldName));
		return entity;
	}
	/**
	 * set the value to the field of the entity
	 * @param entity
	 * @param fieldName
	 */
	public static <S extends Serializable> void setValueToEntity(Entity<S> entity, String fieldName, Object value) {
		String firstStr = fieldName.substring(0,1);
		String setMethodName = "set"+firstStr.toUpperCase()+fieldName.substring(1);
		Method method = BeanUtils.getMethodIfExists(entity, setMethodName);
		if(method==null) return;
		Type[] allOfParameterTypes = method.getGenericParameterTypes();
		Type firstOfParameterType = allOfParameterTypes[0];
		Object obj = BeanUtils.bind(firstOfParameterType, value);
		try {
			method.invoke(entity, obj);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//throw new OrmException("error when invoke the method of an entity: "+setMethodName, e);
		} 
	}
	/**
	 * get the value of the field of the entity
	 * @param entity
	 * @param fieldName
	 * @return the value
	 */
	public static <S extends Serializable> Object getValueFromEntity(Entity<S> entity, String fieldName) {
		String firstStr = fieldName.substring(0,1);
		String getMethodName = "get"+firstStr.toUpperCase()+fieldName.substring(1);
		Method method = BeanUtils.getMethod(entity, getMethodName);
		try {
			return method.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException("error when invoke the method of an entity: "+getMethodName, e);
		}
	}
	
	/**
	 * downcast to the entity by the type, decode the json and write to the entity.
	 * @param type the type of the entity
	 * @param json the json string
	 * @return the entity object
	 */
	public static <T extends Entity<S>, S extends Serializable> T bindEntity(Class<T> type, String json) {
		Class<Map<String, Object>> clazz = null;
		json = json.replace("=", ":");
		Map<String, Object> jsonMap = JSONObject.parseObject(json, clazz);
		return EntityUtils.createEntity(type, jsonMap);
	}
	
	/**
	 * downcast to the collection of the entities by the type, 
	 * decode the json and write to the entity.
	 * @param type the type of the entity
	 * @param value the entity which instance of list or set
	 * @return the collection of the entities
	 */
	public static <T extends Entity<S>, S extends Serializable> 
					Collection<T> bindListOrSetOfEntity(Class<T> type, Object value) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listOfMap = (List<Map<String, Object>>) value;
		List<T> list = new ArrayList<>();
		for(Map<String, Object> map : listOfMap) list.add(createEntity(type, map));
		return list;
	}
}