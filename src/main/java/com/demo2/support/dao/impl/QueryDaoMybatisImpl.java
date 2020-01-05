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
		return sqlSession.selectList(sqlMapper, params);
	}

	@Override
	public long count(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> aggregate(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
