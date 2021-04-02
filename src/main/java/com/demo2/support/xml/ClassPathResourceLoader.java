/*
 * Created by 2020-06-25 13:13:31 
 */
package com.demo2.support.xml;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * load resources with class local path.
 * @author fangang
 */
public class ClassPathResourceLoader 
						extends AbstractResourceLoader implements ResourceLoader {
	private static Log log = LogFactory.getLog(ClassPathResourceLoader.class);
	private Class<?> clazz = this.getClass();
	
	public ClassPathResourceLoader() { super(); }
	
	/**
	 * @param clazz the class to load resource
	 */
	public ClassPathResourceLoader(Class<?> clazz) {
		if(clazz!=null) this.clazz = clazz;
	}
	@Override
	public boolean loadResource(ResourceCallBack callback, String path) throws IOException{
		boolean success = false;
		Resource resource = new ClassPathResource(path, clazz);
		log.debug(resource.getFile().getCanonicalFile());
		InputStream is = resource.getInputStream();
		if(is==null){return false;}
		callback.apply(is);
		success = true;
		return success;
	}
}
