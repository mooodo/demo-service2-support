/**
 * 
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.util.List;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;
import com.demo2.support.utils.BeanUtils;

/**
 * The generic ddd factory to load and assemble domain objects together, 
 * according to vObj.xml 
 * @author fangang
 */
public class GenericEntityFactory {
	private Join join;
	private Entity vo;
	private BasicDao dao;

	/**
	 * load and assemble domain objects together.
	 * @param join the join between domain objects.
	 * @param vo the value object
	 * @param dao the data access object
	 */
	public void build(Join join, Entity vo, BasicDao dao) {
		this.join = join;
		this.vo = vo;
		this.dao = dao;
		
		String joinType = join.getJoinType();
		Entity template;
		if("oneToOne".equals(joinType)) {
			template = loadOfOneToOne(join, vo);
			setValueOfJoinToVo(template);
			return;
		}
		if("manyToOne".equals(joinType)) {
			template = loadOfManyToOne(join, vo);
			setValueOfJoinToVo(template);
			return;
		}
		if("oneToMany".equals(joinType)) {
			List<Entity> list = loadOfOneToMany(join, vo);
			setValueOfJoinToVo(list);
			return;
		}
		if("manyToMany".equals(joinType)) {
			throw new OrmException("Don't support the many to many relation now!");
		}
	}
	
	/**
	 * @param join
	 * @param vo
	 * @return
	 */
	private Entity loadOfOneToOne(Join join, Entity vo) {
		Serializable id = vo.getId();
		String clazz = join.getClazz();
		Entity template = BeanUtils.createEntity(clazz);
		template.setId(id);
		return dao.load(id, template);
	}
	
	/**
	 * load data of the many to one relation.
	 * @param join
	 * @param vo
	 * @return
	 */
	private Entity loadOfManyToOne(Join join, Entity vo) {
		String joinKey = join.getJoinKey();
		Serializable id = (Serializable)BeanUtils.getValueByField(vo, joinKey);
		String clazz = join.getClazz();
		Entity template = BeanUtils.createEntity(clazz);
		template.setId(id);
		return dao.load(id, template);
	}
	
	/**
	 * get value of the join.
	 * @return a list of entity.
	 */
	private List<Entity> loadOfOneToMany(Join join, Entity vo) {
		Serializable id = vo.getId();
		String clazz = join.getClazz();
		Entity template = BeanUtils.createEntity(clazz);
		String joinKey = join.getJoinKey();
		BeanUtils.setValueByField(template, joinKey, id);
		return dao.loadAll(template);
	}
	
	/**
	 * set value of the join to the value object.
	 * @param list the list of value.
	 */
	private void setValueOfJoinToVo(Entity template) {
		String name = join.getName();
		BeanUtils.setValueByField(vo, name, template);
	}
	
	/**
	 * set value of the join to the value object.
	 * @param list the list of value.
	 */
	private void setValueOfJoinToVo(List<Entity> list) {
		String name = join.getName();
		BeanUtils.setValueByField(vo, name, list);
	}
}
