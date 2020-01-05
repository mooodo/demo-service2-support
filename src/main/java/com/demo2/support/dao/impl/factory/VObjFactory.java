/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.demo2.support.exception.DaoException;
import com.demo2.support.xml.XmlBuildFactoryTemplate;

/**
 * The factory that get the configure of the value objects.
 * @author fangang
 */
public class VObjFactory extends XmlBuildFactoryTemplate {
	private static Map<String, VObj> vObjMap = new HashMap<>();
	@Value("vObjFile")
	private String vObjFile = "classpath:vObj.xml";
	
	/**
	 * The default constructor.
	 */
	public VObjFactory() {
		if(vObjMap!=null && !vObjMap.isEmpty()) return;
		initFactory(vObjFile.split(","));
	}
	
	/**
	 * get the configure of the certain value object.
	 * @param clazz the class name of the value object
	 * @return the configure of the value object
	 */
	public static VObj getVObj(String clazz) {
		VObj vObj = vObjMap.get(clazz);
		if(vObj==null) throw new DaoException("Not found the value object["+clazz+"] in the vObj.xml!");
		return vObj;
	}
	
	@Override
	protected void buildFactory(Element root) {
		NodeList nodeList = root.getChildNodes();
		for(int i=0; i<=nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(!(node instanceof Element)) continue;
			Element element = (Element) node;
			loadBean(element);
		}
	}
	
	/**
	 * decode a value object configure and load into the factory.
	 * @param element
	 */
	protected void loadBean(Element element) {
		VObj vObj = new VObj();
		String clazz = element.getAttribute("class");
		vObj.setClazz(clazz);
		String tableName = element.getAttribute("tableName");
		vObj.setTable(tableName);
		loadChildNodes(element, vObj);
		vObjMap.put(clazz, vObj);
	}
	
	/**
	 * load all of the child nodes into vObj.
	 * @param element
	 * @param vObj
	 */
	private void loadChildNodes(Element element, VObj vObj) {
		NodeList nodeList = element.getChildNodes();
		for(int i=0; i<=nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(!(node instanceof Element)) continue;
			if (node.getNodeName().equals("property")) {
				Property property = getProperty((Element) node);
				vObj.getProperties().add(property);
			}
			if (node.getNodeName().equals("join")) {
				Join join = getJoin((Element) node);
				vObj.getJoins().add(join);
			}
		}
	}
	
	/**
	 * get property tag from xml.
	 * @param element
	 * @return property
	 */
	private Property getProperty(Element element) {
		Property property = new Property();
		String name = element.getAttribute("name");
		property.setName(name);
		String column = element.getAttribute("column");
		property.setColumn(column);
		String isPrimaryKey = element.getAttribute("isPrimaryKey");
		property.setPrimaryKey("true".equalsIgnoreCase(isPrimaryKey));
		return property;
	}
	
	/**
	 * get join tag from xml.
	 * @param element
	 * @return join
	 */
	private Join getJoin(Element element) {
		Join join = new Join();
		String name = element.getAttribute("name");
		join.setName(name);
		String joinKey = element.getAttribute("joinKey");
		join.setJoinKey(joinKey);
		String joinType = element.getAttribute("joinType");
		join.setJoinType(joinType);
		String clazz = element.getAttribute("class");
		join.setClazz(clazz);
		boolean isAggregation = "true".equals(element.getAttribute("isAggregation")) ? true : false;
		join.setAggregation(isAggregation);
		return join;
	}
}
