/*
 * created by 2019年7月29日 下午3:45:48
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.entity.Entity;
import com.demo2.support.entity.ResultSet;
import com.demo2.support.service.impl.QueryServiceImpl;
import com.demo2.support.utils.BeanUtils;

/**
 * @author fangang
 */
public class AutofillQueryServiceImpl extends QueryServiceImpl {
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
	protected ResultSet afterQuery(Map<String, Object> params, ResultSet resultSet) {
		List<?> list = resultSet.getData();
		if(list==null||list.isEmpty())
			return super.afterQuery(params, resultSet);
		
		Object firstOfVo = list.get(0);
		List<Join> listOfJoins = listOfJoins(firstOfVo);
		if(listOfJoins==null||listOfJoins.isEmpty())
			return super.afterQuery(params, resultSet);
		
		for(Join join : listOfJoins) {
			autofill(list, join);
		}
		return super.afterQuery(params, resultSet);
	}
	
	/**
	 * list all of the joins in the value object.
	 * @param vo the value object
	 * @return the list of joins
	 */
	private List<Join> listOfJoins(Object vo) {
		VObj vObj = VObjFactory.getVObj(vo.getClass().getName());
		return vObj.getJoins();
	}
	
	/**
	 * auto fill all of the joins in the value object.
	 * @param list
	 * @param join
	 */
	private void autofill(List<?> list, Join join) {
		List<Serializable> listOfIds = new ArrayList<>();
		String key = join.getJoinKey();
		if(!"oneToOne".equals(join.getJoinType())&&!"manyToOne".equals(join.getJoinType())) return;
		
		for(Object vo : list) {
			Serializable id = (Serializable)BeanUtils.getValueByField(vo, key);
			listOfIds.add(id);
		}
		
		Entity template = BeanUtils.createEntity(join.getClazz());
		List<Entity> listOfEntity = dao.loadForList(listOfIds, template);
		
		Map<Object, Entity> mapOfEntity = new HashMap<>();
		for(Entity entity : listOfEntity) {
			mapOfEntity.put(entity.getId(), entity);
		}
		
		for(Object vo : list) {
			Serializable id = (Serializable)BeanUtils.getValueByField(vo, key);
			Entity entity = mapOfEntity.get(id);
			BeanUtils.setValueByField(vo, join.getName(), entity);
		}
	}

}
