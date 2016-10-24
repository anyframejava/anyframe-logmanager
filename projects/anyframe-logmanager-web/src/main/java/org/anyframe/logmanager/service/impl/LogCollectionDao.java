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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.domain.LogCollection;
import org.anyframe.logmanager.domain.LogCollectionResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
@Repository("logCollectionDao")
public class LogCollectionDao {

	@Value("#{mongoProperties['mongo.logcollection.collection'] ?: LogCollection}")
	String logCollectionCollection;
	
	@Value("#{mongoProperties['mongo.logcollectionresult.collection'] ?: LogCollectionResult}")
	String logCollectionResultCollection;
	
	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;
	
	/**
	 * save log collection info
	 * @param param
	 * @throws Exception
	 */
	public void saveLogCollection(LogCollection param) throws Exception {
		mongoOperations.save(param, logCollectionCollection);
	}
	
	/**
	 * get log collection list by appName, agentId
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public List<LogCollection> getLogCollectionListByAppName(LogCollection param) throws Exception {
		return mongoOperations.find(query(where("appName").is(param.getAppName()).and("agentId").is(param.getAgentId())), LogCollection.class, logCollectionCollection);
	}
	
	/**
	 * get log collection by id
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public LogCollection getLogCollectionById(String id) throws Exception {
		return mongoOperations.findById(id, LogCollection.class, logCollectionCollection);
	}

	/**
	 * remove log collection 
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void deleteLogCollection(String id) throws Exception {
		mongoOperations.remove(query(where("_id").is(new ObjectId(id))), logCollectionCollection);
	}
	
	/**
	 * @param appName
	 * @param agentId
	 * @throws Exception
	 */
	public void deleteLogCollectionByAppNameAndAgentId(String appName, String agentId) throws Exception {
		mongoOperations.remove(query(where("appName").is(appName).and("agentId").is(agentId)), logCollectionCollection);
	}

	/**
	 * @param id
	 * @return
	 */
	public LogCollectionResult getLogCollectionResult(String id) throws Exception {
		Query query = query(where("collectionId").is(new ObjectId(id)));
		query.limit(1).sort().on("iterationOrder", Order.DESCENDING);
		return mongoOperations.findOne(query, LogCollectionResult.class, logCollectionResultCollection);
	}

	/**
	 * @param collectionId
	 * @return
	 */
	public List<LogCollectionResult> getLogCollectionResultList(String collectionId) throws Exception {
		Query query = query(where("collectionId").is(new ObjectId(collectionId)));
		query.limit(3).sort().on("iterationOrder", Order.DESCENDING);
		return mongoOperations.find(query, LogCollectionResult.class, logCollectionResultCollection);
	}

	/**
	 * @param collectionId
	 * @param iterationOrder
	 * @param messageType
	 * @return
	 * @throws Exception
	 */
	public LogCollectionResult getResultMessage(String collectionId, int iterationOrder, String messageType) throws Exception {
		return mongoOperations.findOne(query(where("collectionId").is(new ObjectId(collectionId)).and("iterationOrder").is(iterationOrder)), LogCollectionResult.class, logCollectionResultCollection);
	}
}
