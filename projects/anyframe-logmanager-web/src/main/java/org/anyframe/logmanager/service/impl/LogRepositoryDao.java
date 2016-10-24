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

import org.anyframe.logmanager.domain.LogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
@Repository("logRepositoryDao")
public class LogRepositoryDao {
	
	@Value("#{mongoProperties['mongo.repository.collection'] ?: LogRepository}")
	String repoCollection;
	
	@Value("#{mongoProperties['mongo.repository.size'] ?: 104857600}")
	Integer size;
	
	@Value("#{mongoProperties['mongo.repository.maxDocuments'] ?: 100000}")
	Integer maxDocuments;

	@Value("#{mongoProperties['mongo.repository.isCapped'] ?: true}")
	boolean capped;
	
	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void createLogCollection(LogRepository param) throws Exception {
		if(!mongoOperations.collectionExists(param.getRepositoryName())) {
			CollectionOptions options = new CollectionOptions(size, maxDocuments, capped);
			mongoOperations.createCollection(param.getRepositoryName(), options);
		}
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void dropLogRepository(LogRepository param) throws Exception {
		if(mongoOperations.collectionExists(param.getRepositoryName())) {
			mongoOperations.dropCollection(param.getRepositoryName());
		}
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<LogRepository> getAllLogRepositoryList() throws Exception {
		return mongoOperations.findAll(LogRepository.class, repoCollection);
	}
	
	/**
	 * @param active
	 * @return
	 * @throws Exception
	 */
	public List<LogRepository> getLogRepositoryList(boolean active, int monitorLevel) throws Exception {
		return mongoOperations.find(query(where("active").is(active).and("monitorLevel").gte(monitorLevel)), LogRepository.class, repoCollection);
	}
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public LogRepository getLogRepositoryById(String id) throws Exception {
		return mongoOperations.findById(id, LogRepository.class, repoCollection);
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void saveLogRespository(LogRepository param) throws Exception {
		mongoOperations.save(param, repoCollection);
	}
	
	/**
	 * @param param
	 * @throws Exception
	 */
	public void deleteLogRepository(String id) throws Exception {
		mongoOperations.remove(query(where("_id").is(id)), repoCollection);
	}
	
	
}
