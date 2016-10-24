/* 
 * Copyright (C) 2010 Robert Stewart (robert@wombatnation.com)
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
package org.anyframe.logmanager.domain;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogAppender {
	
	private String agentId;
	private String appName;
	private long lastTimestamp = -1;
	private int monitorLevel;
	private String appenderName;
	private String pollingTime;
	private int status;
	private boolean fileAppender;
	private String collectionName;
	
	/**
	 * @return the collectionName
	 */
	public String getCollectionName() {
		return collectionName;
	}
	/**
	 * @param collectionName the collectionName to set
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	/**
	 * @return the fileAppender
	 */
	public boolean isFileAppender() {
		return fileAppender;
	}
	/**
	 * @param fileAppender the fileAppender to set
	 */
	public void setFileAppender(boolean fileAppender) {
		this.fileAppender = fileAppender;
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
	 * @return the agentId
	 */
	public String getAgentId() {
		return agentId;
	}
	/**
	 * @param agentId the agentId to set
	 */
	public void setAgentId(String agentId) {
		this.agentId = agentId;
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
	 * @return the lastTimestamp
	 */
	public long getLastTimestamp() {
		return lastTimestamp;
	}
	/**
	 * @param lastTimestamp the lastTimestamp to set
	 */
	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}
	/**
	 * @return the monitorLevel
	 */
	public int getMonitorLevel() {
		return monitorLevel;
	}
	/**
	 * @param monitorLevel the monitorLevel to set
	 */
	public void setMonitorLevel(int monitorLevel) {
		this.monitorLevel = monitorLevel;
	}
	/**
	 * @return the appenderName
	 */
	public String getAppenderName() {
		return appenderName;
	}
	/**
	 * @param appenderName the appenderName to set
	 */
	public void setAppenderName(String appenderName) {
		this.appenderName = appenderName;
	}
	/**
	 * @return the pollingTime
	 */
	public String getPollingTime() {
		return pollingTime;
	}
	/**
	 * @param pollingTime the pollingTime to set
	 */
	public void setPollingTime(String pollingTime) {
		this.pollingTime = pollingTime;
	}
	
	
}
