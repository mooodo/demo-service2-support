/*
 * Created by 2021-01-04 11:50:54 
 */
package com.demo2.support.utils;

import java.util.Date;

import com.demo2.support.entity.Entity;

/**
 * @author fangang
 */
public class Product extends Entity<Long> {
	private static final long serialVersionUID = -3559091149080538354L;
	private Long id;
	private String name;
	private Long supplier_id;
	private Date create_time;
	private Date update_time;
	private Supplier supplier;
	
	public Product() {
		super();
	}
	
	public Product(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Product(Long id, String name, Long supplierId, Date createDate, Date updateDate) {
		super();
		this.id = id;
		this.name = name;
		this.supplier_id = supplierId;
		this.create_time = createDate;
		this.update_time = updateDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the supplierId
	 */
	public Long getSupplierId() {
		return supplier_id;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(Long supplierId) {
		this.supplier_id = supplierId;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return create_time;
	}

	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.create_time = createDate;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return update_time;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.update_time = updateDate;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

}
