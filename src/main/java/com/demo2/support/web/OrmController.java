/* 
 * Created by 2019年1月24日
 */
package com.demo2.support.web;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo2.support.exception.OrmException;
import com.demo2.support.utils.DateUtils;

/**
 * The generate controller for CRUD operations by ORM
 * @author fangang
 */
@RestController
public class OrmController implements ApplicationContextAware {
	private ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * execute a bean's method by POST method. 
	 * NOTE: if the method has a value object, it must be the first parameter. 
	 * The other parameters must name like arg0, arg1, and so on. 
	 * @param beanName
	 * @param methodName
	 * @param request
	 * @return the returned value of the method
	 */
	@PostMapping("execute/{bean}/{method}")
	public Object execute(@PathVariable("bean")String beanName, @PathVariable("method")String methodName, 
			HttpServletRequest request) {
		Object service = getBean(beanName);
		Method method = getMethod(service, methodName);
		
		Map<String, String> json = new HashMap<String, String>();
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
	 * execute a bean's method by GET method. 
	 * NOTE: the method must has none of value object. 
	 * The parameters must name like arg0, arg1, and so on. 
	 * @param beanName
	 * @param methodName
	 * @param request
	 * @return the returned value of the method
	 */
	@GetMapping("get/{bean}/{method}")
	public Object get(@PathVariable("bean")String beanName, @PathVariable("method")String methodName, 
			HttpServletRequest request) {
		Object service = getBean(beanName);
		Method method = getMethod(service, methodName);
		
		Map<String, String> json = new HashMap<String, String>();
		for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			String value = request.getParameter(key);
			json.put(key, value);
		}
		Object[] args = getArguments(method, json, null);
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
		for(Method method : allOfMethods) {
			if(method.getName().equals(name)) return method;
		}
		throw new OrmException("No such method["+name+"] in the service["+service.getClass().getName()+"]");
	}
	
	/**
	 * get the first parameter of the method, as the value object, and instance, set values from the json.
	 * if the json is null or empty, means no value object, then return null.
	 * if there is no parameters of the method, then return null. 
	 * @param method
	 * @param json
	 * @return the value object with it's data.
	 */
	private Object getValueObj(Method method, Map<String, String> json) {
		if(json==null||json.isEmpty()) return null;
		Class<?>[] allOfParameterTypes = method.getParameterTypes();
		if(allOfParameterTypes.length==0) return null;
		Class<?> firstOfParameterType = allOfParameterTypes[0];
		if(!isValueObject(firstOfParameterType)) return null;
		try {
			Object vo = firstOfParameterType.newInstance();
			Field[] allOfFields = vo.getClass().getDeclaredFields();
			for(Field field : allOfFields) {
				field.setAccessible(true);
				Class<?> clazz = field.getType();
				String value = json.get(field.getName());
				if(value!=null) field.set(vo, bind(clazz, value));
				field.setAccessible(false);
			}
			return vo;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new OrmException("error when creating the value object by reflect", e);
		}
	}
	
	/**
	 * check a parameter whether is a value object.
	 * @param clazz
	 * @return yes or no
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	private boolean isValueObject(Class<?> clazz) {
		if(clazz==null) return false;
		if(Number.class.isAssignableFrom(clazz)) return false;
		if(String.class.isAssignableFrom(clazz)) return false;
		if(Date.class.isAssignableFrom(clazz)) return false;
		if(Collection.class.isAssignableFrom(clazz)) return false;
		return true;
	}
	
	/**
	 * Downcast the value to the class it is.
	 * @param clazz
	 * @param value
	 * @return the downcast value
	 */
	private Object bind(Class<?> clazz, String value) {
		if(value==null) return value;
		if(clazz.equals(String.class)) return value;
		if(clazz.equals(Long.class)||clazz.equals(long.class)) return new Long(value);
		if(clazz.equals(Integer.class)||clazz.equals(int.class)) return new Integer(value);
		if(clazz.equals(Double.class)||clazz.equals(double.class)) return new Double(value);
		if(clazz.equals(Float.class)||clazz.equals(float.class)) return new Float(value);
		if(clazz.equals(Short.class)||clazz.equals(short.class)) return new Short(value);
		
		if(clazz.equals(Date.class)&&value.length()==10) return DateUtils.getDate(value,"yyyy-MM-dd");
		if(clazz.equals(Date.class)) return DateUtils.getDate(value);
		
		//TODO how to bind map, list and set.
		return value;
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
	private Object[] getArguments(Method method, Map<String, String> json, Object vo) {
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
		Class<?>[] allOfParameterTypes = method.getParameterTypes();
		for( ; index<length; index++) {
			String name = allOfParameters[index].getName();//TODO can't get really name of args
			Class<?> clazz = allOfParameterTypes[index];
			Object value = bind(clazz, json.get(name));
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
			throw new OrmException("error when invoking the service by reflect", e);
		}
	}
}
