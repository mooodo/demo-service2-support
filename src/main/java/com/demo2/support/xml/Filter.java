/*
 * created on Dec 3, 2009
 */
package com.demo2.support.xml;

/**
 * 文件过滤器
 * @author 范钢
 */
public abstract class Filter {
	
	/**
	 * @param fileName 文件名
	 * @return 文件名是否满足过滤条件
	 */
	public abstract boolean isSatisfied(String fileName);
}
