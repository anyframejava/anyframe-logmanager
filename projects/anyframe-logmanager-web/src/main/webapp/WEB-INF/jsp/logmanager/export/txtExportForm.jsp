<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %><%@ page import="java.text.SimpleDateFormat,java.util.Date,java.util.List,java.util.Iterator,org.anyframe.logmanager.domain.LogDataMap" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>[${listCount} rows]
<%
List<LogDataMap> list = (List<LogDataMap>)request.getAttribute("list");
StringBuffer logBuffer = new StringBuffer();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
Iterator it = null; 
int size = 0;
String key = null;
if(list != null) {
	size = list.size();
	for(int i=0;i<size;i++) {
		LogDataMap log = list.get(i);
		logBuffer.append("[").append(log.get("level")).append("]");
		//Date s = new Date();
		//s.setTime(Long.parseLong((String)log.get("timestamp")));
		logBuffer.append("[").append(sdf.format(log.get("timestamp"))).append("]");
		if(log.get("clinetIp") != null) {
			logBuffer.append("[").append(log.get("clinetIp")).append("]");	
		}
		if(log.get("userId") != null) {
			logBuffer.append("[").append(log.get("userId")).append("]");	
		}
		if(log.get("className") != null) {
			logBuffer.append("[").append(log.get("className"));
			if(log.get("methodName") != null) {
				logBuffer.append(log.get("methodName")).append("()]");
			}else{
				logBuffer.append("]");
			}
		}else if(log.get("methodName") != null) {
			logBuffer.append("[").append(log.get("methodName")).append("()]");
		}
		if(log.get("lineNumber") != null) {
			logBuffer.append("[line ").append(log.get("lineNumber")).append("]");
		}
		if(log.get("fileName") != null) {
			logBuffer.append("[line ").append(log.get("fileName")).append("]");
		}
		if(log.get("thread") != null) {
			logBuffer.append("[").append(log.get("thread")).append("]");
		}
		
		it = log.keySet().iterator();
		while(it.hasNext()) {
			key = (String)it.next();
			if(!"_id".equals(key) && !"level".equals(key) && !"timestamp".equals(key) && !"clientIp".equals(key) 
					&& !"userId".equals(key) && !"className".equals(key) && !"methodName".equals(key)
					&& !"lineNumber".equals(key) && !"fileName".equals(key) && !"message".equals(key)) {
				if(log.get(key) != null) {
					logBuffer.append("[").append(log.get(key)).append("]");
				}
			}
		}
		logBuffer.append(log.get("message")).append("\r\n");
	}
	out.print(logBuffer.toString());
}
%>