package com.demo2.support.xml;

import java.io.IOException;

/**
 * The loader for reading resources.
 * @author fangang
 */
public interface ResourceLoader {

	/**
	 * @return the filter
	 */
	public Filter getFilter();

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(Filter filter);

	/**
	 * load the resource by the path.
	 * @param callback
	 * @param path
	 * @return whether loaded the resource success.
	 * @throws IOException
	 */
	public boolean loadResource(ResourceCallBack callback, String path) throws IOException;
	
	/**
	 * load the resource by one more paths.
	 * @param callback
	 * @param paths
	 * @return whether loaded the resource success.
	 * @throws IOException
	 */
	public boolean loadResource(ResourceCallBack callback, String... paths) throws IOException;
}