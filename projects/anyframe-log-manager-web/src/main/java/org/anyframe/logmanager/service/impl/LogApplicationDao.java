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
import org.anyframe.logmanager.domain.LogApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
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
	public LogApplication getLogApplication(LogApplication param) throws Exception {
		return mongoOperations.findById(param.getId(), LogApplication.class, adminCollection);
	}
	
	/**
	 * Check for Log Application Duplication
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication checkLogApplicationExist(LogApplication param) throws Exception {
		return mongoOperations.findOne(query(where("appName").is(param.getAppName())), LogApplication.class, adminCollection);
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
	 */
	public List<Appender> getAppenderList(LogApplication param) throws Exception {
		return mongoOperations.findOne(query(where("appName").is(param.getAppName())), LogApplication.class, adminCollection).getAppenders();
	}

	/**
	 * delete log application info
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void deleteApplication(LogApplication param) throws Exception {
		mongoOperations.remove(query(where("appName").is(param.getAppName())), adminCollection);
	}

	/**
	 * satus change to inactive of log application
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void inactiveApplication(LogApplication param) throws Exception {
		mongoOperations.updateFirst(query(where("appName").is(param.getAppName())), update("status", LogManagerConstant.APP_STATUS_INACTIVE), adminCollection);
	}

	/**
	 * get log application info by application name
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public LogApplication getLogApplicationByAppName(LogApplication param) throws Exception {
		return mongoOperations.findOne(query(where("appName").is(param.getAppName())), LogApplication.class, adminCollection);
	}
	
}
