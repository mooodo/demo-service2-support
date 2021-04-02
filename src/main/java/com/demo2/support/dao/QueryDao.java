/* 
 * Created by 2019年1月25日
 */
package com.demo2.support.dao;

import java.util.List;
import java.util.Map;

/**
 * The generate query dao
 * @author fangang
 */
public interface QueryDao {
	/**
	 * execute query
	 * @param params the parameters the query need
	 * @return the result set of query
	 */
	public List<?> query(Map<String, Object> params);
	/**
	 * get count of the query
	 * @param params the parameters the query need
	 * @return the count
	 */
	public long count(Map<String, Object> params);
	/**
	 * execute aggregate such as sum, count, average, etc.
	 * @param params the parameters the query need
	 * @return the result of the aggregate
	 */
	public Map<String, Object> aggregate(Map<String, Object> params);
}
