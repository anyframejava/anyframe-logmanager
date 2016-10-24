/* 
 * Copyright (C) 2002-2012 the original author or authors.
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

import org.springframework.data.annotation.Id;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogCollection {
	@Id
	private String id;
	private String appName;
	private String agentId;
	private String pathOfLog;
	private String logFileName;
	private int collectionTerm;
	private String collectionTermUnit;
	private String regularExp;
	private String logDataSample;
	private String repositoryName;
	private boolean isRegExp;
	private boolean setLogCollectionActive;
	private int currentStatus = 0; // 0 : Initialization , 1 : normalcy, 2 : abnormal
	
	private long lastUpdate = -1;
	
	private ColumnsInfo[] columnsInfo;
	private LogCollectionResult result;
	
	
	
	/**
	 * @return the isRegExp
	 */
	public boolean isRegExp() {
		return isRegExp;
	}
	/**
	 * @param isRegExp the isRegExp to set
	 */
	public void setRegExp(boolean isRegExp) {
		this.isRegExp = isRegExp;
	}
	/**
	 * @return the result
	 */
	public LogCollectionResult getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(LogCollectionResult result) {
		this.result = result;
	}
	
	/**
	 * @return the currentStatus
	 */
	public int getCurrentStatus() {
		return currentStatus;
	}
	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(int currentStatus) {
		this.currentStatus = currentStatus;
	}
	/**
	 * @return the repositoryName
	 */
	public String getRepositoryName() {
		return repositoryName;
	}
	/**
	 * @param repositoryName the repositoryName to set
	 */
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	/**
	 * @return the lastUpdate
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}
	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the columnsInfo
	 */
	public ColumnsInfo[] getColumnsInfo() {
		return columnsInfo;
	}
	/**
	 * @param columnsInfo the columnsInfo to set
	 */
	public void setColumnsInfo(ColumnsInfo[] columnsInfo) {
		this.columnsInfo = columnsInfo;
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
	 * @return the pathOfLog
	 */
	public String getPathOfLog() {
		return pathOfLog;
	}
	/**
	 * @param pathOfLog the pathOfLog to set
	 */
	public void setPathOfLog(String pathOfLog) {
		this.pathOfLog = pathOfLog;
	}
	/**
	 * @return the logFileName
	 */
	public String getLogFileName() {
		return logFileName;
	}
	/**
	 * @param logFileName the logFileName to set
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	/**
	 * @return the collectionTerm
	 */
	public int getCollectionTerm() {
		return collectionTerm;
	}
	/**
	 * @param collectionTerm the collectionTerm to set
	 */
	public void setCollectionTerm(int collectionTerm) {
		this.collectionTerm = collectionTerm;
	}
	/**
	 * @return the collectionTermUnit
	 */
	public String getCollectionTermUnit() {
		return collectionTermUnit;
	}
	/**
	 * @param collectionTermUnit the collectionTermUnit to set
	 */
	public void setCollectionTermUnit(String collectionTermUnit) {
		this.collectionTermUnit = collectionTermUnit;
	}
	/**
	 * @return the regularExp
	 */
	public String getRegularExp() {
		return regularExp;
	}
	/**
	 * @param regularExp the regularExp to set
	 */
	public void setRegularExp(String regularExp) {
		this.regularExp = regularExp;
	}
	/**
	 * @return the logDataSample
	 */
	public String getLogDataSample() {
		return logDataSample;
	}
	/**
	 * @param logDataSample the logDataSample to set
	 */
	public void setLogDataSample(String logDataSample) {
		this.logDataSample = logDataSample;
	}
	/**
	 * @return the setLogCollectionActive
	 */
	public boolean isSetLogCollectionActive() {
		return setLogCollectionActive;
	}
	/**
	 * @param setLogCollectionActive the setLogCollectionActive to set
	 */
	public void setSetLogCollectionActive(boolean setLogCollectionActive) {
		this.setLogCollectionActive = setLogCollectionActive;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogCollection [id=" + id + ", appName=" + appName + ", agentId=" + agentId + ", pathOfLog=" + pathOfLog + ", logFileName=" + logFileName + ", collectionTerm="
				+ collectionTerm + ", collectionTermUnit=" + collectionTermUnit + ", regularExp=" + regularExp 
				+ ", logDataSample=" + logDataSample + ", isRegExp=" + isRegExp + ", setLogCollectionActive=" + setLogCollectionActive + "]";
	}
}
