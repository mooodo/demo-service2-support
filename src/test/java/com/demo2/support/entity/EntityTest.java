/* 
 * create by 2020年1月22日 下午2:33:28
 */
package com.demo2.support.entity;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Date;

import org.junit.Test;

import com.demo2.support.entity.Entity;
import com.demo2.support.utils.DateUtils;

/**
 * Test for class {@link com.demo2.support.entity.Entity}
 * @author fangang
 */
public class EntityTest {

	/**
	 * The test show: 
	 * all of the value object which extends {@link com.demo2.support.entity.Entity} 
	 * can use method equals() to compare each other. 
	 * The compare is true when both of them contain same data, instead of same instance. 
	 * It helps us to test. 
	 */
	@Test
	public void testEqualsObject() {
		Person actual = new Person(1,"John",DateUtils.getDate("2020-01-01 00:00:00","YYYY-MM-DD hh:mm:ss"),3);
		Person excepted =  new Person(1,"John",DateUtils.getDate("2020-01-01 00:00:00","YYYY-MM-DD hh:mm:ss"),3);
		actual.equals(excepted);
		assertThat(actual, equalTo(excepted));
	}
	
	@Test
	public void testToString() {
		Person person = new Person(1,"John",DateUtils.getDate("2020-01-01 00:00:00","YYYY-MM-DD hh:mm:ss"),3);
		String actual = person.toString();
		String excepted = "{id:1, name:John, birthday:Sun Dec 29 00:00:00 CST 2019, children:3}";
		assertThat(actual, equalTo(excepted));
	}

	@SuppressWarnings("unused")
	private class Person extends Entity<Long> {
		private static final long serialVersionUID = 1L;
		private long id;
		private String name;
		private Date birthday;
		private int children;
		public Person(long id, String name, Date birthday, int children) {
			this.id = id;
			this.name = name;
			this.birthday = birthday;
			this.children = children;
		}
		@Override
		public Long getId() {
			return id;
		}
		@Override
		public void setId(Long id) {
			this.id = id;
		}
	}
}
