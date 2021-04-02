/*
 * Created by 2020-12-31 20:05:21 
 */
package com.demo2.support.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.DecoratorDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.dao.impl.factory.VObj;
import com.demo2.support.dao.impl.factory.VObjFactory;
import com.demo2.support.entity.Entity;
import com.demo2.support.utils.BeanUtils;
import com.demo2.support.utils.EntityUtils;

/**
 * The Dao using cache: 
 * 1) if load an entity, load from cache first.
 * 2) if not in the cache, set to cache after query from database.
 * 3) if update or delete an entity, delete from cache.
 * @author fangang
 */
public class CacheEntityDao extends DecoratorDao implements BasicDao {
	private BasicCache cache;
	/**
	 * @return the cache
	 */
	public BasicCache getCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(BasicCache cache) {
		this.cache = cache;
	}

	@Override
	public <T> void insert(T entity) {
		super.insert(entity);
	}

	@Override
	public <T> void update(T entity) {
		super.update(entity);
		if(entity instanceof Entity) {
			deleteCache((Entity<?>) entity);
			deleteCacheOfJoin((Entity<?>)entity);
		}
	}

	/**
	 * delete the value in the cache.
	 * @param entity the entity
	 */
	private <S extends Serializable, T extends Entity<S>> void deleteCache(T entity) {
		cache.delete(entity.getId(), entity);
	}
	
	private <S extends Serializable, T extends Entity<S>> void deleteCacheOfJoin(T entity) {
		String className = entity.getClass().getName();
		VObj vObj = VObjFactory.getVObj(className);
		List<Join> listOfJoins = vObj.getJoins();
		listOfJoins.forEach(join -> {
			String clazz = join.getClazz();
			Entity<S> template = EntityUtils.createEntity(clazz, null);
			String joinKey = join.getJoinKey();
			if(join.getJoinType().equals("oneToMany")) {
				BeanUtils.setValueByField(template, joinKey, entity.getId());
				cache.deleteList(template);
			} else {
				@SuppressWarnings("unchecked")
				S id = (S)BeanUtils.getValueByField(entity, joinKey);
				cache.delete(id, template);
			}
		});
	}

	@Override
	public <T> void insertOrUpdate(T entity) {
		super.insertOrUpdate(entity);
		if(entity instanceof Entity) {
			deleteCache((Entity<?>) entity);
			deleteCacheOfJoin((Entity<?>)entity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Collection<T>> void insertOrUpdateForList(S list) {
		super.insertOrUpdateForList(list);
		if(!list.isEmpty() && list.iterator().next() instanceof Entity) {
			deleteCacheForList((Collection<Entity<Serializable>>)list);
			deleteCacheOfJoinForList((Collection<Entity<Serializable>>)list);
		}
	}
	
	/**
	 * delete a list of entities from cache.
	 * @param list
	 */
	private <S extends Serializable, T extends Entity<S>> void deleteCacheForList(Collection<T> list) {
		Collection<S> ids = new ArrayList<>();
		T template = null;
		for(T entity:list) {
			ids.add(entity.getId());
			template = entity;
		}
		cache.deleteForList(ids, template);
	}
	
	/**
	 * delete the joins of a list of entities from cache.
	 * @param list
	 */
	private <S extends Serializable, T extends Entity<S>> void deleteCacheOfJoinForList(Collection<T> list) {
		for(T entity:list) 
			deleteCacheOfJoin(entity);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template) {
		super.delete(id, template);
		cache.delete(id, template);
		template.setId(id);
		deleteCacheOfJoin(template);
	}

	@Override
	public <T> void delete(T entity) {
		super.delete(entity);
		if(entity instanceof Entity) {
			deleteCache((Entity<?>) entity);
			deleteCacheOfJoin((Entity<?>) entity);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template) {
		super.deleteForList(ids, template);
		cache.deleteForList(ids, template);
		List<T> list = new ArrayList<>();
		ids.forEach(id -> {
			T temp = (T)template.clone();
			list.add(temp);
		});
		deleteCacheOfJoinForList(list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Collection<T>> void deleteForList(S list) {
		super.deleteForList(list);
		if(!list.isEmpty() && list.iterator().next() instanceof Entity) {
			deleteCacheForList((Collection<Entity<Serializable>>)list);
			deleteCacheOfJoinForList((Collection<Entity<Serializable>>)list);
		}
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> T load(S id, T template) {
		T entity = cache.get(id, template);
		if(entity!=null) return entity;
		entity = super.load(id, template);
		cache.set(entity);
		return entity;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadForList(Collection<S> ids, T template) {
		if(ids==null||template==null) return null;
		
		Collection<T> entities = cache.getForList(ids, template);
		entities.removeIf(t->t==null);
		List<S> otherIds = getIdsNotInCache(ids, entities);
		if(otherIds.isEmpty()) return (List<T>)entities; //cache get all of the entities.
		
		List<T> list = super.loadForList(otherIds, template);
		cache.setForList(list);
		if(otherIds.size()==ids.size()) return list; //all of the entities query for database.
		return (List<T>)fillOtherEntitiesIn(entities, list); //fill the entity query for db in the list of entities get in cache.
	}
	
	/**
	 * @param ids
	 * @param entities
	 * @return all of the id not in cache
	 */
	private <S extends Serializable, T extends Entity<S>> List<S> getIdsNotInCache(Collection<S> ids, Collection<T> entities) {
		Map<S, T> map = new HashMap<>();
		for(T entity : entities) if(entity!=null) map.put(entity.getId(), entity);
		List<S> otherIds = new ArrayList<>();
		for(S id : ids) 
			if(id!=null&&map.get(id)==null) 
				otherIds.add(id);
		return otherIds;
	}
	
	/**
	 * fill the entities, which load from other source, in the list of entities load from cache.
	 * @param ids
	 * @param entities the list of entities load from cache
	 * @param otherEntities the other entities load from other source
	 * @return the list of entities
	 */
	private <S extends Serializable, T extends Entity<S>> 
			Collection<T> fillOtherEntitiesIn(Collection<T> entities, Collection<T> otherEntities) {
		entities.addAll(otherEntities);
		return entities;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadAll(T template) {
		List<T> list = cache.getList(template);
		if(list!=null&&!list.isEmpty()) return list; //get values from cache directly
		
		list = super.loadAll(template); //get values from databases.
		if(list!=null&&!list.isEmpty()) cache.setList(template, list);//push to cache.
		return list;
	}
}
