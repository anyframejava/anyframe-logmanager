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
package org.anyframe.logmanager.service;

import java.util.List;

import org.anyframe.logmanager.domain.LogAgent;
import org.anyframe.logmanager.domain.LogApplication;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public interface LogAgentService {
	
	/**
	 * @param agents
	 * @throws Exception
	 */
	public void refreshAgent(List<LogAgent> agents) throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<LogAgent> getLogAgentList() throws Exception;
	
	/**
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public List<LogAgent> getLogAgentList(int status) throws Exception;
	
	/**
	 * @param agentId
	 * @return
	 * @throws Exception
	 */
	public LogAgent getLogAgent(String agentId) throws Exception;
	
	/**
	 * @param agentId
	 * @throws Exception
	 */
	public void deleteLogAgent(String agentId) throws Exception;

	/**
	 * @param agentId
	 * @throws Exception
	 */
	public void restartLogAgent(String agentId) throws Exception;
	
	/**
	 * @param loggingPolicyFilePath
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLoggingPolicyFileInfo(String agentId, String loggingPolicyFilePath, String loggingFramework) throws Exception;
	
	/**
	 * @param agentId
	 * @param loggingPolicyFilePath
	 * @return
	 * @throws Exception
	 */
	public String getLoggingPolicyFileInfoString(String agentId, String loggingPolicyFilePath, String loggingFramework) throws Exception;

	/**
	 * @param param
	 * @param loggingPolicyFileText
	 * @throws Exception
	 */
	public void saveLoggingPolicyFileText(LogApplication param, String loggingPolicyFileText) throws Exception;

	/**
	 * @param param
	 * @throws Exception
	 */
	public void saveLoggingPolicyFile(LogApplication param) throws Exception;

}
