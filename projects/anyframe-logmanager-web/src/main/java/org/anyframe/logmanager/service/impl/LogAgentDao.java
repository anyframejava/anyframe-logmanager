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

import org.anyframe.logmanager.domain.LogAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

/**
 * This is LogAgentDao class.
 * 
 * @author Jaehyoung Eum
 */
@Repository("logAgentDao")
public class LogAgentDao {

	@Value("#{mongoProperties['mongo.agent.collection'] ?: LogAgent}")
	String agentCollection;

	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;

	/**
	 * @param agentId
	 * @return
	 * @throws Exception
	 */
	public LogAgent getLogAgent(String agentId) throws Exception {
		return mongoOperations.findOne(query(where("agentId").is(agentId)), LogAgent.class, agentCollection);
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public List<LogAgent> getLogAgentList() throws Exception {
		return mongoOperations.findAll(LogAgent.class, agentCollection);
	}

	/**
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public List<LogAgent> getLogAgentList(int status) throws Exception {
		return mongoOperations.find(query(where("status").is(status)), LogAgent.class, agentCollection);
	}

	/**
	 * @param agentId
	 * @param status
	 * @throws Exception
	 */
	public void updateAgentStatusInfo(String agentId, int status) throws Exception {
		mongoOperations.updateFirst(query(where("agentId").is(agentId)), update("status", status), agentCollection);
	}

	/**
	 * @param agentId
	 * @throws Exception
	 */
	public void deleteAgent(String agentId) throws Exception {
		mongoOperations.remove(query(where("agentId").is(agentId)), agentCollection);
	}
}
