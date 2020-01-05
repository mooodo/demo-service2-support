/*
 * created on Nov 30, 2009
 */
package com.demo2.support.xml;

import java.io.IOException;

/**
 * 读取一个路径中所有文件资源的代理类
 * @author 范钢
 */
public interface ResourcePath {
	
	/**从一个路径中读取所有文件并封装到装载器中返回
	 * @return Array of Resource 装载器集合
	 * @throws IOException
	 */
	public Resource[] getResources() throws IOException;

	/**
	 * @param filter 文件过滤器
	 */
	public void setFilter(Filter filter);
	
	/**
	 * @return 文件过滤器
	 */
	public Filter getFilter();
}
