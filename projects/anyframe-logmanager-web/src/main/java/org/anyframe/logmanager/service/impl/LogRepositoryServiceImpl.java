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
package org.anyframe.logmanager.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.LogRepository;
import org.anyframe.logmanager.service.LogRepositoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
@Service("logRepositoryService")
public class LogRepositoryServiceImpl implements LogRepositoryService {

	@Value("#{contextProperties['repository.type'] ?: mongodb}")
	String repositoryType;
	
	@Inject
	@Named("logRepositoryDao")
	private LogRepositoryDao dao;
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogRepositoryService#createLogRepository(org.anyframe.logmanager.domain.LogRepository)
	 */
	public void saveLogRepository(LogRepository param) throws Exception {
		if(param.getId() == null) {
			dao.createLogCollection(param);	
		}
		dao.saveLogRespository(param);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogRepositoryService#removeLogRepository(org.anyframe.logmanager.domain.LogRepository)
	 */
	public void removeLogRepository(LogRepository param) throws Exception {
		dao.deleteLogRepository(param.getId());
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogRepositoryService#getLogRepository(org.anyframe.logmanager.domain.LogRepository)
	 */
	public LogRepository getLogRepository(LogRepository param) throws Exception {
		return dao.getLogRepositoryById(param.getId());
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogRepositoryService#getLogRepositoryList(org.anyframe.logmanager.domain.LogRepository)
	 */
	public List<LogRepository> getActiveLogRepositoryList(String userType) throws Exception {
		int monitorLevel = 0;
		if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
			monitorLevel = LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE;
		}else{
			monitorLevel = LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY;			
		}
		return dao.getLogRepositoryList(true, monitorLevel);
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogRepositoryService#getActiveLogRepositoryNameList()
	 */
	public List<String> getActiveLogRepositoryNameList(String userType) throws Exception {
		List<String> nameList = new ArrayList<String>();
		int monitorLevel = 0;
		if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
			monitorLevel = LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE;
		}else{
			monitorLevel = LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY;			
		}
		List<LogRepository> list = dao.getLogRepositoryList(true, monitorLevel);
		if(list != null) {
			Iterator<LogRepository> i = list.iterator();
			while(i.hasNext()) {
				nameList.add(i.next().getRepositoryName());
			}
		}
		return nameList; 
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogRepositoryService#getAllLogRepositoryList(org.anyframe.logmanager.domain.LogRepository)
	 */
	public List<LogRepository> getAllLogRepositoryList() throws Exception {
		return dao.getAllLogRepositoryList();
	}

}
