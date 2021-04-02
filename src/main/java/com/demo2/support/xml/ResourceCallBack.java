/*
 * Created by 2020-06-25 14:15:32 
 */
package com.demo2.support.xml;

import java.io.InputStream;

/**
 * The callback function for reading resources.
 * @author fangang
 */
@FunctionalInterface
public interface ResourceCallBack {
	/**
	 * @param inputStream
	 */
	void apply(InputStream inputStream);
}
