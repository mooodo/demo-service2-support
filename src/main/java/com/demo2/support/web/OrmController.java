/* 
 * Created by 2019年1月24日
 */
package com.demo2.support.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;
import com.demo2.support.utils.BeanUtils;
import com.demo2.support.utils.EntityUtils;

/**
 * The generic controller for CRUD operations by ORM
 * @author fangang
 */
@RestController
public class OrmController {
	@Autowired
	private ApplicationContext applicationContext = null;
	
	/**
	 * execute a bean's method by POST method. 
	 * NOTE: if the method has a value object, it must be the first parameter. 
	 * The other parameters must name like arg0, arg1, and so on. 
	 * @param beanName
	 * @param methodName
	 * @param request
	 * @return the returned value of the method
	 */
	@RequestMapping(value="orm/{bean}/{method}", method= {RequestMethod.GET, RequestMethod.POST})
	public Object execute(@PathVariable("bean")String beanName, @PathVariable("method")String methodName, 
			@RequestBody(required=false) Map<String, Object> json, HttpServletRequest request) {
		Object service = getBean(beanName);
		Method method = getMethod(service, methodName);
		
		if(json==null) json = new HashMap<>();
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			String value = request.getParameter(key);
			json.put(key, value);
		}
		
		Object vo = getValueObj(method, json);
		Object[] args = getArguments(method, json, vo);
		return invoke(service, method, args);
	}
	
	/**
	 * get bean in the spring context by name.
	 * @param name the bean name
	 * @return the instance of the bean
	 */
	private Object getBean(String name) {
		if(name==null||name.isEmpty()) throw new OrmException("The bean name is empty!");
		try {
			return applicationContext.getBean(name);
		} catch (NoSuchBeanDefinitionException e) {
			throw new OrmException("No such bean definition in the spring context!", e);
		} catch (BeansException e) {
			throw new OrmException("error when get the bean["+name+"]");
		}
	}
	
	/**
	 * get the method of the service by name, using reflect.
	 * @param service
	 * @param name the name of the method
	 * @return the reference of the method
	 */
	private Method getMethod(Object service, String name) {
		if(name==null||name.isEmpty()) throw new OrmException("The method name is empty!");
		Method[] allOfMethods = service.getClass().getDeclaredMethods();
		Method rtn = null;
		for(Method method : allOfMethods) {
			if(method.getName().equals(name)) rtn = method;
		}
		if(rtn!=null) return rtn; //if it has an override, return the last one.
		throw new OrmException("No such method["+name+"] in the service["+service.getClass().getName()+"]");
	}
	
	/**
	 * get the first parameter of the method, as the value object, and instance, set values from the json.
	 * if the json is null or empty, means no value object, then return null.
	 * if there is no parameters of the method, then return null. 
	 * @param method
	 * @param json
	 * @return the value object with its data.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getValueObj(Method method, Map<String, Object> json) {
		if(json==null||json.isEmpty()) return null;
		Class<?>[] allOfParameterTypes = method.getParameterTypes();
		if(allOfParameterTypes.length==0) return null;
		Class<?> firstOfParameterType = allOfParameterTypes[0];
		
		if(!EntityUtils.isEntity(firstOfParameterType)) return null;
		return EntityUtils.createEntity((Class<Entity>)firstOfParameterType, json);
	}
	
	/**
	 * get the arguments that invoke the method need. 
	 * The first thing is put the value object to the first argument, if it's available. 
	 * Then put the other arguments in order. 
	 * @param method
	 * @param json
	 * @param vo
	 * @return the arguments
	 */
	private Object[] getArguments(Method method, Map<String, Object> json, Object vo) {
		int length = method.getParameterCount();
		if(length==0) return null;
		int index = 0;
		List<Object> args = new ArrayList<Object>();
		
		//add the value object to the first argument, if it's available.
		if(vo!=null) {
			args.add(vo);
			index++;
		}
		
		//add the other arguments in order.
		Parameter[] allOfParameters = method.getParameters();
		for( ; index<length; index++) {
			Parameter parameter = allOfParameters[index];
			String name = parameter.getName();
			Object value = BeanUtils.bind(parameter.getParameterizedType(), json.get(name));
			args.add(value);
		}
		return args.toArray();
	}
	
	/**
	 * invoke the method of the service using reflect.
	 * if the value object is available, it must be the first argument.
	 * @param service the service
	 * @param method the method
	 * @param args the other arguments
	 * @return the result after invoking.
	 */
	private Object invoke(Object service, Method method, Object[] args) {
		try {
			if(args==null) return method.invoke(service);
			else return method.invoke(service, args);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new OrmException("error when invoking the service by reflect [service: "
					+service+", method: "+method+", args: "+args+"]", e);
		}
	}
}
