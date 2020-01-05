/* 
 * Created by 2019年1月24日
 */
package com.demo2.support.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo2.support.entity.ResultSet;
import com.demo2.support.exception.QueryException;
import com.demo2.support.service.QueryService;

/**
 * The generate query controller
 * @author fangang
 */
@RestController
public class QueryController implements ApplicationContextAware {
	private ApplicationContext applicationContext = null;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@PostMapping("query/{bean}")
	public ResultSet query(@PathVariable("bean")String beanName, HttpServletRequest request) {
		QueryService service = getBean(beanName);
		
		Map<String, Object> params = new HashMap<>();
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			String value = request.getParameter(key);
			params.put(key, value);
		}
		return service.query(params);
	}
	
	/**
	 * get bean in the spring context by name.
	 * @param name the bean name
	 * @return the instance of the bean
	 */
	private QueryService getBean(String name) {
		if(name==null||name.isEmpty()) throw new QueryException("The bean name is empty!");
		try {
			return (QueryService)applicationContext.getBean(name);
		} catch (NoSuchBeanDefinitionException e) {
			throw new QueryException("No such bean definition in the spring context!", e);
		} catch (BeansException e) {
			throw new QueryException("error when get the bean["+name+"]");
		}
	}
}
