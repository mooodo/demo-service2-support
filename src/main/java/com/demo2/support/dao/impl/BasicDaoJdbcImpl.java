/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao.impl;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.mybatis.GenericDao;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.DaoException;

/**
 * The implement of BasicDao with Jdbc.
 * @author fangang
 */
public class BasicDaoJdbcImpl implements BasicDao {
	@Autowired
	private GenericDao dao;

	@Override
	public <T> void insert(T entity) {
		if(entity==null) throw new DaoException("The entity is null");
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(entity);
		try {
			dao.insert(daoEntity.getTableName(), daoEntity.getColumns(), daoEntity.getValues());
		} catch (DataAccessException e) {
			throw new DaoException("error when insert entity", e);
		}
	}

	@Override
	public <T> void update(T entity) {
		if(entity==null) throw new DaoException("The entity is null");
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(entity);
		try {
			dao.update(daoEntity.getTableName(), daoEntity.getColMap(), daoEntity.getPkMap());
		} catch (DataAccessException e) {
			throw new DaoException("error when update entity", e);
		}
	}

	@Override
	public <T> void insertOrUpdate(T entity) {
		if(entity==null) throw new DaoException("The entity is null");
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(entity);
		try {
			dao.insert(daoEntity.getTableName(), daoEntity.getColumns(), daoEntity.getValues());
		} catch (DataAccessException e) {
			if(e.getCause() instanceof SQLIntegrityConstraintViolationException)
				update(entity);
			else throw new DaoException("error when insert entity", e);
		}
	}

	@Override
	public <T, S extends Collection<T>> void insertOrUpdateForList(S list) {
		for(Object entity : list) insertOrUpdate(entity);
	}

	@Override
	public <T> void delete(T entity) {
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(entity);
		dao.delete(daoEntity.getTableName(), daoEntity.getPkMap());
	}

	@Override
	public <T, S extends Collection<T>> void deleteForList(S list) {
		for(Object entity : list) delete(entity);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template) {
		DaoEntity daoEntity = DaoEntityHelper.prepareForList(ids, template);
		dao.deleteForList(daoEntity.getTableName(), daoEntity.getPkMap());
	}
	
	@Override
	public <S extends Serializable, T extends Entity<S>> T load(S id, T template) {
		if(id==null||template==null) throw new DaoException("illegal parameters!");
		template.setId(id);
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(template);
		List<Map<String, Object>> list = dao.find(daoEntity.getTableName(), daoEntity.getPkMap());
		if(list.isEmpty()) return null;
		Map<String, Object> map = list.get(0);
		return DaoEntityHelper.convertMapToEntity(map, template);
	}
	
	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadForList(Collection<S> ids, T template) {
		DaoEntity daoEntity = DaoEntityHelper.prepareForList(ids, template);
		List<Map<String, Object>> listOfMap = dao.load(daoEntity.getTableName(), daoEntity.getPkMap());
		return DaoEntityHelper.convertMapToEntityForList(listOfMap, template);
	}
	
	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(T template) {
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(template);
		List<Map<String, Object>> listOfMap = 
				dao.find(daoEntity.getTableName(), daoEntity.getColMap());
		return DaoEntityHelper.convertMapToEntityForList(listOfMap, template);
	}
	
	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(List<Map<Object, Object>> colMap, T template) {
		DaoEntity daoEntity = DaoEntityHelper.readDataFromEntity(template);
		
		List<Map<String, Object>> listOfMap = 
				dao.load(daoEntity.getTableName(), colMap);
		return DaoEntityHelper.convertMapToEntityForList(listOfMap, template);
	}
	
	@Override
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template) {
		if(id==null||template==null) throw new DaoException("illegal parameters!");
		T entity = this.load(id, template);
		this.delete(entity);
	}
}
