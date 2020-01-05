/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.demo2.support.entity.Entity;

/**
 * The basic dao for generic insert, update, delete operations.
 * @author fangang
 */
public interface BasicDao {
	/**
	 * insert a value object into table.
	 * @param vo
	 */
	public void insert(Object vo);
	/**
	 * update a value object.
	 * @param vo
	 */
	public void update(Object vo);
	/**
	 * if not exists, then insert, else update.
	 * @param vo
	 */
	public void insertOrUpdate(Object vo);
	/**
	 * insert a list of value objects, and if exists, then update.
	 * @param list
	 */
	public void insertOrUpdate(Collection<Object> list);
	/**
	 * delete a value object.
	 * note: you must load the value object first or do like this: 
	 * <pre>
	 * User user = new User();
	 * user.setId("C0001");
	 * dao.delete(user);
	 * </pre>
	 * @param vo
	 */
	public void delete(Object vo);
	/**
	 * delete a list of value objects.
	 * @param list
	 */
	public void delete(Collection<Object> list);
	/**
	 * load an entity by id.
	 * @param id
	 * @param template just an empty object to know which class
	 * @return entity
	 */
	public <T extends Entity> T load(Serializable id, T template);
	/**
	 * load a list of entity by their ids.
	 * @param ids the list of id
	 * @param template just an empty object to know which class
	 * @return list of entity
	 */
	public <T extends Entity> List<T> loadForList(List<Serializable> ids, T template);
	/**
	 * load all entities.
	 * @param template just an empty object to know which class
	 * @return list of entities.
	 */
	public <T extends Entity> List<T> loadAll(T template);
	/**
	 * delete an entity by id.
	 * @param id
	 * @param template just an empty object to know which class
	 */
	public <T extends Entity> void delete(Serializable id, T template);
}
