/* 
 * create by 2020年1月30日 上午11:28:38
 */
package com.demo2.support.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.demo2.support.entity.Entity;

/**
 * The basic cache used by ddd repository.
 * @see com.demo2.support.repository.Repository
 * @author fangang
 */
public interface BasicCache {
	/**
	 * @param vo the value object
	 */
	public <S extends Serializable, T extends Entity<S>> void set(T vo);
	/**
	 * @param id
	 * @param template
	 * @return the value object
	 */
	public <S extends Serializable, T extends Entity<S>> T get(S id, T template);
	/**
	 * @param id
	 * @param template
	 */
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template);
	/**
	 * @param vos the list of value objects.
	 */
	public <S extends Serializable, T extends Entity<S>> void setForList(Collection<T> vos);
	/**
	 * @param ids the list of id
	 * @param template
	 * @return the list of value objects
	 */
	public <S extends Serializable, T extends Entity<S>> Collection<T> getForList(Collection<S> ids, T template);
	/**
	 * @param ids the list of id
	 * @param template
	 */
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template);
	/**
	 * set a value to cache, that the value is a list.
	 * @param template
	 * @param list
	 */
	public <S extends Serializable, T extends Entity<S>> void setList(T template, List<T> list);
	/**
	 * @param template
	 * @return get the value by id, that the value is a list.
	 */
	public <S extends Serializable, T extends Entity<S>> List<T> getList(T template);
	/**
	 * delete a value from cache, that the value is a list.
	 * @param template
	 */
	public <S extends Serializable, T extends Entity<S>> void deleteList(T template);
}
