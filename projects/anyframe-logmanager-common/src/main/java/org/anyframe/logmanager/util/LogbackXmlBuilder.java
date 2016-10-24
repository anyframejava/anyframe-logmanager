/* 
 * Copyright (C) 2002-2012 Robert Stewart (robert@wombatnation.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anyframe.logmanager.util;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.Layout;
import org.anyframe.logmanager.domain.Logger;
import org.anyframe.logmanager.domain.Param;
import org.anyframe.logmanager.domain.Root;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * xml parser class for logback.xml
 *
 * @author jaehyoung.eum
 *
 */
public class LogbackXmlBuilder {
	
	private static final String ROOT = "root";
	private static final String APPENDER = "appender";
	private static final String LOGGER = "logger";
	private static final String APPENDER_REF = "appender-ref";
	private static final String LEVEL = "level";
	private static final String VALUE = "value";
	private static final String REF = "ref";
	private static final String ADDITIVITY = "additivity";
	private static final String NAME = "name";
	private static final String CLASS = "class";
	private static final String PARAM = "param";
	private static final String LAYOUT = "layout";
	private static final String SIFTING_APPENDER = "ch.qos.logback.classic.sift.SiftingAppender";
	private static final String DUPLICATED_APENDER = "duplicated appender";
	
	private Document logbackXmlDoc;

	/**
	 * constructor
	 * 
	 * @param logbackXmlPath
	 */
	public LogbackXmlBuilder(String logbackXmlPath) throws Exception {
		super();
		parse(logbackXmlPath);
	}
	
	/**
	 * constructor
	 * 
	 * @param is
	 * @throws Exception
	 */
	public LogbackXmlBuilder(InputStream is) throws Exception {
		super();
		parse(is);
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getXmlString() throws Exception {
		try {
			Source source = new DOMSource(logbackXmlDoc);
			StringWriter writer = new StringWriter();
			
			Result result = new StreamResult(writer);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", new Integer(4));
			
			Transformer xformer = tf.newTransformer();
			xformer.transform(source, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * get root node
	 * 
	 * @return
	 * @throws Exception
	 */
	public Root getLogbackRoot() throws Exception {
		return getRootNode();
	}
	

	/**
	 * get appenders from xml dom
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Appender> getLogbackAppender() throws Exception {
		return getAppenderNodeList();
	}

	/**
	 * get loggers
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Logger> getLogbackLogger() throws Exception {
		return getLoggerNodeList();
	}
	

	/**
	 * @param logbackXmlPath2
	 * @throws Exception
	 */
	private void parse(String file) throws Exception {
		File f = new File(file);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// dtd 기준 parsing 막기 위해 -> 없애면 FileNotFoundException 발생
		factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();

		logbackXmlDoc = builder.parse(f);
	}

	
	/**
	 * @param is
	 * @throws Exception
	 */
	private void parse(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// dtd 기준 parsing 막기 위해 -> 없애면 FileNotFoundException 발생
		factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		logbackXmlDoc = builder.parse(is);
		
	}
	
	/**
	 * get root logger
	 * 
	 * @return
	 * @throws Exception
	 */
	private Root getRootNode() throws Exception {
		Node rootNode = logbackXmlDoc.getElementsByTagName(ROOT).item(0);

		Root root = new Root();
		root.setLevel(rootNode.getAttributes().getNamedItem(LEVEL).getNodeValue());
		
		NodeList list = rootNode.getChildNodes();
		ArrayList<String> appenders = new ArrayList<String>();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equalsIgnoreCase(APPENDER_REF)) {
				NamedNodeMap nodeAttr = node.getAttributes();
				if (nodeAttr != null) {
					appenders.add(nodeAttr.getNamedItem(REF).getNodeValue());
				}
			}
		}
		root.setAppenderRefs(appenders);
		return root;
	}
	
	/**
	 * get logger list from logback.xml dom
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Logger> getLoggerNodeList() throws Exception {
		List<Logger> result = new ArrayList<Logger>();
		NodeList list = logbackXmlDoc.getElementsByTagName(LOGGER);
		for (int i = 0; i < list.getLength(); i++) {
			Logger logger = new Logger();
			Node node = list.item(i);
			NamedNodeMap loggerAttr = node.getAttributes();

			ArrayList<String> appenders = new ArrayList<String>();
			NodeList loggerChild = node.getChildNodes();
			for (int j = 0; j < loggerChild.getLength(); j++) {
				Node child = loggerChild.item(j);
				NamedNodeMap paramAttr = child.getAttributes();
				if (child.getNodeName().equals(APPENDER_REF)) {
					appenders.add(paramAttr.getNamedItem(REF).getNodeValue());
				}
			}
			logger.setAppenderRefs(appenders);
			logger.setName(loggerAttr.getNamedItem(NAME).getNodeValue());
			logger.setAdditivity(loggerAttr.getNamedItem(ADDITIVITY).getNodeValue());
			logger.setLevel(loggerAttr.getNamedItem(LEVEL).getNodeValue());
			result.add(logger);
		}
		return result;
	}
	
	/**
	 * get appender list from logback.xml dom
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Appender> getAppenderNodeList() throws Exception {
		List<Appender> result = null;//new ArrayList<Appender>();
		NodeList list = logbackXmlDoc.getElementsByTagName(APPENDER);
		Map<String, Appender> appenderMap = new HashMap<String, Appender>();
		boolean isSiftingAppender = false;
		for (int i = 0; i < list.getLength(); i++) {
			if(isSiftingAppender) {
				isSiftingAppender = false;
				continue;
			}
			Appender appender = new Appender();
			Node node = list.item(i);
			NamedNodeMap appenderAttr = node.getAttributes();

			NodeList appenderChild = node.getChildNodes();
			List<Param> paramList = new ArrayList<Param>();
			Layout layout = new Layout();
			for (int j = 0; j < appenderChild.getLength(); j++) {
				Node child = appenderChild.item(j);
				if (child.getNodeName().equals(PARAM)) {
					NamedNodeMap paramAttr = child.getAttributes();
					Param param = new Param();
					param.setName(paramAttr.getNamedItem(NAME).getNodeValue());
					param.setValue(paramAttr.getNamedItem(VALUE).getNodeValue());
					paramList.add(param);
				} else if (child.getNodeName().equals(LAYOUT)) {
					NamedNodeMap paramAttr = child.getAttributes();
					layout.setLayoutClass(paramAttr.getNamedItem(CLASS).getNodeValue());
					NodeList layoutChild = child.getChildNodes();
					for (int k = 0; k < layoutChild.getLength(); k++) {
						Node paramNode = layoutChild.item(k);
						if (paramNode.getNodeName().equals(PARAM)) {
							NamedNodeMap layoutParamAttr = paramNode.getAttributes();
							Param param = new Param();
							param.setName(layoutParamAttr.getNamedItem(NAME).getNodeValue());
							param.setValue(layoutParamAttr.getNamedItem(VALUE).getNodeValue());
							layout.setParam(param);
						}
					}
				}
			}
			appender.setName(appenderAttr.getNamedItem(NAME).getNodeValue());
			appender.setAppenderClass(appenderAttr.getNamedItem(CLASS).getNodeValue());
			appender.setParams(paramList);
			appender.setLayout(layout);
			if(SIFTING_APPENDER.equals(appender.getAppenderClass())) {
				isSiftingAppender = true;
			}
			//result.add(appender);
			if(appenderMap.containsKey(appender.getName())) {
				appender.setAppenderClass(DUPLICATED_APENDER);
			}
			appenderMap.put(appender.getName(), appender);
		}
		result = new ArrayList<Appender>(appenderMap.values());
		return result;
	}
	
}
