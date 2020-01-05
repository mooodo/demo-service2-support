/*
 * created on 2009-11-16 
 */
package com.demo2.support.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 以DOM的方式读取和解析XML文件，并创建工厂的外观模型。
 * 它通过调用initFactory()，从指定的一个或多个XML文件中读取数据，
 * 然后将读取出的数据装载到一个工厂中。
 * <p>该类的所有继承类必须通过对buildFactory()的实现，
 * 具体定义如何将读取出的数据装载到一个工厂中。
 * @author FanGang
 */
public abstract class XmlBuildFactoryTemplate {
	private static final Log log = LogFactory.getLog(XmlBuildFactoryTemplate.class);
	private boolean validating = false;
	private boolean namespaceAware = false;
	private Filter filter = null;
	private String[] paths = null;
	
	/**
	 * 确定在解析XML创建工厂时，是否提供对 XML 名称空间支持的解析器
	 * @return the namespaceAware
	 */
	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	/**
	 * 指定由此代码生成的解析器将提供对 XML 名称空间的支持
	 * @param namespaceAware the namespaceAware to set
	 */
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

	/**
	 * 确定在解析XML创建工厂时，是否解析器在解析时验证 XML 内容。
	 * @return the validating
	 */
	public boolean isValidating() {
		return validating;
	}

	/**
	 * 指定由此代码生成的解析器将验证被解析的 XML 文档
	 * @param validating the validating to set
	 */
	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	/**
	 * 获取一个文件过滤器来过滤哪些应对被过滤出来
	 * 默认的过滤器是将*.xml和*.XML文件过滤出来
	 * @return the filter 文件过滤器
	 */
	public Filter getFilter() {
		if(filter==null){
			filter = new Filter(){

				public boolean isSatisfied(String fileName) {
					if(fileName.endsWith(".xml")||fileName.endsWith(".XML")){return true;}
					else {return false;}
				}};
		}
		return filter;
	}

	/**
	 * 提供一个文件过滤器来过滤文件
	 * @param filter the filter to set
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * 初始化工厂。根据路径读取XML文件，将XML文件中的数据装载到工厂中
	 * @param path XML的路径
	 */
	public void initFactory(String path){
		if(findOnlyOneFileByClassPath(path)){return;}
		if(findResourcesByUrl(path)){return;}
		if(findResourcesByFile(path)){return;}
		this.paths = new String[]{path};
	}
	
	/**
	 * 初始化工厂。根据路径列表依次读取XML文件，将XML文件中的数据装载到工厂中
	 * @param paths 路径列表
	 */
	public void initFactory(String[] paths){
		for(int i=0; i<paths.length; i++){
			initFactory(paths[i]);
		}
		this.paths = paths;
	}
	
	/**
	 * 重新初始化工厂，初始化所需的参数，为上一次初始化工厂所用的参数。
	 */
	public void reloadFactory(){
		initFactory(this.paths);
	}
	
	/**
	 * 采用ClassLoader的方式试图查找一个文件，并调用<code>readXmlStream()</code>进行解析
	 * @param path XML文件的路径
	 * @return 是否成功
	 */
	protected boolean findOnlyOneFileByClassPath(String path){
		boolean success = false;
		try {
			Resource resource = new ClassPathResource(path, this.getClass());
			resource.setFilter(this.getFilter());
			InputStream is = resource.getInputStream();
			if(is==null){return false;}
			readXmlStream(is);
			success = true;
		} catch (SAXException e) {
			log.debug("Error when findOnlyOneFileByClassPath:"+path,e);
		} catch (IOException e) {
			log.debug("Error when findOnlyOneFileByClassPath:"+path,e);
		} catch (ParserConfigurationException e) {
			log.debug("Error when findOnlyOneFileByClassPath:"+path,e);
		}
		return success;
	}
	
	/**
	 * 采用URL的方式试图查找一个目录中的所有XML文件，并调用<code>readXmlStream()</code>进行解析
	 * @param path XML文件的路径
	 * @return 是否成功
	 */
	protected boolean findResourcesByUrl(String path){
		boolean success = false;
		try {
			ResourcePath resourcePath = new PathMatchResource(path, this.getClass());
			resourcePath.setFilter(this.getFilter());
			Resource[] loaders = resourcePath.getResources();
			for(int i=0; i<loaders.length; i++){
				InputStream is = loaders[i].getInputStream();
				if(is!=null){
					readXmlStream(is);
					success = true;
				}
			}
		} catch (SAXException e) {
			log.debug("Error when findResourcesByUrl:"+path,e);
		} catch (IOException e) {
			log.debug("Error when findResourcesByUrl:"+path,e);
		} catch (ParserConfigurationException e) {
			log.debug("Error when findResourcesByUrl:"+path,e);
		}
		return success;
	}
	
	/**
	 * 用File的方式试图查找文件，并调用<code>readXmlStream()</code>解析
	 * @param path XML文件的路径
	 * @return 是否成功
	 */
	protected boolean findResourcesByFile(String path){
		boolean success = false;
		FileResource loader = new FileResource(new File(path));
		loader.setFilter(this.getFilter());
		try {
			Resource[] loaders = loader.getResources();
			if(loaders==null){return false;}
			for(int i=0; i<loaders.length; i++){
				InputStream is = loaders[i].getInputStream();
				if(is!=null){
					readXmlStream(is);
					success = true;
				}
			}
		} catch (IOException e) {
			log.debug("Error when findResourcesByFile:"+path,e);
		} catch (SAXException e) {
			log.debug("Error when findResourcesByFile:"+path,e);
		} catch (ParserConfigurationException e) {
			log.debug("Error when findResourcesByFile:"+path,e);
		}
		return success;
	}

	/**
	 * 读取并解析一个XML的文件输入流，以Element的形式获取XML的根，
	 * 然后调用<code>buildFactory(Element)</code>构建工厂
	 * @param inputStream 文件输入流
	 * @throws SAXException 抛出采用SAX技术解析XML文件过程中出现的异常
	 * @throws IOException 抛出文件读写操作过程中出现的异常
	 * @throws ParserConfigurationException 抛出粘贴XML数据流过程中出现的异常
	 */
	protected void readXmlStream(InputStream inputStream) throws SAXException, IOException, ParserConfigurationException{
		if(inputStream==null){
			throw new ParserConfigurationException("Cann't parse source because of InputStream is null!");
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(this.isValidating());
        factory.setNamespaceAware(this.isNamespaceAware());
        DocumentBuilder build = factory.newDocumentBuilder();
        Document doc = build.parse(new InputSource(inputStream));
        Element root = doc.getDocumentElement();
        buildFactory(root);
	}
	
	/**
	 * 用从一个XML的文件中读取的数据构建工厂
	 * @param root 从一个XML的文件中读取的数据的根
	 */
	protected abstract void buildFactory(Element root);
	
}
