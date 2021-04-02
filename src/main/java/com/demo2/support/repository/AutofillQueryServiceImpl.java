/*
 * created by 2019年7月29日 下午3:45:48
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.dao.impl.factory.Ref;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.entity.Entity;
import com.demo2.support.entity.ResultSet;
import com.demo2.support.exception.QueryException;
import com.demo2.support.service.impl.QueryServiceImpl;

/**
 * The implement of the query service that 
 * it auto fill each of the object property of the item of the query result set, like this: 
 * fill the addresses of each the customer of the result set.
 * @author fangang
 */
public class AutofillQueryServiceImpl extends QueryServiceImpl {
	private BasicDao dao;
	/**
	 * @return the dao
	 */
	public BasicDao getDao() {
		if(dao==null) throw new QueryException("The dao is null");
		return dao;
	}
	/**
	 * @param dao the dao to set
	 */
	public void setDao(BasicDao dao) {
		this.dao = dao;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ResultSet afterQuery(Map<String, Object> params, ResultSet resultSet) {
		//if no result, do nothing.
		List<?> list = resultSet.getData();
		if(list==null||list.isEmpty())
			return super.afterQuery(params, resultSet);
		
		//if the value object hasn't any join, do nothing.
		Object firstOfVo = list.get(0);
		
		//auto fill value objects for each joins.
		List<Join> listOfJoins = listOfJoins(firstOfVo);
		if(listOfJoins!=null&&!listOfJoins.isEmpty()) {
			for(Join join : listOfJoins) {
				//TODO have exception
				autoFillJoin((List<Entity<Serializable>>)list, join);
			}
		}
		
		List<Ref> listOfRefs = listOfRefs(firstOfVo);
		if(listOfRefs!=null&&!listOfRefs.isEmpty()) {
			for(Ref ref : listOfRefs) {
				//TODO have exception
				autoFillRef((List<Entity<Serializable>>)list, ref);
			}
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
	 * list all of the refs in the value object.
	 * @param vo the value object
	 * @return the list of joins
	 */
	private List<Ref> listOfRefs(Object vo) {
		VObj vObj = VObjFactory.getVObj(vo.getClass().getName());
		return vObj.getRefs();
	}
	
	/**
	 * auto fill all of the joins in the value object.
	 * @param list
	 * @param join
	 */
	private <S extends Serializable, T extends Entity<S>> void autoFillJoin(List<T> list, Join join) {
		if(list==null||list.isEmpty()||join==null) return;
		GenericEntityFactoryForList<S, T> factory = new GenericEntityFactoryForList<>();
		factory.build(join, list, getDao());
	}
	
	@Autowired
	private ApplicationContext context;
	
	/**
	 * auto fill all of the joins in the value object.
	 * @param list
	 * @param ref
	 */
	private <S extends Serializable, T extends Entity<S>> void autoFillRef(List<T> list, Ref ref) {
		if(list==null||list.isEmpty()||ref==null) return;
		ReferenceFactoryForList<S, T> factory = new ReferenceFactoryForList<>(context);
		factory.build(ref, list);
	}
}
