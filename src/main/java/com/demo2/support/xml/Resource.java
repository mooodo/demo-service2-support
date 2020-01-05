/*
 * created on Nov 30, 2009
 */
package com.demo2.support.xml;

import java.io.IOException;
import java.io.InputStream;


/**
 * 读取一个文件资源的代理类接口
 * @author 范钢
 */
public interface Resource {
	/**
	 * 从一个文件中读取并返回一个InputStream
	 * @return InputStream
	 * @throws IOException 
	 */
	public InputStream getInputStream()throws IOException;
	
	/**
	 * @return 文件资源的相关信息，便于调试和跟踪
	 */
	public String getDescription();

	/**
	 * @param filter 文件过滤器
	 */
	public void setFilter(Filter filter);
	
	/**
	 * @return 文件过滤器
	 */
	public Filter getFilter();
	
	/**
	 * @return 文件资源的完整文件名（包括路径）
	 */
	public String getFileName();
}
