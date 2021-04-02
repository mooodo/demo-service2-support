package com.demo2.support.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class EntityUtilsTest {
	@Test
	public void testCreateEntity() {
		Product product = new Product();
		Product actual = EntityUtils.createEntity(product.getClass());
		assertNotNull(actual);
	}
	
	@Test
	public void testCloneEntity() {
		Supplier excepted = new Supplier(new Long(20001), "Alibaba");
		Supplier actual = (Supplier)excepted.clone();
		assertThat(actual,equalTo(excepted));
	}

	@Test
	public void testIsEntity() {
		assertTrue(EntityUtils.isEntity(Product.class));
	}

	@Test
	public void testCreateEntityWithNull() {
		Map<String, Object> json = null;
		Product actual = EntityUtils.createEntity(Product.class, json);
		assertNotNull(actual);
	}
	
	@Test
	public void testCreateEntityWithValues() {
		Map<String, Object> json = new HashMap<>();
		json.put("id", "40001");
		json.put("name", "computor");
		json.put("supplierId", "20001");
		json.put("createDate", "2020-01-01");
		json.put("updateDate", "2020-01-01 00:00:00");
		Product actual = EntityUtils.createEntity(Product.class, json);
		Product excepted = new Product(new Long(40001), "computor", new Long(20001), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"), 
				DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		assertThat(actual,equalTo(excepted));
	}
	
	@Test
	public void testSetValueToEntity() {
		Product actual = new Product();
		Product excepted = new Product(new Long(40001), "computor", new Long(20001), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"), 
				DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		EntityUtils.setValueToEntity(actual, "id", "40001");
		EntityUtils.setValueToEntity(actual, "name", "computor");
		EntityUtils.setValueToEntity(actual, "supplierId", "20001");
		EntityUtils.setValueToEntity(actual, "createDate", "2020-01-01");
		EntityUtils.setValueToEntity(actual, "updateDate", "2020-01-01 00:00:00");
		assertThat(actual, equalTo(excepted));
	}
	
	@Test
	public void testGetValueFromEntity() {
		Product actual = new Product(new Long(40001), "computor", new Long(20001),
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"), 
				DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		EntityUtils.getValueFromEntity(actual, "id");
		assertThat(EntityUtils.getValueFromEntity(actual, "id"), equalTo(new Long(40001)));
		assertThat(EntityUtils.getValueFromEntity(actual, "name"), equalTo("computor"));
		assertThat(EntityUtils.getValueFromEntity(actual, "supplierId"), equalTo(new Long(20001)));
		assertThat(EntityUtils.getValueFromEntity(actual, "createDate"), 
				equalTo(DateUtils.getDate("2020-01-01", "yyyy-MM-dd")));
		assertThat(EntityUtils.getValueFromEntity(actual, "updateDate"), 
				equalTo(DateUtils.getDate("2020-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss")));
	}
	
	@Test
	public void testBindEntity() {
		Supplier actual = EntityUtils.bindEntity(Supplier.class, "{id:20001, name:'Alibaba'}");
		assertThat(actual, equalTo(new Supplier(new Long(20001), "Alibaba")));
	}
	
	@Test
	public void testSetEntityToEntity() {
		Product actual = new Product();
		Product excepted = new Product();
		excepted.setSupplier(new Supplier(new Long(20001), "Alibaba"));
		EntityUtils.setValueToEntity(actual, "supplier", "{id:20001, name:'Alibaba'}");
		assertThat(actual, equalTo(excepted));
	}
	
	@Test
	public void testSetListToEntity() {
		Supplier actual = new Supplier();
		Supplier excepted = new Supplier();
		List<Product> products = new ArrayList<>();
		products.add(new Product(new Long(40001), "cup", new Long(20001), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"),
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd")));
		products.add(new Product(new Long(40002), "glass", new Long(20002), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"),
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd")));
		excepted.setProducts(products);
		
		Map<String, Object> map1 = new HashMap<>();
		map1.put("id", 40001);
		map1.put("name", "cup");
		map1.put("supplierId", 20001);
		map1.put("createDate", "2020-01-01");
		map1.put("updateDate", "2020-01-01");
		
		Map<String, Object> map2 = new HashMap<>();
		map2.put("id", 40002);
		map2.put("name", "glass");
		map2.put("supplierId", 20002);
		map2.put("createDate", "2020-01-01");
		map2.put("updateDate", "2020-01-01");
		
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map1);
		list.add(map2);
		
		EntityUtils.setValueToEntity(actual, "products", list);
		assertThat(actual, equalTo(excepted));
	}
	
	@Test
	public void testBindListOfEntity() {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("id", 40001);
		map1.put("name", "cup");
		map1.put("supplierId", 20001);
		map1.put("createDate", "2020-01-01");
		map1.put("updateDate", "2020-01-01");
		
		Map<String, Object> map2 = new HashMap<>();
		map2.put("id", 40002);
		map2.put("name", "glass");
		map2.put("supplierId", 20002);
		map2.put("createDate", "2020-01-01");
		map2.put("updateDate", "2020-01-01");
		
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map1);
		list.add(map2);
		
		Collection<Product> actual = EntityUtils.bindListOrSetOfEntity(Product.class, list);
		
		List<Product> excepted = new ArrayList<>();
		excepted.add(new Product(new Long(40001), "cup", new Long(20001), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"),
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd")));
		excepted.add(new Product(new Long(40002), "glass", new Long(20002), 
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd"),
				DateUtils.getDate("2020-01-01", "yyyy-MM-dd")));
		
		assertThat(actual, equalTo(excepted));
	}
}
