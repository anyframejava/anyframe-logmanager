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

import org.anyframe.logmanager.domain.LogCollection;
import org.anyframe.logmanager.domain.LogCollectionResult;

/**
 * Log Collection Service Interface
 *
 * @author jaehyoung.eum
 *
 */
public interface LogCollectionService {
	
	/**
	 * save log collection infos
	 * @param param
	 * @throws Exception
	 */
	public void saveLogCollection(LogCollection param) throws Exception;
	
	/**
	 * get log collection list by appName, agentId
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<LogCollection> getLogCollectionListByAppName(LogCollection param) throws Exception;
	
	/**
	 * get log collection by id
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public LogCollection getLogCollection(String id) throws Exception;

	/**
	 * delete log application
	 * 
	 * @param collectionId
	 * @throws Exception
	 */
	public void deleteLogCollection(String id) throws Exception;
	
	/**
	 * @param collectionId
	 * @return
	 * @throws Exception
	 */
	public List<LogCollectionResult> getLogCollectionResultList(String collectionId) throws Exception;

	/**
	 * @param collectionId
	 * @param iterationOrder
	 * @param messageType
	 * @return
	 * @throws Exception
	 */
	public List<String> getResultMessage(String collectionId, int iterationOrder, String messageType) throws Exception;
	
}
