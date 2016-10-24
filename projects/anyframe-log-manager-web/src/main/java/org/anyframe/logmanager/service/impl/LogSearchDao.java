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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.LogSearchCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * Log Manager log search mongo-db dao class
 * 
 * @author Jaehyoung Eum
 *
 */
@Repository("logSearchDao")
public class LogSearchDao {
	
	private static Logger logger = LoggerFactory.getLogger(LogSearchDao.class);
	
	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;
	
	
	/**
	 * log search from mongo-db
	 * 
	 * @param searchCondition
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List searchLog(LogSearchCondition searchCondition, Class c, String collectionName) throws Exception{
		Query query = null;
		if(searchCondition.isMatchedLogOnly()) {
			query = query(generateSearchConditionMatchedLogOnly(searchCondition));
		}else{
			query = query(generateSearchCondition(searchCondition));
		}
		
		if(searchCondition.getPageIndex() != -1) {
			try{
				searchCondition.setTotalCount(mongoOperations.count(query, collectionName));
			}catch(IllegalArgumentException e){
				searchCondition.setTotalCount(0);
				return null;
			}
			query = query.skip((searchCondition.getPageIndex() - 1) * searchCondition.getPageSize()).limit(searchCondition.getPageSize());
		}else{
			query = query.limit(LogManagerConstant.MAX_ROW_LIMIT);
		}
		// sorting
		query.sort().on("timestamp", Order.ASCENDING);
		return mongoOperations.find(query, c, collectionName);
	}
	
	/**
	 * generate search condition for log search from mongo-db
	 * 
	 * @param searchCondition
	 * @return
	 * @throws Exception
	 */
	private Criteria generateSearchCondition(LogSearchCondition searchCondition) throws Exception {
		Criteria criteria = null;
		
		// app name
		if(searchCondition.getAppName() != null && !"".equals(searchCondition.getAppName())) {
			criteria = where("appName").is(searchCondition.getAppName());
		}
		
		// server id
		if(searchCondition.getServerId() != null && !"".equals(searchCondition.getServerId())) {
			criteria = criteria.and("serverId").is(searchCondition.getServerId());
		}
		
		// duration
		if(searchCondition.isUseFromDate() && searchCondition.isUseToDate()) {
			logger.debug("duration type 1 : {} ~ {}", searchCondition.getFromDateTime(), searchCondition.getToDateTime());
			criteria = criteria.and("timestamp").lte(searchCondition.getToDateTime()).gte(searchCondition.getFromDateTime());
		} else if(searchCondition.isUseFromDate()) {
			logger.debug("duration type 2 : {}", searchCondition.getFromDateTime());
			criteria = criteria.and("timestamp").gte(searchCondition.getFromDateTime());
		} else if(searchCondition.isUseToDate()) {
			logger.debug("duration type 3 : {}", searchCondition.getToDateTime());
			criteria = criteria.and("timestamp").lte(searchCondition.getToDateTime());
		}
		
		return criteria;
	}
	
	/**
	 * generate search condition for log search from mongo-db matched log only case
	 * 
	 * @param searchCondition
	 * @return
	 * @throws Exception
	 */
	private Criteria generateSearchConditionMatchedLogOnly(LogSearchCondition searchCondition) throws Exception{
		Criteria criteria = null;
		
		// Log Level Direction
		if(searchCondition.getLogLevelDirection() == LogManagerConstant.LEVEL_THIS_ONLY) {
			criteria = where("level").is(searchCondition.getLevel());	
		}else if(searchCondition.getLogLevelDirection() == LogManagerConstant.LEVEL_INCLUDE_SUB) {
			List<String> levels = new ArrayList<String>();
			boolean flag = false;
			for(int i=4;i>=0;i--) {
				if(searchCondition.getLevel().equals(LogManagerConstant.LEVELS[i])) {
					levels.add(LogManagerConstant.LEVELS[i]);
					flag = true;
				}else{
					if(flag) {
						levels.add(LogManagerConstant.LEVELS[i]);
					}
				}
			}
			criteria = where("level").in(levels);
			
		}else if(searchCondition.getLogLevelDirection() == LogManagerConstant.LEVEL_INCLUDE_HIGH) {
			List<String> levels = new ArrayList<String>();
			boolean flag = false;
			for(int i=0;i<=4;i++) {
				if(searchCondition.getLevel().equals(LogManagerConstant.LEVELS[i])) {
					levels.add(LogManagerConstant.LEVELS[i]);
					flag = true;
				}else{
					if(flag) {
						levels.add(LogManagerConstant.LEVELS[i]);
					}
				}
			}
			criteria = where("level").in(levels);
		}
		
		// server id
		if(searchCondition.getServerId() != null && !"".equals(searchCondition.getServerId())) {
			criteria = criteria.and("serverId").is(searchCondition.getServerId());
		}
		
		// app name
		if(searchCondition.getAppName() != null && !"".equals(searchCondition.getAppName())) {
			criteria = criteria.and("appName").is(searchCondition.getAppName());
		}
		
		// duration
		if(searchCondition.isUseFromDate() && searchCondition.isUseToDate()) {
			criteria = criteria.and("timestamp").lte(searchCondition.getToDateTime()).gte(searchCondition.getFromDateTime());
		} else if(searchCondition.isUseFromDate()) {
			criteria = criteria.and("timestamp").gte(searchCondition.getFromDateTime());
		} else if(searchCondition.isUseToDate()) {
			criteria = criteria.and("timestamp").lte(searchCondition.getToDateTime());
		}
		
		// client IP
		if(searchCondition.getClientIp() != null && !"".equals(searchCondition.getClientIp())) {
			criteria = criteria.and("clientIp").is(searchCondition.getClientIp());
		}
		
		// user id
		if(searchCondition.getUserId() != null && !"".equals(searchCondition.getUserId())) {
			criteria = criteria.and("userId").is(searchCondition.getUserId());
		}
		
		// Class Name
		if(searchCondition.getClassName() != null && !"".equals(searchCondition.getClassName())) {
			criteria = criteria.and("className").regex(searchCondition.getClassName(), "i");
		}
		
		// Method Name
		if(searchCondition.getMethodName() != null && !"".equals(searchCondition.getMethodName())) {
			criteria = criteria.and("methodName").regex(searchCondition.getMethodName(), "i");
		}
		
		// Message Text
		if(searchCondition.getMessageText() != null && !"".equals(searchCondition.getMessageText())) {
			StringTokenizer st = new StringTokenizer(searchCondition.getMessageText());
			int i = 0;
			while(st.hasMoreTokens()){
				String keyword = st.nextToken();
				if(i == 0) {
					criteria = criteria.and("message").regex(keyword, "i");
				}else{
					criteria = criteria.regex(keyword, "i");
				}
				i++;
			}
		}
		return criteria;
	}

}
