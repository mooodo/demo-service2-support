/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.demo2.support.entity.Entity;

/**
 * The basic dao for generic insert, update, delete operations.
 * @author fangang
 */
public interface BasicDao {
	/**
	 * insert an entity into table.
	 * @param entity
	 */
	public <T> void insert(T entity);
	/**
	 * update an entity.
	 * @param entity
	 */
	public <T> void update(T entity);
	/**
	 * if not exists, then insert, else update.
	 * @param entity
	 */
	public <T> void insertOrUpdate(T entity);
	/**
	 * insert a list of value objects, and if exists, then update.
	 * @param list
	 */
	public <T, S extends Collection<T>> void insertOrUpdateForList(S list);
	/**
	 * delete an entity.
	 * note: you must load the entity first or do like this: 
	 * <pre>
	 * User user = new User();
	 * user.setId("C0001");
	 * dao.delete(user);
	 * </pre>
	 * @param entity
	 */
	public <T> void delete(T entity);
	/**
	 * delete a list of value objects.
	 * @param list
	 */
	public <T, S extends Collection<T>> void deleteForList(S list);
	/**
	 * @param ids
	 * @param template
	 */
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template);
	/**
	 * load an entity by id.
	 * @param id
	 * @param template just an empty object to know which class
	 * @return entity
	 */
	public <S extends Serializable, T extends Entity<S>> T load(S id, T template);
	/**
	 * load a list of entity by their ids.
	 * @param ids the list of id
	 * @param template just an empty object to know which class
	 * @return list of entity
	 */
	public <S extends Serializable, T extends Entity<S>> List<T> loadForList(Collection<S> ids, T template);
	/**
	 * load all entities according to a condition, which the condition come from the template. 
	 * for example: I want to load all of the items of an order, then the template is the orderItem 
	 * and set the orderId to the 'orderId' column in the template. 
	 * Then it will load all the items of this order according to conditions in the template.
	 * @param template just an empty object to know which class, and set the condition to it.
	 * @return list of entities.
	 */
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(T template);
	/**
	 * load all entities according to a condition, which the condition come from the list<map>, 
	 * such as: [{key:"col0",opt:'=',value:"val0"},{key:"col1",opt:'=',value:"val1"}]
	 * @param colMap
	 * @param template
	 * @return list of entities.
	 */
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(List<Map<Object, Object>> colMap, T template);
	/**
	 * delete an entity by id.
	 * @param id
	 * @param template just an empty object to know which class
	 */
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template);
}
