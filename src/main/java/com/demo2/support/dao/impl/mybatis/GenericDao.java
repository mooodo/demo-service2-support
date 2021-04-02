/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao.impl.mybatis;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * The generic dao which persist data with mybatis.
 * @author fangang
 */
public interface GenericDao {
	/**
	 * @param tableName
	 * @param keys
	 * @param values
	 * @throws DataAccessException
	 */
	public void insert(String tableName,List<Object> keys,List<Object> values) throws DataAccessException;
	/**
	 * @param tableName
	 * @param keys
	 * @param pks
	 * @throws DataAccessException
	 */
	public void update(String tableName,List<Map<Object, Object>> keys,List<Map<Object, Object>> pks) throws DataAccessException;
	/**
	 * @param tableName
	 * @param pks
	 * @throws DataAccessException
	 */
	public void delete(String tableName,List<Map<Object, Object>> pks) throws DataAccessException;
	/**
	 * @param tableName
	 * @param pks
	 * @throws DataAccessException
	 */
	public void deleteForList(String tableName,List<Map<Object, Object>> pks) throws DataAccessException;
	/**
	 * @param tableName
	 * @param pks
	 * @return result set
	 * @throws DataAccessException
	 */
	public List<Map<String, Object>> load(String tableName,List<Map<Object, Object>> pks) throws DataAccessException;
	/**
	 * @param tableName
	 * @param pks
	 * @return result set
	 * @throws DataAccessException
	 */
	public List<Map<String, Object>> find(String tableName,List<Map<Object, Object>> pks) throws DataAccessException;
}
