/*
 * created on Nov 30, 2009
 */
package com.demo2.support.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 利用URL读取文件资源的代理类
 * @author 范钢
 */
public class UrlResource implements Resource, ResourcePath {

	private static final Log log = LogFactory.getLog(UrlResource.class);
	private URL url;
	private Filter filter = null;
	
	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}
	
	/**
	 * Constructor for url
	 * @param url
	 * @throws FileNotFoundException 
	 */
	public UrlResource(URL url) throws FileNotFoundException {
		super();
		this.url = url;
		if(url==null){
			throw new FileNotFoundException("No url to be found!");
		}
		log.debug("loading "+this.getDescription());
	}
	
	/**
	 * 利用<code>url.openStream()</code>来读取一个文件
	 * @return InputStream
	 * @exception IOException
	 */
	public InputStream getInputStream() throws IOException {
		URL url = this.getUrl();
		if(url==null){
			throw new FileNotFoundException("No url to be found!");
		}
		Filter filter = this.getFilter();
		if(filter==null||!filter.isSatisfied(url.getFile())){
			return null;
		}
		return url.openStream();
	}
	
	/**
	 * 利用URL来读取某个目录下的所有文件。
	 * 如果这个URL是一个jar，则调用JarResource来读取文件；
	 * 如果这个URL是一个file，则调用FileResource来读取文件。
	 * @return Resource[]
	 * @exception IOException
	 */
	public Resource[] getResources() throws IOException {
		URL url = this.getUrl();
		if(url==null){
			throw new FileNotFoundException("No url to be found!");
		}
		if(this.isJarResource(url)){
			JarResource resource = new JarResource(url.openConnection());
			resource.setFilter(this.getFilter());
			return resource.getResources();
		}else{
			File file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
			FileResource resource = new FileResource(file);
			resource.setFilter(this.getFilter());
			return resource.getResources();
		}
	}
	
	private boolean isJarResource(URL url){
		String protecol = url.getProtocol();
		if("jar".equals(protecol)||"zip".equals(protecol)||"wsjar".equals(protecol)){
			return true;
		}else{
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#getDescription()
	 */
	public String getDescription() {
		if(this.getUrl()==null){
			return "UrlResource: [url:null]";
		}
		return (new StringBuffer("UrlResource:[file:"))
					.append(this.getUrl().getFile()).append(",protocol:")
					.append(this.getUrl().getProtocol())
					.append("]").toString();
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

	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#getFileName()
	 */
	public String getFileName() {
		if(this.getUrl()==null){return null;}
		return this.getUrl().getFile();
	}
}
