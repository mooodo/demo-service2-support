/*
 * Created by 2020-06-25 13:59:49 
 */
package com.demo2.support.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * load resources with the file path.
 * @author fangang
 */
public class FileResourceLoader 
					extends AbstractResourceLoader implements ResourceLoader {

	@Override
	public boolean loadResource(ResourceCallBack callback, String path) throws IOException {
		boolean success = false;
		FileResource loader = new FileResource(new File(path));
		loader.setFilter(this.getFilter());
		Resource[] loaders = loader.getResources();
		if(loaders==null){return false;}
		for(int i=0; i<loaders.length; i++){
			InputStream is = loaders[i].getInputStream();
			if(is!=null){
				callback.apply(is);
				success = true;
			}
		}
		return success;
	}
}
