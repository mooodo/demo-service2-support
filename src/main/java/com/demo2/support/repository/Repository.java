package com.demo2.support.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.DecoratorDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.dao.impl.factory.Ref;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.entity.Entity;
import com.demo2.support.utils.BeanUtils;

/**
 * The generic DDD repository for all of the services in the system. 
 * According to the configuration vObj.xml: 
 * 1)if the entity has any join, fill the join after load data.
 * 2)if the entity has any join and the join is aggregation, save the join data in same transaction.
 * @author fangang
 */
public class Repository extends DecoratorDao implements BasicDao {
	@Autowired
	private ApplicationContext context;
	
	@Override
	public <S extends Serializable, T extends Entity<S>> T load(S id, T template) {
		T entity = super.load(id, template);
		setJoins(entity);
		setRefs(entity);
		return entity;
	}
	
	/**
	 * set the entity's joins, if it has.
	 * @param entity
	 */
	private <S extends Serializable> void setJoins(Entity<S> entity) {
		if(entity==null) return;
		VObj vObj = VObjFactory.getVObj(entity.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			GenericEntityFactory<S> factory = new GenericEntityFactory<>();
			factory.build(join, entity, dao);
		}
	}
	
	/**
	 * set each of entities' joins for a list
	 * @param list
	 */
	private <S extends Serializable, T extends Entity<S>> void setJoinsForList(Collection<T> list) { 
		if(list==null||list.isEmpty()) return;
		Entity<S> entity = list.iterator().next();
		VObj vObj = VObjFactory.getVObj(entity.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			GenericEntityFactoryForList<S, T> factory = new GenericEntityFactoryForList<>();
			factory.build(join, list, dao);
		}
	}
	
	/**
	 * set the entity's refs, if it has.
	 * @param entity
	 */
	private <S extends Serializable> void setRefs(Entity<S> entity) {
		if(entity==null) return;
		VObj vObj = VObjFactory.getVObj(entity.getClass().getName());
		List<Ref> listOfRefs = vObj.getRefs();
		if(listOfRefs==null||listOfRefs.isEmpty()) return;
		
		for(Ref ref : listOfRefs) {
			ReferenceFactory<S> factory = new ReferenceFactory<>(context);
			factory.build(ref, entity);
		}
	}

	/**
	 * set each of entities' refs for a list
	 * @param list
	 * @param <S> The key
	 * @param <T> The entity
	 */
	private <S extends Serializable, T extends Entity<S>> void setRefsForList(Collection<T> list) {
		if(list==null||list.isEmpty()) return;
		Entity<S> template = list.iterator().next();
		VObj vObj = VObjFactory.getVObj(template.getClass().getName());
		List<Ref> listOfRefs = vObj.getRefs();
		if(listOfRefs==null||listOfRefs.isEmpty()) return;
		
		for(Ref ref : listOfRefs) {
			ReferenceFactoryForList<S, T> factory = new ReferenceFactoryForList<>(context);
			factory.build(ref, list);
		}
	}
	
	/**
	 * @param template
	 * @return whether the entity has join and the join is aggregation.
	 */
	private <S extends Serializable> boolean hasJoinAndAggregation(Entity<S> template) {
		if(template==null) return false;
		VObj vObj = VObjFactory.getVObj(template.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return false;
		int count = 0;
		for(Join join : listOfJoins) {
			if(join.isAggregation()) count++;
		}
		if(count>0) return true;
		return false;
	}
	
	/**
	 * @param template
	 * @return whether the entity has reference and the reference is aggregation.
	 */
	private <S extends Serializable> boolean hasRefAndAggregation(Entity<S> template) {
		if(template==null) return false;
		VObj vObj = VObjFactory.getVObj(template.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return false;
		int count = 0;
		for(Join join : listOfJoins) {
			if(join.isAggregation()) count++;
		}
		if(count>0) return true;
		return false;
	}
	
	/**
	 * save all of the joins of an entity, if it has.
	 * @param entity
	 */
	private void saveJoins(Object entity) {
		VObj vObj = VObjFactory.getVObj(entity.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			if(!join.isAggregation()) continue;
			String name = join.getName();
			Object value = BeanUtils.getValueByField(entity, name);
			if(value==null) continue;
			if(Collection.class.isAssignableFrom(value.getClass())) {
				Collection<?> collection = (Collection<?>)value;
				super.insertOrUpdateForList(collection);//is a lot of items.
			} else {
				super.insertOrUpdate(value);//just one item.
			}
		}
	}
	
	/**
	 * delete all of the joins of an entity, if it has.
	 * @param entity the entity
	 */
	private void deleteJoins(Object entity) {
		VObj vObj = VObjFactory.getVObj(entity.getClass().getName());
		List<Join> listOfJoins = vObj.getJoins();
		if(listOfJoins==null||listOfJoins.isEmpty()) return;
		
		for(Join join : listOfJoins) {
			if(!join.isAggregation()) continue;
			String name = join.getName();
			Object value = BeanUtils.getValueByField(entity, name);
			if(value==null) continue;
			if(Collection.class.isAssignableFrom(value.getClass())) {
				Collection<?> collection = (Collection<?>)value;
				super.deleteForList(collection);//is a lot of items.
			} else {
				super.delete(value);//just one item.
			}
		}
	}

	@Override
	@Transactional
	public <T> void insert(T entity) {
		super.insert(entity);
		saveJoins(entity);
	}

	@Override
	@Transactional
	public <T> void update(T entity) {
		super.update(entity);
		saveJoins(entity);
	}

	@Override
	@Transactional
	public <T> void insertOrUpdate(T entity) {
		super.insertOrUpdate(entity);
		saveJoins(entity);
	}

	@Override
	@Transactional
	public <T, S extends Collection<T>> void insertOrUpdateForList(S list) {
		for(Object entity : list) insertOrUpdate(entity);
	}

	@Override
	@Transactional
	public <T> void delete(T entity) {
		super.delete(entity);
		deleteJoins(entity);
	}

	@Override
	@Transactional
	public <T, S extends Collection<T>> void deleteForList(S list) {
		for(Object entity : list) delete(entity);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(T template) {
		List<T> list = super.loadAll(template);
		setJoinsForList(list);
		setRefsForList(list);
		return list;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(List<Map<Object, Object>> colMap, T template) {
		List<T> list = super.loadAll(colMap, template);
		setJoinsForList(list);
		setRefsForList(list);
		return list;
	}

	@Override
	@Transactional
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template) {
		if(hasJoinAndAggregation(template)||hasRefAndAggregation(template)) {
			T entity = super.load(id, template);
			delete(entity);
		} else super.delete(id, template);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadForList(Collection<S> ids, T template) {
		if(ids==null||template==null) return null;
		List<T> list = super.loadForList(ids, template);
		for(T entity : list) {
			setJoins(entity);
			setRefs(entity);
		}
		return list;
	}

	@Override
	@Transactional
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template) {
		if(hasJoinAndAggregation(template)||hasRefAndAggregation(template)) {
			List<T> list = super.loadForList(ids, template);
			deleteForList(list);
		} else super.deleteForList(ids, template);
	}
}
