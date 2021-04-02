/*
 * created on Nov 30, 2009
 */
package com.demo2.support.xml;

import java.io.IOException;
import java.io.InputStream;


/**
 * The interface for reading resources.
 * @author fangang
 */
public interface Resource {
	/**
	 * read resources and return InputStream.
	 * @return InputStream
	 * @throws IOException 
	 */
	public InputStream getInputStream()throws IOException;
	
	/**
	 * @return get the description of resource for debugging.
	 */
	public String getDescription();

	/**
	 * @param filter the file filter
	 */
	public void setFilter(Filter filter);
	
	/**
	 * @return the file filter
	 */
	public Filter getFilter();
	
	/**
	 * @return the full filename include the path.
	 */
	public String getFileName();
}
