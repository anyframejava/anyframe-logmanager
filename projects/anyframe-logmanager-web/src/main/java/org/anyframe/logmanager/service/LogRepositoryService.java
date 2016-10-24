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
package org.anyframe.logmanager.service;

import java.util.List;

import org.anyframe.logmanager.domain.LogRepository;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public interface LogRepositoryService {

	/**
	 * @param param
	 * @throws Exception
	 */
	public void saveLogRepository(LogRepository param) throws Exception;
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void removeLogRepository(LogRepository param) throws Exception;
	
	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogRepository getLogRepository(LogRepository param) throws Exception;
	
	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<LogRepository> getActiveLogRepositoryList(String userType) throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<String> getActiveLogRepositoryNameList(String userType) throws Exception;
	
	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<LogRepository> getAllLogRepositoryList() throws Exception;
}
