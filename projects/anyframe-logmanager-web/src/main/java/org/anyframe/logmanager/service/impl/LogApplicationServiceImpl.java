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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.service.LogApplicationService;
import org.springframework.stereotype.Service;

/**
 * This is LogApplicationServiceImpl class.
 * 
 * @author Jaehyoung Eum
 */
@Service("logApplicationService")
public class LogApplicationServiceImpl implements LogApplicationService {

	@Inject
	@Named("logApplicationDao")
	private LogApplicationDao dao;
	
	@Inject
	@Named("logCollectionDao")
	private LogCollectionDao logCollectionDao;
	
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getLogApplicationList(org.anyframe.logmanager.domain.LogApplication)
	 */
	public List<LogApplication> getLogApplicationList(LogApplication param) throws Exception {
		List<LogApplication> list = dao.getAllLogApplicationList();
		if(list != null) {
			for(int i=0;i<list.size();i++) {
				LogApplication app = (LogApplication)list.get(i);
				app.setStatusMessage(LogManagerConstant.APP_STATUS[app.getStatus()]);
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getLogApplication(org.anyframe.logmanager.domain.LogApplication)
	 */
	public LogApplication getLogApplication(LogApplication param) throws Exception {
		return dao.getLogApplication(param);
	}
	
	public LogApplication getLogApplicationById(LogApplication param) throws Exception {
		// TODO Auto-generated method stub
		return dao.getLogApplicationById(param);
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#checkLogApplicationExist(org.anyframe.logmanager.domain.LogApplication)
	 */
	public LogApplication checkLogApplicationExist(LogApplication param) throws Exception {
		return dao.checkLogApplicationExist(param);
	}

	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#saveLogApplication(org.anyframe.logmanager.domain.LogApplication, java.lang.String[], java.lang.String[], int[], boolean[])
	 */
	public void saveLogApplication(LogApplication param) throws Exception {
		
		dao.saveLogApplication(param);
		
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#deleteApplication(org.anyframe.logmanager.domain.LogApplication)
	 */
	public void deleteApplication(LogApplication param) throws Exception {
		dao.deleteApplication(param);
		logCollectionDao.deleteLogCollectionByAppNameAndAgentId(param.getAppName(), param.getAgentId());
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#reloadApplication(org.anyframe.logmanager.domain.LogApplication)
	 */
	public void reloadApplication(LogApplication param) throws Exception {
		if(param.getStatus() == LogManagerConstant.APP_STATUS_ACTIVE) {
			dao.inactivateApplication(param);
		}else if(param.getStatus() == LogManagerConstant.APP_STATUS_INACTIVE){
			dao.activateApplication(param);
		}
	}

}
