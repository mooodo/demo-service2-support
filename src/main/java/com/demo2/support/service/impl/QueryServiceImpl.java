/* 
 * Created by 2019年1月24日
 */
package com.demo2.support.service.impl;

import java.util.List;
import java.util.Map;

import com.demo2.support.dao.QueryDao;
import com.demo2.support.entity.ResultSet;
import com.demo2.support.service.QueryService;

/**
 * The implement of the generate query service.
 * @author fangang
 */
public class QueryServiceImpl implements QueryService {
	private QueryDao queryDao;
	/**
	 * @return the queryDao
	 */
	public QueryDao getQueryDao() {
		return queryDao;
	}

	/**
	 * @param queryDao the queryDao to set
	 */
	public void setQueryDao(QueryDao queryDao) {
		this.queryDao = queryDao;
	}

	@Override
	public ResultSet query(Map<String, Object> params) {
		beforeQuery(params);
		List<?> result = queryDao.query(params);
		
		ResultSet resultSet = new ResultSet();
		resultSet.setData(result);
		resultSet = afterQuery(params, resultSet);
		
		page(params, resultSet);
		aggregate(params, resultSet);
		return resultSet;
	}
	
	/**
	 * do something before query. 
	 * It just a hood that override the function in subclass if we need do something before query.
	 * @param params the parameters the query need
	 */
	protected void beforeQuery(Map<String, Object> params) {
		//just a hood
	}
	
	/**
	 * @param params the parameters the query need
	 * @param resultSet the result set after query.
	 * @return 
	 */
	protected ResultSet afterQuery(Map<String, Object> params, ResultSet resultSet) {
		//just a hood
		return resultSet;
	}
	
	/**
	 * @param params
	 * @param resultSet
	 */
	private void page(Map<String, Object> params, ResultSet resultSet) {
		if(params==null||params.isEmpty()) return;
		Object page = params.get("page");
		Object size = params.get("size");
		Object count = params.get("count");
		if( size==null ) return;
		
		int p = (page==null)? 0 : new Integer(page.toString());
		int s = new Integer(size.toString());
		params.put("page", p);
		resultSet.setPage(p);
		resultSet.setSize(s);
		
		long cnt = (count==null) ? queryDao.count(params) : new Long(count.toString());
		resultSet.setCount(cnt);
	}
	
	/**
	 * @param params
	 * @param resultSet
	 */
	private void aggregate(Map<String, Object> params, ResultSet resultSet) {
		if(params==null||params.isEmpty()) return;
		//TODO
	}
}
