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
package org.anyframe.logmanager.domain;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * @author Jaehyoung Eum
 *
 */
public class LogApplication {
	
	@Id
	private String id;
	private String appName;
	private String log4jXmlPath;
	private int status;
	private String statusMessage;
	private List<Logger> loggers;
	private List<Appender> appenders;
	private Root root;
	private String agentId;
	
	/**
	 * @return
	 */
	public String getAgentId() {
		return agentId;
	}

	/**
	 * @param agentId
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	/**
	 * @return the root
	 */
	public Root getRoot() {
		return root;
	}
	
	/**
	 * @param root the root to set
	 */
	public void setRoot(Root root) {
		this.root = root;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * @param statusMessage the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	
	/**
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	/**
	 * @return the log4jXmlPath
	 */
	public String getLog4jXmlPath() {
		return log4jXmlPath;
	}
	
	/**
	 * @param log4jXmlPath the log4jXmlPath to set
	 */
	public void setLog4jXmlPath(String log4jXmlPath) {
		this.log4jXmlPath = log4jXmlPath;
	}
	
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * @return the loggers
	 */
	public List<Logger> getLoggers() {
		return loggers;
	}
	
	/**
	 * @param loggers the loggers to set
	 */
	public void setLoggers(List<Logger> loggers) {
		this.loggers = loggers;
	}
	
	/**
	 * @return the appenders
	 */
	public List<Appender> getAppenders() {
		return appenders;
	}
	
	/**
	 * @param appenders the appenders to set
	 */
	public void setAppenders(List<Appender> appenders) {
		this.appenders = appenders;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogApplication [id=" + id + ", appName=" + appName + ", log4jXmlPath=" + log4jXmlPath 
		+ ", status=" + status + ", statusMessage=" + statusMessage 
		+ ", root=" + (root == null ? "" : root.toString())
		+ ", loggers=" + (loggers == null ? "" : loggers.toString()) + ", appenders=" + (appenders == null ? "" : appenders.toString()) + "]";
	}
	
}
