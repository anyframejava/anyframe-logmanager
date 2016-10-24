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

import org.anyframe.logmanager.domain.LogApplication;

/**
 * This is LogApplicationService class.
 * 
 * @author Jaehyoung Eum
 */
public interface LogApplicationService {

	/**
	 * get log application list
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<LogApplication> getLogApplicationList(LogApplication param) throws Exception;

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
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication checkLogApplicationExist(LogApplication param) throws Exception;

	/**
	 * save log application info
	 * 
	 * @param param
	 * @param appenders
	 * @param pollingTimes
	 * @param monitorLevels
	 * @throws Exception
	 */
	public void saveLogApplication(LogApplication param) throws Exception;


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


}
