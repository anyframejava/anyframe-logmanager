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
	 * @param log4jXmlPath
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLog4jXmlInfo(String agentId, String log4jXmlPath) throws Exception;
	
	/**
	 * @param agentId
	 * @param log4jXmlPath
	 * @return
	 * @throws Exception
	 */
	public String getLog4jXmlInfoString(String agentId, String log4jXmlPath) throws Exception;

	/**
	 * @param param
	 * @param log4jXmlText
	 * @throws Exception
	 */
	public void saveLog4jXml(LogApplication param, String log4jXmlText) throws Exception;

	/**
	 * @param param
	 * @throws Exception
	 */
	public void saveLog4jXml(LogApplication param) throws Exception;
	
}
