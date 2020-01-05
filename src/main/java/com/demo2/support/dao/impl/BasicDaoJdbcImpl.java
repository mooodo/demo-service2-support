/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao.impl;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.factory.Property;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.dao.impl.mybatis.GenericDao;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.DaoException;
import com.demo2.support.utils.BeanUtils;
import com.demo2.support.utils.DateUtils;

/**
 * The implement of BasicDao with Jdbc.
 * @author fangang
 */
public class BasicDaoJdbcImpl implements BasicDao {
	@Autowired
	private GenericDao dao;

	@Override
	public void insert(Object vo) {
		if(vo==null) throw new DaoException("The value object is null");
		TmpObj tmpObj = readDataFromVo(vo);
		try {
			dao.insert(tmpObj.tableName, tmpObj.columns, tmpObj.values);
		} catch (DataAccessException e) {
			throw new DaoException("error when insert vo");
		}
	}

	@Override
	public void update(Object vo) {
		if(vo==null) throw new DaoException("The value object is null");
		TmpObj tmpObj = readDataFromVo(vo);
		try {
			dao.update(tmpObj.tableName, tmpObj.colMap, tmpObj.pkMap);
		} catch (DataAccessException e) {
			throw new DaoException("error when update vo");
		}
		
	}

	@Override
	public void insertOrUpdate(Object vo) {
		if(vo==null) throw new DaoException("The value object is null");
		TmpObj tmpObj = readDataFromVo(vo);
		try {
			dao.insert(tmpObj.tableName, tmpObj.columns, tmpObj.values);
		} catch (DataAccessException e) {
			if(e.getCause() instanceof SQLIntegrityConstraintViolationException)
				update(vo);
			else throw new DaoException("error when insert vo");
		}
	}

	@Override
	public void insertOrUpdate(Collection<Object> list) {
		for(Object vo : list) insertOrUpdate(vo);
	}

	@Override
	public void delete(Object vo) {
		TmpObj tmpObj = readDataFromVo(vo);
		dao.delete(tmpObj.tableName, tmpObj.pkMap);
	}

	@Override
	public void delete(Collection<Object> list) {
		for(Object vo : list) delete(vo);
	}
	
	@Override
	public <T extends Entity> T load(Serializable id, T template) {
		if(id==null||template==null) throw new DaoException("illegal parameters!");
		template.setId(id);
		TmpObj tmpObj = readDataFromVo(template);
		List<Map<String, Object>> list = dao.find(tmpObj.tableName, tmpObj.pkMap);
		Map<String, Object> map = list.get(0);
		return this.setMapToVo(map, template);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entity> List<T> loadForList(List<Serializable> ids, T template) {
		if(ids==null||ids.isEmpty()||template==null) throw new DaoException("illegal parameters!");
		
		List<TmpObj> listOfTmpObj = new ArrayList<>();
		for(Serializable id : ids) {
			T temp = (T)BeanUtils.createEntity(template.getClass());
			temp.setId(id);
			TmpObj tmpObj = readDataFromVo(temp);
			listOfTmpObj.add(tmpObj);
		}
		
		Map<Object, List<Object>> mapOfValues = new HashMap<>();
		for(TmpObj tmpObj : listOfTmpObj) {
			 for(Map<Object, Object> map : tmpObj.pkMap) {
				 Object key = map.get("key");
				 Object value = map.get("value");
				 if(mapOfValues.get(key)==null) mapOfValues.put(key, new ArrayList<Object>());
				 mapOfValues.get(key).add(value);
			 }
		}
		
		TmpObj tmpObj = readDataFromVo(template);
		List<Map<Object, Object>> pkMap = new ArrayList<>();
		for(Object key : mapOfValues.keySet()) {
			Map<Object, Object> map = new HashMap<>();
			map.put("key", key);
			map.put("value", mapOfValues.get(key));
			pkMap.add(map);
		}
		tmpObj.pkMap = pkMap;		
		
		List<Map<String, Object>> list = dao.load(tmpObj.tableName, tmpObj.pkMap);
		
		List<T> listOfVo = new ArrayList<T>();
		for(Map<String, Object> map : list) {
			T temp = (T)BeanUtils.createEntity(template.getClass());
			T vo = this.setMapToVo(map, temp);
			listOfVo.add(vo);
		}
		return listOfVo;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Entity> List<T> loadAll(T template) {
		TmpObj tmpObj = readDataFromVo(template);
		List<Map<String, Object>> list = dao.find(tmpObj.tableName, tmpObj.colMap);
		
		List<T> listOfVo = new ArrayList<T>();
		for(Map<String, Object> map : list) {
			T temp = (T)BeanUtils.createEntity(template.getClass());
			T vo = this.setMapToVo(map, temp);
			listOfVo.add(vo);
		}
		return listOfVo;
	}
	
	@Override
	public <T extends Entity> void delete(Serializable id, T template) {
		if(id==null||template==null) throw new DaoException("illegal parameters!");
		T vo = this.load(id, template);
		this.delete(vo);
	}
	
	/**
	 * according to the configure, read each of field's value from the value object.
	 * @param vo the value object
	 * @return the result object
	 */
	private TmpObj readDataFromVo(Object vo) {
		if(vo==null) throw new DaoException("The value object is null");
		
		VObj vObj = VObjFactory.getVObj(vo.getClass().getName());
		if(vObj==null) throw new DaoException("No found the entity ["+vo.getClass().getName()+"] in the vObj.xml");
		
		List<Property> properties = vObj.getProperties();
		TmpObj tmpObj = new TmpObj();
		tmpObj.tableName = vObj.getTable();
		
		for(Property property : properties) {
			String name = property.getName();
			String column = property.getColumn();
			Object value = BeanUtils.getValueByField(vo, name);
			
			if(value==null) continue;
			tmpObj.columns.add(column);
			tmpObj.values.add(value);
			
			Map<Object, Object> map = new HashMap<>();
			map.put("key", column);
			map.put("value", value);
			tmpObj.colMap.add(map);
			
			if(property.isPrimaryKey()) tmpObj.pkMap.add(map);
		}
		return tmpObj;
	}
	
	/**
	 * @param map
	 * @param vo
	 * @return
	 */
	private <T> T setMapToVo(Map<String, Object> map, T vo) {
		if(map==null && vo==null) throw new DaoException("Illegal parameters!");
		for(String key : map.keySet()) {
			Object value = map.get(key);
			BeanUtils.setValueByField(vo, key, new BeanUtils.BeanCallback() {
				@Override
				public Object getValue(Class<?> clazz) {
					return bind(clazz, value);
				}
			});
		}
		return vo;
	}
	
	/**
	 * Downcast the value to the class it is.
	 * @param clazz
	 * @param value
	 * @return the downcast value
	 */
	private Object bind(Class<?> clazz, Object value) {
		if(value==null) return value;
		if(clazz.equals(String.class)) return value;
		
		String str = value.toString();
		if(clazz.equals(Long.class)||clazz.equals(long.class)) return new Long(str);
		if(clazz.equals(Integer.class)||clazz.equals(int.class)) return new Integer(str);
		if(clazz.equals(Double.class)||clazz.equals(double.class)) return new Double(str);
		if(clazz.equals(Float.class)||clazz.equals(float.class)) return new Float(str);
		if(clazz.equals(Short.class)||clazz.equals(short.class)) return new Short(str);
		
		if(clazz.equals(Date.class)&&str.length()==10) return DateUtils.getDate(str,"yyyy-MM-dd");
		if(clazz.equals(Date.class)) return DateUtils.getDate(str);
		
		//TODO how to bind map, list and set.
		return value;
	}
	
	class TmpObj {
		String tableName;
		List<Object> columns = new ArrayList<>();
		List<Object> values = new ArrayList<>();
		List<Map<Object, Object>> colMap = new ArrayList<>();
		List<Map<Object, Object>> pkMap = new ArrayList<>();
	}
}
