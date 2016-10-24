/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.logmanager.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.Layout;
import org.anyframe.logmanager.domain.Logger;
import org.anyframe.logmanager.domain.Param;
import org.anyframe.logmanager.domain.Root;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * xml parser class for log4j.xml 
 * 
 * @author Jaehyoung Eum
 *
 */
public class Log4jXmlBuilder {
	
	private static final String ROOT = "root";
	private static final String APPENDER = "appender";
	private static final String LOGGER = "logger";
	
	private Document log4jXmlDoc;
	private String log4jXmlPath;
	
	/**
	 * constructor
	 * 
	 * @param log4jXmlPath
	 */
	public Log4jXmlBuilder(String log4jXmlPath) throws Exception {
		super();
		this.log4jXmlPath = log4jXmlPath;
		parse(log4jXmlPath);
	}
	
	/**
	 * save to file log4j.xml of log4j xml dom data 
	 * 
	 * @throws Exception
	 */
	public void exportLog4jXmlDoc() throws Exception {
		Source source = new DOMSource(log4jXmlDoc);
//		Result result = new StreamResult(new File(log4jXmlPath));
		Result result = new StreamResult(new OutputStreamWriter(new FileOutputStream(log4jXmlPath), "utf-8"));
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setAttribute("indent-number", new Integer(4));
		Transformer xformer = tf.newTransformer();
		xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "log4j.dtd");
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.transform(source, result);
	}
	
	/**
	 * get root node
	 * 
	 * @return
	 * @throws Exception
	 */
	public Root getLog4jRoot() throws Exception {
		return getRootNode();
	}
	
	/**
	 * set root logger log level info to log4j xml dom
	 * 
	 * @param root
	 * @throws Exception
	 */
	public void setLog4jRoot(Root root) throws Exception {
		changeRootLevel(root);
	}
	
	/**
	 * set appender info to log4j xml dom
	 * 
	 * @param appenders
	 * @throws Exception
	 */
	public void setLog4jAppender(List<Appender> appenders) throws Exception {
		setAppenderNodeList(appenders);
	}
	
	/**
	 * get appenders from xml dom
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Appender> getLog4jAppender() throws Exception {
		return getAppenderNodeList();
	}
	
	/**
	 * get loggers
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Logger> getLog4jLogger() throws Exception {
		return getLoggerNodeList();
	}
	
	/**
	 * set logger info to log4j xml dom
	 * 
	 * @param loggers
	 * @throws Exception
	 */
	public void setLog4jLogger(List<Logger> loggers) throws Exception {
		changeLoggerLevel(loggers);
	}
	
	/**
	 * log4j.xml load and parse to xml dom
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void parse(String file) throws Exception {
		File f = new File(file);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//dtd 기준 parsing 막기 위해 -> 없애면 FileNotFoundException 발생
		factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		log4jXmlDoc = builder.parse(f);
	}
	
	/**
	 * get root logger
	 * 
	 * @return
	 * @throws Exception
	 */
	private Root getRootNode() throws Exception {
		Node rootNode = log4jXmlDoc.getElementsByTagName(ROOT).item(0);
		
		Root root = new Root();
		NodeList list = rootNode.getChildNodes();
		ArrayList<String> appenders = new ArrayList<String>();
		for(int i=0; i<list.getLength(); i++){
			Node node = list.item(i);
			if(node.getNodeName().equalsIgnoreCase("level")){
				NamedNodeMap nodeAttr = node.getAttributes();
				if(nodeAttr!=null)
					root.setLevel(nodeAttr.getNamedItem("value").getNodeValue());
				
			}else if(node.getNodeName().equalsIgnoreCase("appender-ref")){
				NamedNodeMap nodeAttr = node.getAttributes();
				if(nodeAttr!=null)
					appenders.add(nodeAttr.getNamedItem("ref").getNodeValue());
			}
		}
		root.setAppenderRef(appenders);
		return root;
	}

	/**
	 * get appender list from log4j xml dom
	 * 
	 * @return
	 * @throws Exception
	 */
	private List<Appender> getAppenderNodeList() throws Exception {
		List<Appender> result = new ArrayList<Appender>();
		NodeList list = log4jXmlDoc.getElementsByTagName(APPENDER);
		
		for(int i=0; i<list.getLength(); i++){
			Appender appender = new Appender();
			Node node = list.item(i);
			NamedNodeMap appenderAttr = node.getAttributes();
			
			NodeList appenderChild = node.getChildNodes();
			List<Param> paramList = new ArrayList<Param>();
			Layout layout = new Layout();
			for(int j=0; j<appenderChild.getLength(); j++){
				Node child = appenderChild.item(j);
				if(child.getNodeName().equals("param")){
					NamedNodeMap paramAttr = child.getAttributes();
					Param param = new Param();
					param.setName(paramAttr.getNamedItem("name").getNodeValue());
					param.setValue(paramAttr.getNamedItem("value").getNodeValue());
					paramList.add(param);
				}else if(child.getNodeName().equals("layout")){
					NamedNodeMap paramAttr = child.getAttributes();
					layout.setLayoutClass(paramAttr.getNamedItem("class").getNodeValue());
					NodeList layoutChild = child.getChildNodes();
					for(int k=0; k<layoutChild.getLength(); k++){
						Node paramNode = layoutChild.item(k);
						if(paramNode.getNodeName().equals("param")){
							NamedNodeMap layoutParamAttr = paramNode.getAttributes();
							Param param = new Param();
							param.setName(layoutParamAttr.getNamedItem("name").getNodeValue());
							param.setValue(layoutParamAttr.getNamedItem("value").getNodeValue());
							layout.setParam(param);
						}
					}
				}
			}
			
			appender.setName(appenderAttr.getNamedItem("name").getNodeValue());
			appender.setAppenderClass(appenderAttr.getNamedItem("class").getNodeValue());
			appender.setParams(paramList);
			appender.setLayout(layout);
			
			result.add(appender);
		}
		return result;
	}
	
	/**
	 * set appender list info to log4j xml dom
	 * 
	 * @param appenders
	 * @throws Exception
	 */
	private void setAppenderNodeList(List<Appender> appenders) throws Exception {
		NodeList list = log4jXmlDoc.getElementsByTagName(APPENDER);
		for(int i=0;i<appenders.size();i++) {
			Appender appender = (Appender)appenders.get(i);
			for(int j=0;j<list.getLength();j++) {
				Node node = (Node)list.item(j);
				if(appender.getName().equals(node.getAttributes().getNamedItem("name").getNodeValue())) {
					//log4jXmlDoc.removeChild(node);
					//log4jXmlDoc.appendChild(convertAppender2Element(appender));
					//log4jXmlDoc.replaceChild(convertAppender2Element(appender), node);
					node.getParentNode().replaceChild(convertAppender2Element(appender), node);
					break;
				}	
			}
		}
	}
	
	/**
	 * convert appender to xml element
	 * 
	 * @param appender
	 * @return
	 * @throws Exception
	 */
	private Element convertAppender2Element(Appender appender) throws Exception {
		Element el = null;
		el = log4jXmlDoc.createElement("appender");
		el.setAttribute("name", appender.getName());
		el.setAttribute("class", appender.getAppenderClass());
		el.appendChild(convertLayout2Element(appender.getLayout()));
		for(int i=0;i<appender.getParams().size();i++) {
			Param param = (Param)appender.getParams().get(i);
			el.appendChild(convertParam2Element(param));
		}
		return el;
	}
	
	/**
	 * convert appender layout to xml element
	 * 
	 * @param layout
	 * @return
	 * @throws Exception
	 */
	private Element convertLayout2Element(Layout layout) throws Exception {
		Element el = null;
		el = log4jXmlDoc.createElement("layout");
		el.setAttribute("class", layout.getLayoutClass());
		el.appendChild(convertParam2Element(layout.getParam()));
		return el;
	}
	
	/**
	 * convert appender param to xml element 
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private Element convertParam2Element(Param param) throws Exception {
		Element el = null;
		el = log4jXmlDoc.createElement("param");
		el.setAttribute("name", param.getName());
		el.setAttribute("value", param.getValue());
		return el;
	}

	/**
	 * get logger list from log4j xml dom
	 * 
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Logger> getLoggerNodeList() throws Exception {
		ArrayList<Logger> result = new ArrayList<Logger>();
		NodeList list = log4jXmlDoc.getElementsByTagName(LOGGER);
		for(int i=0; i<list.getLength(); i++){
			Logger logger = new Logger();
			Node node = list.item(i);
			NamedNodeMap loggerAttr = node.getAttributes();
			
			ArrayList<String> appenders = new ArrayList<String>();
			NodeList loggerChild = node.getChildNodes();
			for(int j=0; j<loggerChild.getLength(); j++){
				Node child = loggerChild.item(j);
				NamedNodeMap paramAttr = child.getAttributes();
				if(child.getNodeName().equals("level")){
					logger.setLevel(paramAttr.getNamedItem("value").getNodeValue());
				}else if(child.getNodeName().equals("appender-ref")){
					appenders.add(paramAttr.getNamedItem("ref").getNodeValue());
				}
			}
			logger.setAppenderRef(appenders);
			logger.setName(loggerAttr.getNamedItem("name").getNodeValue());
			logger.setAdditivity(loggerAttr.getNamedItem("additivity").getNodeValue());
			
			result.add(logger);
		}
		return result;
	}
	
	/**
	 * change log level of log4j logger from log4j xml dom
	 * 
	 * @param loggers
	 */
	private void changeLoggerLevel(List<Logger> loggers){
		NodeList list = log4jXmlDoc.getElementsByTagName(LOGGER);
		for(int k=0;k<loggers.size();k++) {
			Logger logger = (Logger)loggers.get(k);
			for(int i=0; i<list.getLength(); i++){
				Node node = list.item(i);
				if(logger.getName().equals(node.getAttributes().getNamedItem("name").getNodeValue())) {
					NodeList loggerChild = node.getChildNodes();
					for(int j=0; j<loggerChild.getLength(); j++){
						Node child = loggerChild.item(j);
						if(child.getNodeName().equals("level")){
							((Element)child).setAttribute("value", logger.getLevel());
						}
					}
				}
			}
		}
	}
	
	/**
	 * change root logger level
	 * 
	 * @param root
	 */
	private void changeRootLevel(Root root) {
		Node rootNode = log4jXmlDoc.getElementsByTagName(ROOT).item(0);
		
		NodeList rootChild = rootNode.getChildNodes();
		for(int j=0; j<rootChild.getLength(); j++){
			Node child = rootChild.item(j);
			if(child.getNodeName().equals("level")){
				((Element)child).setAttribute("value", root.getLevel());
			}
		}
	}
}
