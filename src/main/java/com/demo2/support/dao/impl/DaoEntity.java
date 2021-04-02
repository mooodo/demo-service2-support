/*
 * Created by 2020-12-04 12:41:48 
 */
package com.demo2.support.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fangang
 */
public class DaoEntity {
	private String tableName;
	private List<Object> columns = new ArrayList<>();
	private List<Object> values = new ArrayList<>();
	//colMap: a list of conditions like: [{key:"col0",opt:'=',value:"val0"},{key:"col1",opt:'=',value:"val1"}]
	private List<Map<Object, Object>> colMap = new ArrayList<>();
	private List<Map<Object, Object>> pkMap = new ArrayList<>();
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the columns
	 */
	public List<Object> getColumns() {
		return columns;
	}
	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Object> columns) {
		this.columns = columns;
	}
	/**
	 * @return the values
	 */
	public List<Object> getValues() {
		return values;
	}
	/**
	 * @param values the values to set
	 */
	public void setValues(List<Object> values) {
		this.values = values;
	}
	/**
	 * @return the colMap
	 */
	public List<Map<Object, Object>> getColMap() {
		return colMap;
	}
	/**
	 * @param colMap the colMap to set
	 */
	public void setColMap(List<Map<Object, Object>> colMap) {
		this.colMap = colMap;
	}
	/**
	 * @return the pkMap
	 */
	public List<Map<Object, Object>> getPkMap() {
		return pkMap;
	}
	/**
	 * @param pkMap the pkMap to set
	 */
	public void setPkMap(List<Map<Object, Object>> pkMap) {
		this.pkMap = pkMap;
	}
}
