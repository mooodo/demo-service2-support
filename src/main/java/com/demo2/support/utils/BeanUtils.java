/*
 * created by 2019年7月23日 上午10:11:43
 */
package com.demo2.support.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;

/**
 * The utility for the bean.
 * @author fangang
 */
public class BeanUtils {
	/**
	 * get the value from a bean by field name.
	 * @param bean
	 * @param fieldName
	 * @return the value
	 */
	public static Object getValueByField(Object bean, String fieldName) {
		if(bean==null||fieldName==null) return null;
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			boolean isAccessible = field.isAccessible();
			if(!isAccessible) field.setAccessible(true);
			Object value = field.get(bean);
			field.setAccessible(isAccessible);
			return value;
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			throw new OrmException("error when get value from the bean[bean:"+bean+",field:"+fieldName+"]", e);
		}
	}
	
	/**
	 * set the value to the bean by the field name.
	 * @param bean
	 * @param fieldName
	 * @param value
	 */
	public static void setValueByField(Object bean, String fieldName, Object value) {
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			boolean isAccessible = field.isAccessible();
			if(!isAccessible) field.setAccessible(true);
			field.set(bean, value);
			field.setAccessible(isAccessible);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new OrmException("error when set the value to the bean", e);
		}
	}
	
	/**
	 * set the value to the bean by the field name.
	 * @param bean
	 * @param fieldName
	 * @param callback
	 */
	public static void setValueByField(Object bean, String fieldName, BeanCallback callback) {
		try {
			Field field = bean.getClass().getDeclaredField(fieldName);
			Type type = field.getGenericType();
			Object value = callback.apply(type);
			
			setValueByField(bean, fieldName, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
			throw new OrmException("error when set the value to the bean", e);
		}
	}
	
	@FunctionalInterface
	public interface BeanCallback {
		public Object apply(Type type);
	}
	
	/**
	 * Downcast the value to the class it should be.
	 * @param type the type that the value should be
	 * @param value
	 * @return the downcast value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object bind(Type type, Object value) {
		if(value==null) return value;
		if(type instanceof Class) {
			Class<?> clazz = (Class<?>)type;
			if(clazz.equals(String.class)) return value;
			
			String str = value.toString();
			if(clazz.equals(Long.class)||clazz.equals(long.class)) return new Long(str);
			if(clazz.equals(Integer.class)||clazz.equals(int.class)) return new Integer(str);
			if(clazz.equals(Double.class)||clazz.equals(double.class)) return new Double(str);
			if(clazz.equals(Float.class)||clazz.equals(float.class)) return new Float(str);
			if(clazz.equals(Short.class)||clazz.equals(short.class)) return new Short(str);
			
			if(clazz.equals(Date.class)&&str.length()==10) return DateUtils.getDate(str,"yyyy-MM-dd");
			if(clazz.equals(Date.class)&&str.length()==24) return DateUtils.getDateForUTC(str);
			if(clazz.equals(Date.class)) return DateUtils.getDate(str,"yyyy-MM-dd HH:mm:ss");
			
			if(clazz.equals(List.class)||clazz.equals(Set.class)) {
				List<String> listOfStr = Arrays.asList(str.split(","));
				return (clazz.equals(List.class)) ? listOfStr : new HashSet<String>(listOfStr);
			}
			
			if(EntityUtils.isEntity(clazz)) 
				return EntityUtils.bindEntity((Class<Entity>)clazz, str);
			
			//TODO do nothing with other types
		} else if(type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType)type;
			Class<?> clazz = (Class<?>)pt.getRawType();
			if(clazz.equals(List.class)||clazz.equals(Set.class)) {
				return bindListOrSet(pt, value);
			}
			//TODO do nothing with map or other types.
		}
		return value;
	}
	
	/**
	 * Downcast the value to the parameterized list or set it should be.
	 * @param pt ParameterizedType
	 * @return the downcast value
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object bindListOrSet(ParameterizedType pt, Object value) {
		Class<?> clazz = (Class<?>)pt.getRawType();
		List<String> listOfStr = Arrays.asList(value.toString().split(","));
		Type ata = pt.getActualTypeArguments()[0];
		if(ata instanceof Class) {
			Class<?> ataClazz = (Class<?>)ata;
			if(ataClazz.equals(String.class)) 
				return convert(listOfStr, clazz, s->{return s;});
			if(ataClazz.equals(Long.class)||clazz.equals(long.class)) 
				return convert(listOfStr, clazz, s->{return new Long(s);});
			if(ataClazz.equals(Integer.class)||clazz.equals(int.class)) 
				return convert(listOfStr, clazz, s->{return new Integer(s);});
			if(ataClazz.equals(Double.class)||clazz.equals(double.class)) 
				return convert(listOfStr, clazz, s->{return new Double(s);});
			if(ataClazz.equals(Float.class)||clazz.equals(float.class)) 
				return convert(listOfStr, clazz, s->{return new Float(s);});
			if(ataClazz.equals(Short.class)||clazz.equals(short.class)) 
				return convert(listOfStr, clazz, s->{return new Short(s);});
			
			if(ataClazz.equals(Date.class)&&listOfStr.get(0).length()==10)
				return convert(listOfStr, clazz, s->{return DateUtils.getDate(s,"yyyy-MM-dd");});
			if(ataClazz.equals(Date.class))
				return convert(listOfStr, clazz, s->{return DateUtils.getDate(s,"yyyy-MM-dd HH:mm:ss");});
			
			if(EntityUtils.isEntity(ataClazz))
				return EntityUtils.bindListOrSetOfEntity((Class<Entity>)ataClazz, value);
		} else {
			//TODO do nothing other types.
		}
		return value;
	}
	
	/**
	 * convert list of String to Collection<T>
	 * @param listOfStr
	 * @param clazz
	 * @param instance
	 * @return Collection<T>
	 */
	private static<T> Collection<T> convert(List<String> listOfStr, Class<?> clazz,
			NewInstance<T> instance) {
		Collection<T> c = (clazz.equals(List.class)) ? new ArrayList<T>() : new HashSet<T>();
		for(String str : listOfStr) c.add(instance.apply(str));
		return c;
	}
	
	@FunctionalInterface
	interface NewInstance<T> {
		T apply(String s);
	}
	
	/**
	 * get the method of the service by name, using reflect.
	 * @param service
	 * @param methodName the name of the method
	 * @return the reference of the method
	 */
	public static Method getMethodIfExists(Object service, String methodName) {
		if(methodName==null||methodName.isEmpty()) throw new OrmException("The method name is empty!");
		Method[] allOfMethods = service.getClass().getDeclaredMethods();
		Method rtn = null;
		for(Method method : allOfMethods) {
			if(method.getName().equals(methodName)) 
				if(rtn==null||(rtn.getParameterTypes().length>0&&rtn.getParameterTypes()[0].isAssignableFrom(method.getParameterTypes()[0]))) rtn = method;
		}
		return rtn; //if have override, return the last one.
	}
	
	/**
	 * get the method of the service by name, using reflect.
	 * @param service
	 * @param methodName the name of the method
	 * @return the reference of the method
	 */
	public static Method getMethod(Object service, String methodName) {
		Method rtn = getMethodIfExists(service, methodName);
		if(rtn!=null) return rtn; //if have override, return the last one.
		throw new OrmException("No such method["+methodName+"] in the Object["+service.getClass().getName()+"]");
	}
}
