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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Jaehyoung Eum
 */
public class AnalysisLog extends BaseLog {
	private String serverId;
	private String agentId;
	private String appName;
	private String className;
	private String methodName;
	private String logger;
	
	private Map<String, String> mdc;
	
	@SuppressWarnings("unused")
	private String timestampString;
	
	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the logger
	 */
	public String getLogger() {
		return logger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(String logger) {
		this.logger = logger;
	}

	/**
	 * @return the mdc
	 */
	public Map<String, String> getMdc() {
		return mdc;
	}

	/**
	 * @param mdc the mdc to set
	 */
	public void setMdc(Map<String, String> mdc) {
		this.mdc = mdc;
	}

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
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.domain.BaseLog#toString()
	 */
	@Override
	public String toString() {
		Set<String> keySet = null;
		StringBuffer sb = new StringBuffer();
		if(mdc != null) {
			keySet = mdc.keySet();
			sb.append(", mdc=[");
			Iterator<String> i = keySet.iterator();
			while(i.hasNext()) {
				String key = i.next();
				sb.append(", ").append(key).append("=").append(mdc.get(key));
			}
			sb.append("]");
		}
		
		return super.toString() + "\nAnalysisLog [serverId=" + serverId + ", agentId=" + agentId + ", appName=" + appName + ", className=" + className + ", methodName=" + methodName + sb.toString() + "]" ;
	}
}
