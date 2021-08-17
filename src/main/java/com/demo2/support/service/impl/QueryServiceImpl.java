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
 * The implement of the generic query service.
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
		ResultSet resultSet = new ResultSet();
		page(params, resultSet);
		aggregate(params, resultSet);
		
		beforeQuery(params);
		List<?> result = queryDao.query(params);
		resultSet.setData(result);
		resultSet = afterQuery(params, resultSet);
		return resultSet;
	}
	
	/**
	 * do something before query. 
	 * It just a hook that override the function in subclass if we need do something before query.
	 * @param params the parameters the query need
	 */
	protected void beforeQuery(Map<String, Object> params) {
		//just a hook
	}
	
	/**
	 * do something after query.
	 * It just a hook that override the function in subclass if we need do something after query.
	 * @param params the parameters the query need
	 * @param resultSet the result set before do something.
	 * @return the result set after do something.
	 */
	protected ResultSet afterQuery(Map<String, Object> params, ResultSet resultSet) {
		//just a hook
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
		
		long cnt = (count==null) ? queryDao.count(params) : new Long(count.toString());
		resultSet.setCount(cnt);
		
		if( size==null ) return;
		int p = (page==null)? 0 : new Integer(page.toString());
		int s = new Integer(size.toString());
		int firstRow = p * s;
		params.put("page", p);
		params.put("size", s);
		params.put("firstRow", firstRow);
		resultSet.setPage(p);
		resultSet.setSize(s);
	}
	
	/**
	 * @param params
	 * @param resultSet
	 */
	private void aggregate(Map<String, Object> params, ResultSet resultSet) {
		if(params==null||params.isEmpty()) return;
		@SuppressWarnings("unchecked")
		Map<String, String> aggregation = (Map<String, String>)params.get("aggregation");
		if(aggregation==null||aggregation.isEmpty()) return;
		
		@SuppressWarnings("unchecked")
		Map<String, Object> aggValue = (Map<String, Object>)params.get("aggValue");
		if(aggValue==null) aggValue = queryDao.aggregate(params);
		resultSet.setAggregation(aggValue);
	}
}
