/*
 * created on Nov 30, 2009
 */
package com.demo2.support.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用File来读取文件资源的代理类
 * @author 范钢
 */
public class FileResource implements Resource, ResourcePath {

	private static final Log log = LogFactory.getLog(FileResource.class);
	private File file;
	private Filter filter = null;
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * Default constructor
	 */
	public FileResource() {
		super();
	}
	/**
	 * Constructor for file
	 * @param file
	 */
	public FileResource(File file) {
		super();
		this.file = file;
		log.debug("loading "+this.getDescription());
	}
	
	/**
	 * 以<code>new FileInputStream(file)</code>的形式读取文件，
	 * 如果文件是一个目录，则返回null。
	 * @return InputStream
	 * @exception IOException
	 */
	public InputStream getInputStream() throws IOException {
		File file = this.getFile();
		if(file==null||file.isDirectory()){
			return null;
		}
		Filter filter = this.getFilter();
		if(filter!=null&&!filter.isSatisfied(file.getName())){
			return null;
		}
		return new FileInputStream(file);
	}
	
	/**
	 * 以<code>File.listFiles()</code>的形式获取一个目录下的所有文件
	 * @return Resource[]
	 * @exception IOException
	 */
	public Resource[] getResources() throws IOException {
		File file = this.getFile();
		if(file==null){return null;}
		if(file.isDirectory()){
			File[] files = file.listFiles();
			Filter filter = this.getFilter();
			List<FileResource> fileLoaders = new ArrayList<FileResource>();
			for(int i=0; i<files.length; i++){
				if(filter!=null&&!filter.isSatisfied(files[i].getName())){continue;}
				fileLoaders.add(new FileResource(files[i]));
			}
			return (Resource[])fileLoaders.toArray(new Resource[fileLoaders.size()]);
		}
		Resource resource = new FileResource(file);
		resource.setFilter(this.getFilter());
		return new Resource[]{resource};
	}
	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#getDescription()
	 */
	public String getDescription() {
		return (new StringBuffer("FileResource:[file:"))
					.append(this.getFile()).append("]").toString();
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
		if(this.getFile()==null){return null;}
		String fileName = this.getFile().getName();
		if(fileName.regionMatches(0, "\\", 0, 2))
			fileName = fileName.replaceAll("\\", "/");
		return fileName;
	}
}
