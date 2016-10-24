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

import org.springframework.data.annotation.Id;

/**
 * This is LogApplication class.
 * 
 * @author Jaehyoung Eum
 */
public class LogApplication {

	@Id
	private String id;
	private String appName;
	private String loggingPolicyFilePath;
	private String loggingFramework;
	private int status;
	private String statusMessage;
	private String agentId;

	/**
	 * @return the loggingFramework
	 */
	public String getLoggingFramework() {
		return loggingFramework;
	}

	/**
	 * @param loggingFramework
	 *            the loggingFramework to set
	 */
	public void setLoggingFramework(String loggingFramework) {
		this.loggingFramework = loggingFramework;
	}

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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
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
	 * @param statusMessage
	 *            the statusMessage to set
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
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the loggingPolicyFilePath
	 */
	public String getLoggingPolicyFilePath() {
		return loggingPolicyFilePath;
	}

	/**
	 * @param loggingPolicyFilePath
	 *            the loggingPolicyFilePath to set
	 */
	public void setLoggingPolicyFilePath(String loggingPolicyFilePath) {
		this.loggingPolicyFilePath = loggingPolicyFilePath;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogApplication [id=" + id + ", appName=" + appName + ", loggingPolicyFilePath=" + loggingPolicyFilePath + ", status=" + status + ", statusMessage=" + statusMessage + "]";
	}

}
