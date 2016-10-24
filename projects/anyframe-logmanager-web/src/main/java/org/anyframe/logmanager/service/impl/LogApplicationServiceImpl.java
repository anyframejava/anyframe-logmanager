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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.LogAppender;
import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.service.LogApplicationService;
import org.anyframe.logmanager.util.Log4jXmlBuilder;
import org.springframework.stereotype.Service;

/**
 * @author Jaehyoung Eum
 *
 */
@Service("logApplicationService")
public class LogApplicationServiceImpl implements LogApplicationService {

	@Inject
	@Named("logApplicationDao")
	private LogApplicationDao dao;
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getInitLogApplication()
	 */
	public void getInitLogApplication() throws Exception {
		/*List<LogApplication> list = null;
		list = dao.getAllLogApplicationList();
		if(list != null) {
			int l = list.size();
			for(int i=0;i<l;i++) {
				LogApplication app = (LogApplication)list.get(i);
				// TODO: log4j xml 파일을 읽어서 logger 및 appender 정보를 다시 셋팅한다.
				if((new File(app.getLog4jXmlPath())).exists()) {
					Log4jXmlBuilder log4jxml = new Log4jXmlBuilder(app.getLog4jXmlPath());
					
					List<Appender> appenders = log4jxml.getLog4jAppender();
					List<Appender> filteredAppenders = new ArrayList<Appender>();
					if(appenders != null) {
						for(int j=0;j<appenders.size();j++){
							Appender appender = (Appender)appenders.get(j);
							if(LogManagerConstant.APPENDER_CLASS.equals(appender.getAppenderClass())) {
								filteredAppenders.add(appender);
							}
						}
					}
					app.setAppenders(filteredAppenders);

					List<Logger> loggers = log4jxml.getLog4jLogger();
					app.setLoggers(loggers);
				}else {
					app.setStatus(LogManagerConstant.APP_STATUS_FAILED);
				}
				
				dao.saveLogApplication(app);
			}
		}*/
	}
	
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
	 * @see org.anyframe.logmanager.service.LogApplicationService#loadLog4jXml(org.anyframe.logmanager.domain.LogApplication)
	 */
	public LogApplication loadLog4jXml(LogApplication param) throws Exception {
		if((new File(param.getLog4jXmlPath())).exists()) {
			Log4jXmlBuilder log4jxml = new Log4jXmlBuilder(param.getLog4jXmlPath());
			
			List<Appender> appenders = log4jxml.getLog4jAppender();
			List<Appender> filteredAppenders = new ArrayList<Appender>();
			if(appenders != null) {
				for(int i=0;i<appenders.size();i++){
					Appender appender = (Appender)appenders.get(i);
					if(LogManagerConstant.APPENDER_CLASS.equals(appender.getAppenderClass())) {
						filteredAppenders.add(appender);
					}
				}
			}
			param.setAppenders(filteredAppenders);
			param.setLoggers(log4jxml.getLog4jLogger());
			param.setRoot(log4jxml.getLog4jRoot());
		}else {
			return null;
		}
		return param;
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
	 * @see org.anyframe.logmanager.service.LogApplicationService#saveLogApplication(org.anyframe.logmanager.domain.LogApplication, java.lang.String[], java.lang.String[])
	 */
	public void saveLogApplication(LogApplication param, String[] loggers, String[] appenders) throws Exception {
		
		/*LogApplication app = loadLog4jXml(param);
		
		if(app != null) {
			app.setId(param.getId());
			List<Appender> appenderList = app.getAppenders();
			List<Logger> loggerList = app.getLoggers();
			for(int i=0;i<appenderList.size();i++) {
				Appender appender = (Appender)appenderList.get(i);

				// appender monitorLevel info update
				for(int j=0;j<appenders.length;j++) {
					//log.debug("appenders[j]={}", appenders[j]);
					String[] appenderArray = appenders[j].split("\\|");
					if(appender.getName().equals(appenderArray[0])){
						boolean f = false;
						for(int k=0;k<appender.getParams().size();k++) {
							Param p = (Param)appender.getParams().get(k);
							if(p.getName().equals(LogManagerConstant.MONITOR_LEVEL)){
								p.setValue(appenderArray[1]);
								f = true;
								break;
							}
						}
						if(!f) {
							Param p = new Param();
							p.setName(LogManagerConstant.MONITOR_LEVEL);
							p.setValue(appenderArray[1]);
							appender.getParams().add(p);
						}
					}
				}
			}
			
			// logger level info update
			for(int j=0;j<loggers.length;j++) {
				String[] loggerArray = loggers[j].split("\\|");
				if("root".equals(loggerArray[0])){
					app.getRoot().setLevel(loggerArray[1]);
					continue;
				}else{
					for(int i=0;i<loggerList.size();i++) {
						Logger logger = (Logger)loggerList.get(i);
						if(logger.getName().equals(loggerArray[0])){
							logger.setLevel(loggerArray[1]);
							break;
						}	
					}
				}
			}
			
			app.setStatus(LogManagerConstant.APP_STATUS_ACTIVE);
			dao.saveLogApplication(app);
			saveLog4jXmlFile(app);
			
		}else {
			throw new Exception("Unable To access : " + param.getLog4jXmlPath());
		}*/
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#saveLogApplication(org.anyframe.logmanager.domain.LogApplication, java.lang.String[], java.lang.String[], int[], boolean[])
	 */
	public void saveLogApplication(LogApplication param, String[] appenders, String[] pollingTimes, int[] monitorLevels, boolean[] fileAppenders, String[] collectionNames, int[] status) throws Exception {
		param.setStatus(LogManagerConstant.APP_STATUS_ACTIVE);
		dao.saveLogApplication(param);
		
		LogAppender logAppender = new LogAppender();
		logAppender.setAgentId(param.getAgentId());
		logAppender.setAppName(param.getAppName());
		
		dao.inactivateAllLogAppender(logAppender);
		
		for(int i=0;i<appenders.length;i++) {
			logAppender.setAppenderName(appenders[i]);
			logAppender.setMonitorLevel(monitorLevels[i]);
			logAppender.setPollingTime(pollingTimes[i]);
			logAppender.setFileAppender(fileAppenders[i]);
			logAppender.setCollectionName(collectionNames[i]);
			logAppender.setStatus(status[i]);	
			if(dao.checkLogAppender(logAppender) > 0) {
				dao.updateLogAppender(logAppender);
			}else{
				dao.saveLogAppender(logAppender);	
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getAppenderList(org.anyframe.logmanager.domain.LogApplication)
	 */
	public List<Appender> getAppenderList(LogApplication param, String userType) throws Exception {
		/*List<Appender> appenders = dao.getAppenderList(param);
		List<Appender> r = new ArrayList<Appender>();
		if(appenders != null) {
			if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
				int l = appenders.size();
				for(int i=0;i<l;i++) {
					Appender appender = appenders.get(i);
					int l2 = appender.getParams().size();
					for(int j=0;j<l2;j++) {
						Param p = appender.getParams().get(j);
						if(LogManagerConstant.MONITOR_LEVEL.equals(p.getName())) {
							if(p.getValue().equals("1")) {
								r.add(appender);
							}
							break;
						}
					}
				}
				return r;
			}else {
				return appenders;
			}
		}else{
			return null;
		}*/
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getAppenderList(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<LogAppender> getAppenderList(String agentId, String appName, String userType) throws Exception {
		if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
			return dao.getAppenderList(agentId, appName, LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE);
		}else{
			return dao.getAppenderList(agentId, appName, LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY);
		}
		
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getAppenderList(java.lang.String, java.lang.String)
	 */
	public List<LogAppender> getAppenderList(String appName, String userType) throws Exception {
		if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
			return dao.getAppenderList(appName, LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE);
		}else{
			return dao.getAppenderList(appName, LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getAppenderList(java.lang.String)
	 */
	public List<LogAppender> getAppenderList(String userType) throws Exception {
		if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
			return dao.getAppenderList(LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE);
		}else{
			return dao.getAppenderList(LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#getLogAppenderListByCollection(java.lang.String, java.lang.String)
	 */
	public List<LogAppender> getLogAppenderListByCollection(String collectionName, String userType) throws Exception {
		if(LogManagerConstant.DETAIL_USER_TYPES[1].equals(userType)) {
			return dao.getAppenderListByCollection(collectionName, LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE);
		}else{
			return dao.getAppenderListByCollection(collectionName, LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY);
		}
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.LogApplicationService#deleteApplication(org.anyframe.logmanager.domain.LogApplication)
	 */
	public void deleteApplication(LogApplication param) throws Exception {
		dao.deleteApplication(param);
		dao.deleteLogAppender(param.getAgentId(), param.getAppName());
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
