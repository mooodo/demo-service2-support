/* 
 * Created by 2019年1月25日
 */
package com.demo2.support.entity;

import java.util.List;
import java.util.Map;

/**
 * The result set of query.
 * @author fangang
 */
public class ResultSet {
	private List<?> data;
	private Integer page;
	private Integer size;
	private Long count;
	private Map<String, Object> aggregate;
	/**
	 * @return the data
	 */
	public List<?> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(List<?> data) {
		this.data = data;
	}
	/**
	 * @return the page
	 */
	public Integer getPage() {
		return page;
	}
	/**
	 * @param page the page to set
	 */
	public void setPage(Integer page) {
		this.page = page;
	}
	/**
	 * @return the size
	 */
	public Integer getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
	/**
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Long count) {
		this.count = count;
	}
	/**
	 * @return the aggregate
	 */
	public Map<String, Object> getAggregate() {
		return aggregate;
	}
	/**
	 * @param aggregate the aggregate to set
	 */
	public void setAggregate(Map<String, Object> aggregate) {
		this.aggregate = aggregate;
	}
	
}
