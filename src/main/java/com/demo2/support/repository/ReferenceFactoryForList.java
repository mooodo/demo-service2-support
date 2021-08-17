/* 
 * create by 2020年2月4日 下午5:54:31
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.demo2.support.dao.impl.factory.Ref;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;
import com.demo2.support.utils.BeanUtils;

/**
 * @author fangang
 */
public class ReferenceFactoryForList <S extends Serializable, T extends Entity<S>> {
	private Ref ref;
	private Collection<T> entities;
	private ApplicationContext context = null;
	
	public ReferenceFactoryForList(ApplicationContext context) {
		this.context = context;
	}
	
	/**
	 * @param ref the information of reference.
	 * @param entities
	 */
	public void build(Ref ref, Collection<T> entities) {
		this.ref = ref;
		this.entities = entities;
		
		String refType = ref.getRefType();
		if("oneToOne".equals(refType)) {
			loadOfOneToOne(ref);
		}
		if("manyToOne".equals(refType)) {
			loadOfManyToOne(ref);
		}
		if("oneToMany".equals(refType)) {
			loadOfOneToMany(ref);
		}
		if("manyToMany".equals(refType)) {
			throw new OrmException("Don't support the many to many relation now!");
		}
	}
	
	/**
	 * load data of the one to one relation.
	 * @param ref the information of reference.
	 */
	@SuppressWarnings("unchecked")
	private void loadOfOneToOne(Ref ref) {
		if(ref==null||entities==null||entities.isEmpty()) return;
		List<S> ids = new ArrayList<>();
		for(T entity : entities) ids.add(entity.getId());
		String bean = ref.getBean();
		Object service = getBean(bean);
		String methodName = ref.getListMethod();
		Method method = getMethod(service, methodName);
		Collection<Entity<S>> listOfEntitiesNeedRef;
		try {
			listOfEntitiesNeedRef = (Collection<Entity<S>>)method.invoke(service, convertListToString(ids));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException("error when invoking the service by reflect", e);
		}
		
		Map<S,Entity<S>> mapOfEntitiesNeedRef = new HashMap<>();
		for(Entity<S> enj : listOfEntitiesNeedRef ) mapOfEntitiesNeedRef.put(enj.getId(), enj);
		
		for(T entity : entities) {
			Entity<S> enj = mapOfEntitiesNeedRef.get(entity.getId());
			setValueOfRefToEntity(entity, enj);
		}
	}
	
	/**
	 * load data of the many to one relation.
	 * @param ref the information of reference.
	 */
	@SuppressWarnings("unchecked")
	private void loadOfManyToOne(Ref ref) {
		if(ref==null||entities==null||entities.isEmpty()) return;
		String refKey = ref.getRefKey();
		List<S> ids = new ArrayList<>();
		for(T entity : entities) {
			S id = (S)BeanUtils.getValueByField(entity, refKey);
			ids.add(id);
		}
		String bean = ref.getBean();
		Object service = getBean(bean);
		String methodName = ref.getListMethod();
		Method method = getMethod(service, methodName);
		Collection<Entity<S>> listOfEntitiesNeedRef;
		try {
			listOfEntitiesNeedRef = (Collection<Entity<S>>)method.invoke(service, convertListToString(ids));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new OrmException("error when invoking the service by reflect", e);
		}
		
		Map<S,Entity<S>> mapOfEntitiesNeedRef = new HashMap<>();
		for(Entity<S> enj : listOfEntitiesNeedRef ) mapOfEntitiesNeedRef.put(enj.getId(), enj);
		
		for(T entity : entities) {
			S id = (S)BeanUtils.getValueByField(entity, refKey);
			Entity<S> enj = mapOfEntitiesNeedRef.get(id);
			setValueOfRefToEntity(entity, enj);
		}
	}
	
	/**
	 * load data of the one to many relation.
	 * @param ref the information of reference.
	 */
	@SuppressWarnings("unchecked")
	private void loadOfOneToMany(Ref ref) {
		if(ref==null||entities==null||entities.isEmpty()) return;
		String bean = ref.getBean();
		Object service = getBean(bean);
		String methodName = ref.getListMethod();
		Method method = getMethod(service, methodName);
		for(T entity : entities) {
			try {
				List<Object> list = (List<Object>)method.invoke(method, entity.getId());
				setListOfRefToEntity(entity, list);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new OrmException("error when invoking the service by reflect", e);
			}
		}
	}
	
	/**
	 * get bean in the spring context by name.
	 * @param name the bean name
	 * @return the instance of the bean
	 */
	private Object getBean(String name) {
		if(name==null||name.isEmpty()) throw new OrmException("The bean name is empty!");
		try {
			return context.getBean(name);
		} catch (NoSuchBeanDefinitionException e) {
			throw new OrmException("No such bean definition in the spring context!", e);
		} catch (BeansException e) {
			throw new OrmException("error when get the bean["+name+"]");
		}
	}
	
	/**
	 * get the method of the service by name, using reflect.
	 * @param service
	 * @param name the name of the method
	 * @return the reference of the method
	 */
	private Method getMethod(Object service, String name) {
		if(name==null||name.isEmpty()) 
			throw new OrmException("The method name is empty![service:"+service.getClass().getName()+", method:"+name+"]");
		Method[] allOfMethods = service.getClass().getDeclaredMethods();
		for(Method method : allOfMethods) {
			if(method.getName().equals(name)) return method;
		}
		throw new OrmException("No such method["+name+"] in the service["+service.getClass().getName()+"]");
	}
	
	/**
	 * set value of the reference to the entity.
	 * @param entity
	 * @param value
	 */
	private void setValueOfRefToEntity(T entity, Object value) {
		String name = ref.getName();
		BeanUtils.setValueByField(entity, name, value);
	}
	
	/**
	 * set value of the join to the value object.
	 * @param list the list of value.
	 */
	private void setListOfRefToEntity(T entity, List<Object> list) {
		String name = ref.getName();
		BeanUtils.setValueByField(entity, name, list);
	}
	
	/**
	 * @param list
	 * @return String
	 */
	private String convertListToString(List<?> list) {
		String str = list.toString();
		str = str.substring(1, str.length()-1);
		return str.replace(" ", "");
	}
}
