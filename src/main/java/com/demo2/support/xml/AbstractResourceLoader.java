/*
 * Created by 2020-06-25 13:44:36 
 */
package com.demo2.support.xml;

import java.io.IOException;

/**
 * @author fangang
 */
public abstract class AbstractResourceLoader implements ResourceLoader {
	private Filter filter = null;

	/**
	 * @return the file filter, default the xml file filter.
	 */
	@Override
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
	 * @param filter
	 */
	@Override
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public boolean loadResource(ResourceCallBack callback, String... paths) throws IOException {
		boolean success = true;
		for(int i=0; i<paths.length; i++) {
			if(!loadResource(callback, paths[i])) success=false;
		}
		return success;
	}
}
