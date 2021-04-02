/*
 * Created by 2020-12-04 12:22:23 
 */
package com.demo2.support.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo2.support.dao.impl.factory.Property;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.DaoException;
import com.demo2.support.utils.BeanUtils;

/**
 * The utilities for entity.
 * @author fangang
 */
public class DaoEntityHelper {

	/**
	 * according to the configure, read each of field's value from the entity.
	 * @param entity
	 * @return daoEntity
	 */
	public static DaoEntity readDataFromEntity(Object entity) {
		if(entity==null) throw new DaoException("The entity is null");
		
		VObj vObj = VObjFactory.getVObj(entity.getClass().getName());
		if(vObj==null) throw new DaoException("No found the entity ["+entity.getClass().getName()+"] in the vObj.xml");
		
		List<Property> properties = vObj.getProperties();
		DaoEntity daoEntity = new DaoEntity();
		daoEntity.setTableName(vObj.getTable());
		
		for(Property property : properties) {
			String name = property.getName();
			String column = property.getColumn();
			Object value = BeanUtils.getValueByField(entity, name);
			
			if(value==null) continue;
			daoEntity.getColumns().add(column);
			daoEntity.getValues().add(value);
			
			Map<Object, Object> map = new HashMap<>();
			map.put("key", column);
			map.put("value", value);
			daoEntity.getColMap().add(map);
			
			if(property.isPrimaryKey()) daoEntity.getPkMap().add(map);
		}
		return daoEntity;
	}
	
	/**
	 * convert the data map to entity
	 * @param map
	 * @param template 
	 * @return the entity with its data
	 */
	public static <S extends Serializable, T extends Entity<S>> T convertMapToEntity(Map<String, Object> map, T template) {
		if(map==null && template==null) throw new DaoException("Illegal parameters!");
		@SuppressWarnings("unchecked")
		T entity = (T)template.clone();
		for(String key : map.keySet()) {
			Object value = map.get(key);
			BeanUtils.setValueByField(entity, key, type->{return BeanUtils.bind(type, value);});
		}
		return entity;
	}
	
	/**
	 * convert a list of map to a list of entities
	 * @param listOfMap
	 * @param template
	 * @return the list of entities with their data.
	 */
	public static <S extends Serializable, T extends Entity<S>> List<T> convertMapToEntityForList(List<Map<String, Object>> listOfMap, T template) {
		if(listOfMap==null && template==null) throw new DaoException("Illegal parameters!");
		List<T> listOfEntities = new ArrayList<>();
		listOfMap.forEach(map -> {
			T entity = convertMapToEntity(map, template);
			listOfEntities.add(entity);
		});
		return listOfEntities;
	}
	
	/**
	 * prepare for list operations.
	 * @param ids
	 * @param template
	 * @return daoHelper
	 */
	public static <S extends Serializable, T extends Entity<S>> 
			DaoEntity prepareForList(Collection<S> ids, T template) {
		if(template==null) throw new DaoException("illegal parameters!");
		if(ids==null||ids.isEmpty()) return null;
		
		//list of DaoEntity, which help to execute sql.
		List<DaoEntity> listOfDaoHelper = new ArrayList<>();
		for(S id : ids) {
			@SuppressWarnings("unchecked")
			T temp = (T)template.clone();
			temp.setId(id);
			DaoEntity helper = DaoEntityHelper.readDataFromEntity(temp);
			listOfDaoHelper.add(helper);
		}
		
		//deal with the map of primary keys.
		Map<Object, List<Object>> mapOfValues = new HashMap<>();
		for(DaoEntity helper : listOfDaoHelper) {
			 for(Map<Object, Object> map : helper.getPkMap()) {
				 Object key = map.get("key");
				 Object value = map.get("value");
				 if(mapOfValues.get(key)==null) mapOfValues.put(key, new ArrayList<Object>());
				 mapOfValues.get(key).add(value);
			 }
		}
		
		DaoEntity helper = DaoEntityHelper.readDataFromEntity(template);
		List<Map<Object, Object>> pkMap = new ArrayList<>();
		for(Object key : mapOfValues.keySet()) {
			Map<Object, Object> map = new HashMap<>();
			map.put("key", key);
			map.put("value", mapOfValues.get(key));
			pkMap.add(map);
		}
		helper.setPkMap(pkMap);
		return helper;
	}
}
