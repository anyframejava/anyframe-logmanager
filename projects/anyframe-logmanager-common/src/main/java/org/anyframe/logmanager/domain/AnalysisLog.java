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

import java.text.SimpleDateFormat;

/**
 * @author Jaehyoung Eum
 *
 */
public class AnalysisLog extends BaseLog {
	private String agentId;
	private String appName;
	private String clientIp;
	private String userId;
	private String className;
	private String methodName;
	private String category;
	
	@SuppressWarnings("unused")
	private String timestampString;
	
	/**
	 * @return the timestampString
	 */
	public String getTimestampString() {
		return super.getTimestamp() != null ? (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss, SSS")).format(super.getTimestamp()) : null;
	}
	
	/**
	 * @param timestampString the timestampString to set
	 */
	public void setTimestampString(String timestampString) {
		this.timestampString = timestampString;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}
	
	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
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
	 * @return the apName
	 */
	public String getAppName() {
		return appName;
	}
	
	/**
	 * @param apName the apName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}
	
	/**
	 * @param clientIp the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.domain.BaseLog#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + "\nAnalysisLog [agentId=" + agentId + ", appName=" + appName + ", clientIp=" + clientIp
		+ ", userId=" + userId + ", className=" + className + ", methodName=" + methodName + "]";
	}
}
