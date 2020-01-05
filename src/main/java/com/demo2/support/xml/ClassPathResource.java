/*
 * created on Nov 30, 2009
 */
package com.demo2.support.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用ClassLoader来读取文件资源的代理类
 * @author 范钢
 */
public class ClassPathResource implements Resource, ResourcePath {

	private static final Log log = LogFactory.getLog(ClassPathResource.class);
	protected static final String CLASSPATH_URL_PREFIX = "classpath:";
	private String filename;
	private Class<?> clazz;
	private ClassLoader classLoader;
	private Filter filter = null;
	/**
	 * @return the filename
	 */
	public String getFileName() {
		return filename;
	}
	/**
	 * @param location the location to set
	 */
	public void setFileName(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the clazz
	 */
	public Class<?> getClazz() {
		return clazz;
	}
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	/**
	 * @return the classLoader
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}
	/**
	 * @param classLoader the classLoader to set
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	/**
	 * Default constructor
	 */
	public ClassPathResource(){
		
	}
	
	/**
	 * Constructor for class
	 * @param filename
	 * @param clazz
	 */
	public ClassPathResource(String filename, Class<?> clazz) {
		super();
		this.filename = filename;
		this.clazz = clazz;
		this.classLoader = clazz.getClassLoader();
		log.debug("loading "+this.getDescription());
	}
	
	/**
	 * Constructor for ClassLoader
	 * @param filename
	 * @param classLoader
	 */
	public ClassPathResource(String filename, ClassLoader classLoader) {
		super();
		this.filename = filename;
		this.classLoader = classLoader;
	}
	
	/**
	 * 采用<code>ClassLoader.getResourceAsStream(filename)</code>来读取文件，
	 * 这里的文件名可以写为诸如："/WEB-INF/context.xml"，或"classpath:com/context.xml"
	 * @return InputStream
	 * @exception IOException
	 */
	public InputStream getInputStream() throws IOException {
		String filename = this.getFileName();
		Filter filter = this.getFilter();
		if(filter!=null&&!filter.isSatisfied(filename)){
			return null;
		}
		if(filename.startsWith(CLASSPATH_URL_PREFIX)){
			filename = "/"+filename.substring(CLASSPATH_URL_PREFIX.length());
		}
		InputStream is = null;
		if(this.getClazz()!=null){
			is = this.getClazz().getResourceAsStream(filename);
		}else if(this.getClassLoader()!=null){
			is = this.getClassLoader().getResourceAsStream(filename);
		}
		if(is==null){
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		}
		if(is==null){
			throw new FileNotFoundException("Cannot Loader this file by ClassLoader: "+filename);
		}
		return is;
	}
	
	/**
	 * 运用<code>ClassLoader.getResources(filename)</code>的方式获取某路径下的所有文件，
	 * 这里的路径可以写为诸如："/WEB-INF/"，或"classpath:com/"
	 * @return Resource[] 
	 * @throws IOException
	 */
	public Resource[] getResources() throws IOException{
		String filename = this.getFileName();
		if(filename==null){return null;}
		if(filename.startsWith(CLASSPATH_URL_PREFIX)){
			filename = filename.substring(CLASSPATH_URL_PREFIX.length());
		}
		if(filename.startsWith("/")){
			filename = filename.substring(1);
		}
		Enumeration<URL> en = null;
		if(this.getClazz()!=null){
			en = this.getClazz().getClassLoader().getResources(filename);
		}else if(this.getClassLoader()==null){
			en = this.getClassLoader().getResources(filename);
		}
		if(en==null||!en.hasMoreElements()){
			en = Thread.currentThread().getContextClassLoader().getResources(filename);
		}
		if(en==null||!en.hasMoreElements()){
			throw new FileNotFoundException();
		}
		
		List<Resource> xmlResourceLoaders = new ArrayList<Resource>();
		while(en.hasMoreElements()){
			URL url = (URL)en.nextElement();
			UrlResource resource = new UrlResource(url);
			resource.setFilter(this.getFilter());
			Resource[] loaders = resource.getResources();
			if(loaders!=null){
				xmlResourceLoaders.addAll(Arrays.asList(loaders));
			}
		}
		return (Resource[])xmlResourceLoaders.toArray(new Resource[xmlResourceLoaders.size()]);
	}
	
	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#getDescription()
	 */
	public String getDescription() {
		StringBuffer buff = new StringBuffer();
		if(this.getClazz()!=null){
			buff.append("ClassPathResource:[filename:")
				.append(this.getFileName()).append(",class:")
				.append(this.getClazz()).append("]");
		}else if(this.getClassLoader()!=null){
			buff.append("ClassPathResource:[filename:")
				.append(this.getFileName()).append(",classLoader:")
				.append(this.getClassLoader()).append("]");
		}
		return buff.toString();
	}
	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#getFilter()
	 */
	public Filter getFilter() {
		return this.filter;
	}
	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#setFilter(com.htxx.taglib.xml.Filter)
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
}
