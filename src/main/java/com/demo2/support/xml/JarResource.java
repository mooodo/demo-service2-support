/*
 * created on Dec 1, 2009
 */
package com.demo2.support.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 专门用于读取jar包中文件资源的代理类
 * @author 范钢
 */
public class JarResource implements ResourcePath {

	private static final Log log = LogFactory.getLog(JarResource.class);
	private URLConnection jarConnection = null;
	private URL jarUrl = null;
	private Filter filter = null;
	
	/**
	 * @return the jarConnection
	 */
	public URLConnection getJarConnection() {
		return jarConnection;
	}

	/**
	 * @return the jarUrl
	 */
	public URL getJarUrl() {
		return jarUrl;
	}
	
	/**
	 * Construction for URLConnection
	 * @param urlConnection
	 * @throws IOException
	 */
	public JarResource(URLConnection urlConnection) throws IOException{
		if(urlConnection==null){
			throw new RuntimeException("The urlConnection is null!");
		}
		if(urlConnection instanceof JarURLConnection){
			JarURLConnection jarURLConnection = (JarURLConnection)urlConnection;
			this.jarConnection = jarURLConnection;
			this.jarUrl = jarURLConnection.getJarFileURL();
		}else{
			this.jarConnection = urlConnection;
			this.jarUrl = urlConnection.getURL();
		}
		log.debug("loading "+this.getDescription());
	}
	
	/**
	 * Construction for Url
	 * @param jarUrl
	 * @throws IOException 
	 */
	public JarResource(URL jarUrl) throws IOException {
		this.jarUrl = jarUrl;
		this.jarConnection = jarUrl.openConnection();
		log.debug("loading "+this.getDescription());
	}

	/**
	 * 利用JarFile来获取jar包中某个目录下的所有文件
	 * @return Resource[]
	 * @exception IOException
	 */
	public Resource[] getResources() throws IOException {
		URLConnection urlConnection = this.getJarConnection();
		if(urlConnection==null){
			throw new IOException("无法获取URLConnection");
		}
		if(urlConnection instanceof JarURLConnection){
			return this.getJarResources((JarURLConnection)urlConnection);
		}else{
			return this.getResources(urlConnection);
		}
	}

	/**
	 * JarConnection获取文件资源
	 * @param jarURLConnection
	 * @return Resource[]
	 * @throws IOException
	 */
	protected Resource[] getJarResources(JarURLConnection jarURLConnection) throws IOException {
		String rootEntryPath = jarURLConnection.getJarEntry().getName();
		JarFile jarFile = jarURLConnection.getJarFile();
		if (rootEntryPath.endsWith("/")) {
			// Root entry path must not end with slash to allow for proper matching.
			// The Sun JRE does not return a slash here, but BEA JRockit does.
			rootEntryPath = rootEntryPath.substring(0, rootEntryPath.length() - 1);
		}
		String jarFileUrlPrefix = "jar:" + this.getJarUrl().toExternalForm() + "!/";
		List<Resource> loaderList = new ArrayList<Resource>();
		for(Enumeration<JarEntry> entries = jarFile.entries();entries.hasMoreElements();){
			JarEntry entry = (JarEntry) entries.nextElement();
			String entryPath = entry.getName();
			Filter filter = this.getFilter();
			if(filter!=null&&!filter.isSatisfied(entryPath)){continue;}
			if (entryPath.startsWith(rootEntryPath)){
				URL url= new URL(jarFileUrlPrefix + entryPath);
				Resource resource = new UrlResource(url);
				resource.setFilter(this.getFilter());
				loaderList.add(resource);
			}
		}
		return (Resource[])loaderList.toArray(new Resource[loaderList.size()]);
	}
	
	/**
	 * 非JarConnection(zip或war)获取文件资源
	 * @param urlConnection
	 * @return Resource[]
	 * @throws IOException
	 */
	protected Resource[] getResources(URLConnection urlConnection) throws IOException {
		URL jarURL = urlConnection.getURL();
		String urlFile = jarURL.getFile();
		if(urlFile.startsWith("file:")){
			urlFile = urlFile.substring("file:".length());
		}
        if(!urlFile.endsWith("/"))
        	urlFile = urlFile + "/";
		int index = urlFile.indexOf("!/");
		if(index==-1){
			throw new FileNotFoundException("The url isn't a jar/zip/war url: ["+urlFile+"]");
		}
		String jarFileUrl = urlFile.substring(0, index);
		JarFile jarFile = new JarFile(jarFileUrl);
        jarFileUrl = "file:" + jarFileUrl;
        String rootEntryPath = urlFile.substring(index + "!/".length());
		
        Set<Resource> result = new HashSet<Resource>();
        for(Enumeration<JarEntry> entries = jarFile.entries();entries.hasMoreElements();){
            JarEntry entry = (JarEntry)entries.nextElement();
            String entryPath = entry.getName();
            Filter filter = this.getFilter();
			if(filter!=null&&!filter.isSatisfied(entryPath)){continue;}
            if(entryPath.startsWith(rootEntryPath)){
                String relativePath = entryPath.substring(rootEntryPath.length());
                URL url = new URL("zip:"+urlFile+relativePath);
                Resource resource = new UrlResource(url);
                resource.setFilter(this.getFilter());
                result.add(resource);
            }
        }
        jarFile.close();
		return (Resource[])result.toArray(new Resource[result.size()]);
	}

	/* (non-Javadoc)
	 * @see com.htxx.taglib.xml.Resource#getDescription()
	 */
	public String getDescription() {
		return (new StringBuffer("JarResource:[jarUrl:"))
					.append(this.getJarUrl()).append("]").toString();
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
		if(this.getJarUrl()==null){return null;}
		return this.getJarUrl().getFile();
	}
}
