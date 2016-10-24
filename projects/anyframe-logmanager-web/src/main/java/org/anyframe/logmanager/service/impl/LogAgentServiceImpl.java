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
package org.anyframe.logmanager.service.impl;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.bundle.service.LogAgentBundleService;
import org.anyframe.logmanager.domain.LogAgent;
import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.service.LogAgentService;
import org.anyframe.logmanager.util.Log4jXmlBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
@Service("logAgentService")
public class LogAgentServiceImpl implements LogAgentService {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(LogAgentServiceImpl.class);
	
	@Inject
	@Named("hessianProxyFactory")
	private HessianProxyFactory proxyfactory;
	
	@Inject
	@Named("logAgentDao")
	private LogAgentDao dao;
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#getLogAgent(java.lang.String)
	 */
	public LogAgent getLogAgent(String agentId) throws Exception {
		return dao.getLogAgent(agentId);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#getLogAgentList()
	 */
	public List<LogAgent> getLogAgentList() throws Exception {
		List<LogAgent> list = dao.getLogAgentList();
		if(list != null) {
			for(int i=0;i<list.size();i++) {
				LogAgent agent = list.get(i);
				agent.setStatusMessage(LogManagerConstant.AGENT_STATUS[agent.getStatus()]);
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#getLogAgentList(int)
	 */
	public List<LogAgent> getLogAgentList(int status) throws Exception {
		List<LogAgent> list = dao.getLogAgentList(status);
		if(list != null) {
			for(int i=0;i<list.size();i++) {
				LogAgent agent = list.get(i);
				agent.setStatusMessage(LogManagerConstant.AGENT_STATUS[status]);
			}
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#refreshAgent(java.util.List)
	 */
	public void refreshAgent(List<LogAgent> agents) throws Exception {
		// TODO Auto-generated method stub
		for(LogAgent agent : agents){
			try{
				LogAgentBundleService service = (LogAgentBundleService) proxyfactory.create(
						Class.forName(LogAgentBundleService.class.getName()), 
						"http://" + agent.getAgentId() + "/LogAgentService",
						this.getClass().getClassLoader());
				if(service.ping("ping check!!") != null) {
					// update agent status info
					dao.updateAgentStatusInfo(agent.getAgentId(), LogManagerConstant.AGENT_STATUS_ACTIVE);
				}else{
					// update agent status info
					dao.updateAgentStatusInfo(agent.getAgentId(), LogManagerConstant.AGENT_STATUS_INACTIVE);
					logger.warn(" ping return of " + agent.getAgentId() + " is null.");
				}
			}catch(Exception e){
				// update agent status info
				dao.updateAgentStatusInfo(agent.getAgentId(), LogManagerConstant.AGENT_STATUS_INACTIVE);
				logger.warn(e.getMessage() + " to " + agent.getAgentId());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#deleteLogAgent(java.lang.String)
	 */
	public void deleteLogAgent(String agentId) throws Exception {
		dao.deleteAgent(agentId);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#restartLogAgent(java.lang.String)
	 */
	public void restartLogAgent(String agentId) throws Exception {
		LogAgentBundleService service = (LogAgentBundleService) proxyfactory.create(
				Class.forName(LogAgentBundleService.class.getName()), 
				"http://" + agentId + "/LogAgentService",
				this.getClass().getClassLoader());
		service.restartHarvestManager();
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#getLog4jXmlInfo(java.lang.String)
	 */
	public LogApplication getLog4jXmlInfo(String agentId, String log4jXmlPath) throws Exception {
		LogAgentBundleService service = (LogAgentBundleService) proxyfactory.create(
				Class.forName(LogAgentBundleService.class.getName()), 
				"http://" + agentId + "/LogAgentService",
				this.getClass().getClassLoader());
		InputStream is = service.getLog4jXmlInfo(log4jXmlPath);
		
		Log4jXmlBuilder builder = new Log4jXmlBuilder(is);
		
//		String xml = builder.getXmlString();
		
		LogApplication logApplication = new LogApplication();
		logApplication.setAppenders(builder.getLog4jAppender());
		logApplication.setLoggers(builder.getLog4jLogger());
		logApplication.setRoot(builder.getLog4jRoot());
		return logApplication;
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#getLog4jXmlInfoString(java.lang.String, java.lang.String)
	 */
	public String getLog4jXmlInfoString(String agentId, String log4jXmlPath) throws Exception {
		LogAgentBundleService service = (LogAgentBundleService) proxyfactory.create(
				Class.forName(LogAgentBundleService.class.getName()), 
				"http://" + agentId + "/LogAgentService",
				this.getClass().getClassLoader());
		InputStream is = service.getLog4jXmlInfo(log4jXmlPath);
		
		Log4jXmlBuilder builder = new Log4jXmlBuilder(is);
		
		return builder.getXmlString();
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#saveLog4jXml(org.anyframe.logmanager.domain.LogApplication, java.lang.String)
	 */
	public void saveLog4jXml(LogApplication param, String log4jXmlText) throws Exception {
		LogAgentBundleService service = (LogAgentBundleService) proxyfactory.create(
				Class.forName(LogAgentBundleService.class.getName()), 
				"http://" + param.getAgentId() + "/LogAgentService",
				this.getClass().getClassLoader());
		
		service.saveLog4jXmlInfo(param.getLog4jXmlPath(), log4jXmlText);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogAgentService#saveLog4jXml(org.anyframe.logmanager.domain.LogApplication)
	 */
	public void saveLog4jXml(LogApplication param) throws Exception {
		// TODO Auto-generated method stub
		LogAgentBundleService service = (LogAgentBundleService) proxyfactory.create(
				Class.forName(LogAgentBundleService.class.getName()), 
				"http://" + param.getAgentId() + "/LogAgentService",
				this.getClass().getClassLoader());
		Log4jXmlBuilder builder = new Log4jXmlBuilder(service.getLog4jXmlInfo(param.getLog4jXmlPath()));
		
		builder.setAppenderNodeList(param.getAppenders());
		builder.setLoggerNodeList(param.getLoggers());
		builder.setRootNode(param.getRoot());
		
		String log4jXmlText = builder.getXmlString();
		service.saveLog4jXmlInfo(param.getLog4jXmlPath(), log4jXmlText);
	}

}
