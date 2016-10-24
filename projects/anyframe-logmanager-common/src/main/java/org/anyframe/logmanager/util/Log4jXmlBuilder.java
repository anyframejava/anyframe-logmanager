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
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

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
	private static final String APPENDER_REF = "appender-ref";
	private static final String LEVEL = "level";
	private static final String VALUE = "value";
	private static final String REF = "ref";
	private static final String ADDITIVITY = "additivity";
	private static final String NAME = "name";
	private static final String CLASS = "class";
	private static final String PARAM = "param";
	private static final String LAYOUT = "layout";
	private static final String INDENT = "    ";
	
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
	 * constructor
	 * 
	 * @param is
	 * @throws Exception
	 */
	public Log4jXmlBuilder(InputStream is) throws Exception {
		super();
		parse(is);
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public String getXmlString() throws Exception {
		try {
			Source source = new DOMSource(log4jXmlDoc);
			StringWriter writer = new StringWriter();
			
			Result result = new StreamResult(writer);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", new Integer(4));
			
			Transformer xformer = tf.newTransformer();
			xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "log4j.dtd");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * save to file log4j.xml of log4j xml dom data
	 * 
	 * @throws Exception
	 */
	public void exportLog4jXmlDoc() throws Exception {
		Source source = new DOMSource(log4jXmlDoc);
		Result result = null;

		result = new StreamResult(new OutputStreamWriter(new FileOutputStream(log4jXmlPath), "utf-8"));

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
	 * log4j.xml load and parse to xml dom
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void parse(String file) throws Exception {
		File f = new File(file);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// dtd 기준 parsing 막기 위해 -> 없애면 FileNotFoundException 발생
		factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();

		log4jXmlDoc = builder.parse(f);
	}

	/**
	 * @param xml
	 * @throws Exception
	 */
	private void parse(InputStream is) throws Exception {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// dtd 기준 parsing 막기 위해 -> 없애면 FileNotFoundException 발생
		factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		log4jXmlDoc = builder.parse(is);
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
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (node.getNodeName().equalsIgnoreCase(LEVEL)) {
				NamedNodeMap nodeAttr = node.getAttributes();
				if (nodeAttr != null)
					root.setLevel(nodeAttr.getNamedItem(VALUE).getNodeValue());

			} else if (node.getNodeName().equalsIgnoreCase(APPENDER_REF)) {
				NamedNodeMap nodeAttr = node.getAttributes();
				if (nodeAttr != null)
					appenders.add(nodeAttr.getNamedItem(REF).getNodeValue());
			}
		}
		root.setAppenderRefs(appenders);
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

		for (int i = 0; i < list.getLength(); i++) {
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
				} else if (child.getNodeName().equals("layout")) {
					NamedNodeMap paramAttr = child.getAttributes();
					layout.setLayoutClass(paramAttr.getNamedItem("class").getNodeValue());
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
	public void setAppenderNodeList(List<Appender> appenders) throws Exception {
		Element rootElement = log4jXmlDoc.getDocumentElement();
		Map<String, Node> previousNodeMap = new HashMap<String, Node>();
		Map<String, Appender> appenderMap = new HashMap<String, Appender>();
		
		NodeList list = log4jXmlDoc.getElementsByTagName(APPENDER);
		for(int i=0;i<list.getLength();i++){
			Node previousNode = list.item(i);
			previousNodeMap.put(previousNode.getAttributes().getNamedItem(NAME).getNodeValue(), previousNode);
		}
		
		for (int i = 0; i < appenders.size(); i++) {
			Appender appender = (Appender) appenders.get(i);
			appenderMap.put(appender.getName(), appender);
			Node tempNode = previousNodeMap.get(appender.getName());
			if (tempNode == null) { // appender add
				Node node = convertAppender2Node(appender);
				rootElement.appendChild(node);
				Text t = log4jXmlDoc.createTextNode("\n" + INDENT);
				rootElement.insertBefore(t, node);
			}else{ // appender update
				changeAppender(tempNode, appender);
			}
		}
		
		// appender delete
		Iterator<String> i = previousNodeMap.keySet().iterator();
		while(i.hasNext()) {
			String key = i.next();
			if(appenderMap.get(key) == null) {
				Node node = previousNodeMap.get(key);
				if(Node.TEXT_NODE == node.getNextSibling().getNodeType()) {
					((Text)node.getNextSibling()).setData("");
				}
				node.getParentNode().removeChild(node);
			}
		}
	}

	/**
	 * @param tempNode
	 * @param appender
	 * @return
	 */
	private void changeAppender(Node tempNode, Appender appender) throws Exception {
		NamedNodeMap nnm = tempNode.getAttributes();
		Node n = nnm.getNamedItem(CLASS);
		n.setNodeValue(appender.getAppenderClass());
		
		removeNodes(tempNode, PARAM);
		if(appender.getParams() != null){
			int paramsSize = appender.getParams().size();
			for(int i=0;i<paramsSize;i++) {
				tempNode.appendChild(convertParam2Node(appender.getParams().get(i)));
			}
		}
		removeNodes(tempNode, LAYOUT);
		tempNode.appendChild(convertLayout2Node(appender.getLayout()));
		
		for(int i=0;i<tempNode.getChildNodes().getLength();i++) {
			if(Node.TEXT_NODE == tempNode.getChildNodes().item(i).getNodeType()) {
				Text t = (Text)tempNode.getChildNodes().item(i);
				t.setData("");
			}
		}
		((Text)tempNode.getChildNodes().item(0)).setData("\n" + INDENT + INDENT);
	}
	
	

	/**
	 * convert appender to xml element
	 * 
	 * @param appender
	 * @return
	 * @throws Exception
	 */
	private Node convertAppender2Node(Appender appender) throws Exception {
		Element el = null;
		el = log4jXmlDoc.createElement(APPENDER);
		el.setAttribute(NAME, appender.getName());
		el.setAttribute(CLASS, appender.getAppenderClass());
		for (int i = 0; i < appender.getParams().size(); i++) {
			Param param = (Param) appender.getParams().get(i);
			el.appendChild(convertParam2Node(param));
		}
		el.appendChild(convertLayout2Node(appender.getLayout()));
		return el;
	}

	/**
	 * convert appender layout to xml element
	 * 
	 * @param layout
	 * @return
	 * @throws Exception
	 */
	private Node convertLayout2Node(Layout layout) throws Exception {
		Node node = null;
		node = log4jXmlDoc.createElement("layout");
		((Element)node).setAttribute("class", layout.getLayoutClass());
		List<Param> params = layout.getParams();
		int paramLength = params.size();
		for (int i = 0; i < paramLength; i++) {
			node.appendChild(convertParam2Node(params.get(i)));
		}
		return node;
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
		for (int i = 0; i < list.getLength(); i++) {
			Logger logger = new Logger();
			Node node = list.item(i);
			NamedNodeMap loggerAttr = node.getAttributes();

			ArrayList<String> appenders = new ArrayList<String>();
			NodeList loggerChild = node.getChildNodes();
			for (int j = 0; j < loggerChild.getLength(); j++) {
				Node child = loggerChild.item(j);
				NamedNodeMap paramAttr = child.getAttributes();
				if (child.getNodeName().equals(LEVEL)) {
					logger.setLevel(paramAttr.getNamedItem(VALUE).getNodeValue());
				} else if (child.getNodeName().equals(APPENDER_REF)) {
					appenders.add(paramAttr.getNamedItem("ref").getNodeValue());
				}
			}
			logger.setAppenderRefs(appenders);
			logger.setName(loggerAttr.getNamedItem(NAME).getNodeValue());
			logger.setAdditivity(loggerAttr.getNamedItem(ADDITIVITY).getNodeValue());

			result.add(logger);
		}
		return result;
	}

	/**
	 * change log level of log4j logger from log4j xml dom
	 * 
	 * @param loggers
	 */
	public void setLoggerNodeList(List<Logger> loggers) throws Exception  {
		Element rootElement = log4jXmlDoc.getDocumentElement();
		
		NodeList loggerNodeList = log4jXmlDoc.getElementsByTagName(LOGGER);
		while(loggerNodeList.getLength() > 0) {
			Node rn = loggerNodeList.item(0);
			((Text)rn.getPreviousSibling()).setData("");
			((Text)rn.getNextSibling()).setData("");
			rn.getParentNode().removeChild(loggerNodeList.item(0));
		}
		
		int loggerSize = loggers.size();
		int appenderRefsSize = 0;
		for (int i = 0; i < loggerSize; i++) {
			Logger logger = (Logger) loggers.get(i);
			Node loggerNode = log4jXmlDoc.createElement(LOGGER);
			((Element)loggerNode).setAttribute(NAME, logger.getName());
			((Element)loggerNode).setAttribute(ADDITIVITY, logger.getAdditivity());
			loggerNode.appendChild(convertLevel2Node(logger.getLevel()));
			appenderRefsSize = logger.getAppenderRefs().size();
			for(int j=0;j<appenderRefsSize;j++){
				loggerNode.appendChild(convertAppenderRef2Node(logger.getAppenderRefs().get(j)));
			}
			rootElement.insertBefore(loggerNode, log4jXmlDoc.getElementsByTagName(ROOT).item(0));
			Text pt = log4jXmlDoc.createTextNode("\n\n" + INDENT);
			rootElement.insertBefore(pt, loggerNode);
		}
	}
	
	
	/**
	 * convert appender param to xml element
	 * 
	 * @param parentNode
	 * @param param
	 * @throws Exception
	 */
	private Node convertParam2Node(Param param) throws Exception {
		Node paramNode = log4jXmlDoc.createElement(PARAM);
		((Element)paramNode).setAttribute(NAME, param.getName());
		((Element)paramNode).setAttribute(VALUE, param.getValue());
		return paramNode;
	}
	
	/**
	 * @param parentNode
	 * @param level
	 */
	private Node convertLevel2Node(String level) throws Exception {
		Node levelNode = log4jXmlDoc.createElement(LEVEL);
		((Element)levelNode).setAttribute(VALUE, level);
		return levelNode;
	}

	/**
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	private Node convertAppenderRef2Node(String ref) throws Exception {
		Node appenderRefNode = log4jXmlDoc.createElement(APPENDER_REF);
		((Element)appenderRefNode).setAttribute(REF, ref);
		return appenderRefNode;
	}

	/**
	 * @param root
	 * @throws Exception
	 */
	public void setRootNode(Root root) throws Exception {
		Node rootNode = log4jXmlDoc.getElementsByTagName(ROOT).item(0);
		List<Node> appenderRefList = new ArrayList<Node>();
		NodeList childNodeList = rootNode.getChildNodes();
		for(int i=0;i<childNodeList.getLength();i++){
			if(LEVEL.equals(childNodeList.item(i).getNodeName())) {
				// level
				rootNode.replaceChild(convertLevel2Node(root.getLevel()), childNodeList.item(i));
			}
		}
		
		NodeList childList = rootNode.getChildNodes();
		for(int i=0;i<childList.getLength();i++) {
			if(APPENDER_REF.equals(childList.item(i).getNodeName())) {
				appenderRefList.add(childList.item(i));
			}
		}
		for(int i=0;i<appenderRefList.size();i++) {
			Node node = appenderRefList.get(i);
			if(Node.TEXT_NODE == node.getNextSibling().getNodeType()) {
				((Text)node.getNextSibling()).setData("");
			}
			node.getParentNode().removeChild(node);
		}
		
		// appender-refs
		int appenderRefsSize = root.getAppenderRefs().size();
		for(int i=0;i<appenderRefsSize;i++){
			rootNode.appendChild(convertAppenderRef2Node(root.getAppenderRefs().get(i)));	
		}
	}
	
	
	/**
	 * @param parentNode
	 * @param elementName
	 */
	private void removeNodes(Node parentNode, String elementName) throws Exception {
		NodeList childList = parentNode.getChildNodes();
		List<Node> removeNodeList = new ArrayList<Node>();
		for (int j = 0; j < childList.getLength(); j++) {
			Node child = childList.item(j);
			if (child.getNodeName().equals(elementName)) {
				removeNodeList.add(child);
			}
		}
		for(int i=0;i<removeNodeList.size();i++) {
			parentNode.removeChild(removeNodeList.get(i));
		}
	}

}
