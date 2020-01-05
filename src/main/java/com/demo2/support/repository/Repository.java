/**
 * 
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.entity.Entity;
import com.demo2.support.utils.BeanUtils;

/**
 * The generic DDD repository for all of the services in the system.
 * @author fangang
 */
public class Repository implements BasicDao {
	private BasicDao dao;
	
	/**
	 * @return the dao
	 */
	public BasicDao getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(BasicDao dao) {
		this.dao = dao;
	}

	@Override
	public <T extends Entity> T load(Serializable id, T template) {
		T vo = loadFromCache(id, template);
		if(vo!=null) return vo;
		vo = loadFromDb(id, template);
		setJoins(vo);
		return vo;
	}

	/**
	 * load the value object from cache. If no found, return null.
	 * @param id
	 * @param template
	 * @return the value object
	 */
	private <T extends Entity> T loadFromCache(Serializable id, T template) {
		return null;
	}

	/**
	 * load the value object from database. If no found, return null.
	 * @param id
	 * @param template
	 * @return the value object
	 */
	private <T extends Entity> T loadFromDb(Serializable id, T template) {
		T vo = dao.load(id, template);
		return vo;
	}
	
	/**
	 * set the value object's joins, if it has.
	 * @param vo
	 */
	private void setJoins(Entity vo) {
		VObj vObj = VObjFactory.getVObj(vo.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			GenericEntityFactory factory = new GenericEntityFactory();
			factory.build(join, vo, dao);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void saveDetail(Object vo) {
		VObj vObj = VObjFactory.getVObj(vo.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			if(!join.isAggregation()) continue;
			String name = join.getName();
			Object detail = BeanUtils.getValueByField(vo, name);
			
			if(Collection.class.isAssignableFrom(detail.getClass())) {
				Collection<Object> collection = (Collection<Object>)detail;
				dao.insertOrUpdate(collection);
			} else {
				dao.insertOrUpdate(detail);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteDetail(Object vo) {
		VObj vObj = VObjFactory.getVObj(vo.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			if(!join.isAggregation()) continue;
			String name = join.getName();
			Object detail = BeanUtils.getValueByField(vo, name);
			
			if(Collection.class.isAssignableFrom(detail.getClass())) {
				Collection<Object> collection = (Collection<Object>)detail;
				dao.delete(collection);
			} else {
				dao.delete(detail);
			}
		}
	}

	@Override
	public void insert(Object vo) {
		dao.insert(vo);
		saveDetail(vo);
	}

	@Override
	public void update(Object vo) {
		dao.update(vo);
		saveDetail(vo);
	}

	@Override
	public void insertOrUpdate(Object vo) {
		dao.insertOrUpdate(vo);
		saveDetail(vo);
	}

	@Override
	public void insertOrUpdate(Collection<Object> list) {
		dao.insertOrUpdate(list);
		for(Object vo : list) {
			saveDetail(vo);
		}
	}

	@Override
	public void delete(Object vo) {
		dao.delete(vo);
		deleteDetail(vo);
	}

	@Override
	public void delete(Collection<Object> list) {
		dao.delete(list);
		for(Object vo : list){
			deleteDetail(vo);
		}
	}

	@Override
	public <T extends Entity> List<T> loadAll(T template) {
		//TODO 
		return dao.loadAll(template);
	}

	@Override
	public <T extends Entity> void delete(Serializable id, T template) {
		//TODO
		dao.delete(id, template);
	}

	@Override
	public <T extends Entity> List<T> loadForList(List<Serializable> ids, T template) {
		List<T> list = dao.loadForList(ids, template);
		
		return list;
	}
}
