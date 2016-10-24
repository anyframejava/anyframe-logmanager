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
package org.anyframe.logmanager.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.domain.LogDataMap;
import org.anyframe.logmanager.domain.LogSearchCondition;
import org.anyframe.logmanager.service.LogSearchService;
import org.springframework.stereotype.Service;

/**
 * This is LogSearchServiceImpl class.
 * 
 * @author Jaehyoung Eum
 */
@Service("logSearchService")
public class LogSearchServiceImpl implements LogSearchService {

	@Inject
	@Named("logSearchDao")
	private LogSearchDao dao;

	@Inject
	@Named("logApplicationDao")
	private LogApplicationDao appDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.anyframe.logmanager.service.LogSearchService#searchAnalysisLog(org
	 * .anyframe.logmanager.domain.SearchCondition)
	 */
	@SuppressWarnings("unchecked")
	public List<LogDataMap> searchAnalysisLog(LogSearchCondition searchCondition) throws Exception {
//		return (List<AnalysisLog>) dao.searchLog(searchCondition, AnalysisLog.class, searchCondition.getCollection());
		return (List<LogDataMap>) dao.searchLog(searchCondition, LogDataMap.class, searchCondition.getCollection());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.anyframe.logmanager.service.LogSearchService#getLogApplicationList
	 * (org.anyframe.logmanager.domain.LogApplication, boolean)
	 */
	public List<String> getActiveLogApplicationNameList() throws Exception {
		List<String> appNameList = new ArrayList<String>();
		List<LogApplication> appList = appDao.getActiveLogApplicationList();
		int size = appList.size();
		for (int i = 0; i < size; i++) {
			appNameList.add(appList.get(i).getAppName());
		}
		return appNameList;
	}


	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogSearchService#getLogData(java.lang.String, java.lang.String)
	 */
	public LogDataMap getLogData(String id, String repositoryName) throws Exception {
		return dao.getLogData(id, repositoryName);
	}
}
