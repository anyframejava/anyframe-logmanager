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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.LogAppender;
import org.anyframe.logmanager.domain.LogApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * Log Manager log application operate mongo-db dao class
 * 
 * @author Jaehyoung Eum
 *
 */
@Repository("logApplicationDao")
public class LogApplicationDao {
	
	@Value("#{mongoProperties['mongo.admin.collection'] ?: LogApplication}")
	String adminCollection;
	
	@Value("#{mongoProperties['mongo.appender.collection'] ?: LogAppender}")
	String appenderCollection;
	
	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;
	
	/**
	 * get all log application list
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<LogApplication> getAllLogApplicationList() throws Exception {
		return mongoOperations.findAll(LogApplication.class, adminCollection);
	}
	
	/**
	 * save log application info
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void saveLogApplication(LogApplication param) throws Exception {
		mongoOperations.save(param, adminCollection);
	}

	/**
	 * get log application info
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLogApplicationById(LogApplication param) throws Exception {
		return mongoOperations.findById(param.getId(), LogApplication.class, adminCollection);
	}
	
	/**
	 * Check for Log Application Duplication
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication checkLogApplicationExist(LogApplication param) throws Exception {
		return mongoOperations.findOne(query(where("appName").is(param.getAppName()).and("agentId").is(param.getAgentId())), LogApplication.class, adminCollection);
	}

	/**
	 * get active log application info
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<LogApplication> getActiveLogApplicationList() throws Exception {
		return mongoOperations.find(query(where("status").is(LogManagerConstant.APP_STATUS_ACTIVE)), LogApplication.class, adminCollection);
	}

	/**
	 * get appender list from a log application
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * @deprecated instead use getAppenderList(String, String, int) or getAppenderList(String, int)
	 */
	public List<Appender> getAppenderList(LogApplication param) throws Exception {
		LogApplication logApplication = mongoOperations.findOne(query(where("appName").is(param.getAppName())), LogApplication.class, adminCollection);
		if(logApplication != null) return logApplication.getAppenders();
		else return null;
	}

	/**
	 * @param agentId
	 * @param appName
	 * @param monitorLevel
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList(String agentId, String appName, int monitorLevel) throws Exception {
		return mongoOperations.find(query(where("agentId").is(agentId).and("appName").is(appName).and("monitorLevel").gte(monitorLevel)), LogAppender.class, appenderCollection);
	}
	
	/**
	 * @param appName
	 * @param monitorLevel
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList(String appName, int monitorLevel) throws Exception {
		return mongoOperations.find(query(where("appName").is(appName).and("monitorLevel").gte(monitorLevel)), LogAppender.class, appenderCollection);
	}
	
	/**
	 * @param monitorLevel
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList(int monitorLevel) throws Exception {
		return mongoOperations.find(query(where("monitorLevel").gte(monitorLevel).and("status").is(LogManagerConstant.APPENDER_STATUS_ACTIVE)), LogAppender.class, appenderCollection);
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<LogAppender> getAppenderList() throws Exception {
		return mongoOperations.findAll(LogAppender.class, appenderCollection);
	} 
	
	/**
	 * @param collectionName
	 * @param monitorLevel
	 * @return
	 */
	public List<LogAppender> getAppenderListByCollection(String collectionName, int monitorLevel) throws Exception {
		return mongoOperations.find(query(where("collectionName").is(collectionName).and("status").is(LogManagerConstant.APPENDER_STATUS_ACTIVE).and("monitorLevel").gte(monitorLevel)), LogAppender.class, appenderCollection);
	}
	
	/**
	 * delete log application info
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void deleteApplication(LogApplication param) throws Exception {
		mongoOperations.remove(query(where("appName").is(param.getAppName()).and("agentId").is(param.getAgentId())), adminCollection);
	}

	/**
	 * satus change to inactive of log application
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void inactivateApplication(LogApplication param) throws Exception {
		mongoOperations.updateFirst(query(where("appName").is(param.getAppName()).and("agentId").is(param.getAgentId())), update("status", LogManagerConstant.APP_STATUS_INACTIVE), adminCollection);
	}
	
	/**
	 * @param param
	 */
	public void activateApplication(LogApplication param) {
		mongoOperations.updateFirst(query(where("appName").is(param.getAppName()).and("agentId").is(param.getAgentId())), update("status", LogManagerConstant.APP_STATUS_ACTIVE), adminCollection);
	}

	/**
	 * get log application info by application name
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLogApplication(LogApplication param) throws Exception {
		return mongoOperations.findOne(query(where("appName").is(param.getAppName()).and("agentId").is(param.getAgentId())), LogApplication.class, adminCollection);
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void deleteLogAppender(LogAppender param) throws Exception {
		mongoOperations.remove(query(where("agentId").is(param.getAgentId()).and("appName").is(param.getAppName()).and("appenderName").is(param.getAppenderName())), appenderCollection);
	}
	
	/**
	 * @param agentId
	 * @param appName
	 * @throws Exception
	 */
	public void deleteLogAppender(String agentId, String appName) throws Exception {
		mongoOperations.remove(query(where("agentId").is(agentId).and("appName").is(appName)), appenderCollection);
	}

	/**
	 * @param param
	 * @throws Exception
	 */
	public void updateLogAppender(LogAppender param) throws Exception {
		Update _set = new Update();
		_set.set("pollingTime", param.getPollingTime());
		_set.set("status", param.getStatus());
		_set.set("monitorLevel", param.getMonitorLevel());
		mongoOperations.updateFirst(query(where("agentId").is(param.getAgentId()).and("appName").is(param.getAppName()).and("appenderName").is(param.getAppenderName())), _set, appenderCollection);
	}
	
	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public long checkLogAppender(LogAppender param) throws Exception {
		return mongoOperations.count(query(where("agentId").is(param.getAgentId()).and("appName").is(param.getAppName()).and("appenderName").is(param.getAppenderName())), appenderCollection);
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void inactivateLogAppender(LogAppender param) throws Exception {
		mongoOperations.updateFirst(query(where("agentId").is(param.getAgentId()).and("appName").is(param.getAppName()).and("appenderName").is(param.getAppenderName())), update("status", LogManagerConstant.APPENDER_STATUS_INACTIVE), appenderCollection);
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void inactivateAllLogAppender(LogAppender param) throws Exception {
		mongoOperations.updateMulti(query(where("agentId").is(param.getAgentId()).and("appName").is(param.getAppName())), update("status", LogManagerConstant.APPENDER_STATUS_INACTIVE), appenderCollection);
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void saveLogAppender(LogAppender param) throws Exception {
		mongoOperations.save(param, appenderCollection);
	}

}
