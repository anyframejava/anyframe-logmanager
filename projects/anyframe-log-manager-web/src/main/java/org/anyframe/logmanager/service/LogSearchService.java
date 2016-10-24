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

import org.anyframe.logmanager.domain.AnalysisLog;
import org.anyframe.logmanager.domain.BaseLog;
import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.domain.LogSearchCondition;

/**
 * @author Jaehyoung Eum
 *
 */
public interface LogSearchService {
	
	/**
	 * get list of base log data
	 * 
	 * @param searchCondition
	 * @return
	 * @throws Exception
	 */
	public List<BaseLog> searchBaseLog(LogSearchCondition searchCondition) throws Exception;
	
	/**
	 * get list of analysis log data
	 * 
	 * @param searchCondition
	 * @return
	 * @throws Exception
	 */
	public List<AnalysisLog> searchAnalysisLog(LogSearchCondition searchCondition) throws Exception;
	
	/**
	 * get active log application list
	 * 
	 * @param param
	 * @param devVisible
	 * @return
	 * @throws Exception
	 */
	public List<LogApplication> getActiveLogApplicationList() throws Exception;
}
