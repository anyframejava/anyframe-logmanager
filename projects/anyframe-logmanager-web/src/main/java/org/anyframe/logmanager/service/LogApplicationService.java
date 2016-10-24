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
package org.anyframe.logmanager.service;

import java.util.List;

import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.LogAppender;
import org.anyframe.logmanager.domain.LogApplication;

/**
 * @author Jaehyoung Eum
 *
 */
public interface LogApplicationService {
	
	/**
	 * call log application list at log manager start-time
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @deprecated 
	 */
	public void getInitLogApplication() throws Exception;
	
	/**
	 * get log application list
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<LogApplication> getLogApplicationList(LogApplication param) throws Exception;
	
	/**
	 * load log4j.xml file
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication loadLog4jXml(LogApplication param) throws Exception;

	/**
	 * get log application info
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLogApplication(LogApplication param) throws Exception;
	
	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLogApplicationById(LogApplication param) throws Exception;

	/**
	 * Check for Log Application Exist
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication checkLogApplicationExist(LogApplication param) throws Exception;
	
	/**
	 * save log application info
	 * 
	 * @param param
	 * @param loggers
	 * @param appenders
	 * @throws Exception
	 * @deprecated instead use saveLogApplication(LogApplication, String[], int[], String[])
	 */
	public void saveLogApplication(LogApplication param, String[] loggers, String[] appenders) throws Exception;
	
	
	/**
	 * save log application info
	 * 
	 * @param param
	 * @param appenders
	 * @param pollingTimes
	 * @param monitorLevels
	 * @throws Exception
	 */
	public void saveLogApplication(LogApplication param, String[] appenders, String[] pollingTimes, int[] monitorLevels, boolean[] fileAppenders, String[] collectionNames, int[] status) throws Exception;

	/**
	 * get appender list of a log application
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @deprecated instead use getAppenderList String, String, String)
	 */
	public List<Appender> getAppenderList(LogApplication param, String userType) throws Exception;
	
	/**
	 * @param agentId
	 * @param appName
	 * @param userType
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList(String agentId, String appName, String userType) throws Exception;
	
	/**
	 * @param appName
	 * @param userType
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList(String appName, String userType) throws Exception;
	
	/**
	 * @param userType
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList(String userType) throws Exception;

	/**
	 * delete log application info
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void deleteApplication(LogApplication param) throws Exception;

	/**
	 * reload log application info
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void reloadApplication(LogApplication param) throws Exception;

	/**
	 * @param collectionName
	 * @param userType
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getLogAppenderListByCollection(String collectionName, String userType) throws Exception;
	
}
