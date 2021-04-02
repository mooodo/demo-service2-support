/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo2.support.dao.QueryDao;

/**
 * The implements of the QueryDao with mybatis.
 * @author fangang
 */
public class QueryDaoMybatisImpl implements QueryDao {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	private String sqlMapper;

	/**
	 * @return the sqlMapper
	 */
	public String getSqlMapper() {
		return sqlMapper;
	}

	/**
	 * @param sqlMapper the sqlMapper to set
	 */
	public void setSqlMapper(String sqlMapper) {
		this.sqlMapper = sqlMapper;
	}

	@Override
	public List<?> query(Map<String, Object> params) {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectList(sqlMapper+".query", params);
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public long count(Map<String, Object> params) {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectOne(sqlMapper+".count", params);
		} finally {
			sqlSession.close();
		}
	}

	@Override
	public Map<String, Object> aggregate(Map<String, Object> params) {
		@SuppressWarnings("unchecked")
		Map<String, String> aggregation = (Map<String, String>)params.get("aggregation");
		if(aggregation==null||aggregation.isEmpty()) return null;
		
		String buffer = "";
		for(String key : aggregation.keySet()) {
			String value = aggregation.get(key);
			if(!"".equals(buffer)) buffer+=", ";
			buffer += value+"("+key+") "+key;
		}
		params.put("aggregation", buffer);
		
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectOne(sqlMapper+".aggregate", params);
		} finally {
			sqlSession.close();
		}
	}
}
