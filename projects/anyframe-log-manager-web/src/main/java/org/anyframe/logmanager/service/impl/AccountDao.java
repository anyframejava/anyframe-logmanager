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

import org.anyframe.datatype.SearchVO;
import org.anyframe.logmanager.domain.Account;
import org.anyframe.pagination.Page;
import org.anyframe.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * This AccountDao class is a DAO class to provide account crud functionality.
 * 
 * @author Heewon Jung
 */
@Repository("accountDao")
public class AccountDao {
	
	@Value("#{mongoProperties['mongo.account.collectionName'] ?: Account}")
	String accountCollection;
	
	@Value("#{contextProperties['pageSize'] ?: 10}")
	int pageSize;

	@Value("#{contextProperties['pageUnit'] ?: 10}")
	int pageUnit;
	
	@Inject
	@Named("mongoTemplate")
	private MongoOperations mongoOperations;
	
	
	/**
	 * Create account.
	 * @param account
	 * @throws Exception
	 */
	public void create(Account account) throws Exception{
		mongoOperations.insert(account, accountCollection);
	}
	
	/**
	 * Remove account.
	 * @param userId
	 * @throws Exception
	 */
	public void remove(String userId) throws Exception{
		mongoOperations.remove(query(where("userId").is(userId)), accountCollection);
	}
	
	/**
	 * Get account by userId.
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Account get(String userId) throws Exception{
		return mongoOperations.findOne(query(where("userId").is(userId)), Account.class, accountCollection);
	}
	
	/**
	 * Update account.
	 * @param account
	 * @throws Exception
	 */
	public void save(Account account) throws Exception{
		//mongoOperations.updateFirst(query(where("userId").is(account.getUserId())), update("", ""), accountCollection);
		mongoOperations.updateFirst(query(where("userId").is(account.getUserId())), update("userName", account.getUserName()), accountCollection);
		mongoOperations.updateFirst(query(where("userId").is(account.getUserId())), update("password", account.getPassword()), accountCollection);
		mongoOperations.updateFirst(query(where("userId").is(account.getUserId())), update("userType", account.getUserType()), accountCollection);
	}

	/**
	 * 
	 * get account list with paging
	 * @param search
	 * @return
	 * @throws Exception
	 */
	public Page getList(SearchVO search) throws Exception{
		Criteria criteria = new Criteria();
		Query query = new Query();
		// user id
		if(StringUtil.isNotEmpty(search.getSearchKeyword())) {
			criteria = where("userId").is(search.getSearchKeyword());
		}
		
		// user type
		if(StringUtil.isNotEmpty(search.getSearchCondition()) && !search.getSearchCondition().equals("All")) {
			if(criteria==null){
				criteria = where("userType").is(search.getSearchCondition());
			}
			else
				criteria = criteria.and("userType").is(search.getSearchCondition());
		}
		
		if(criteria!=null)
			query = query(criteria);
		// end where
		
		//paging
		if(search.getPageIndex() > 0) {
			query = query.skip((search.getPageIndex() - 1) * pageSize).limit(pageSize);
		}
		int totalCount = (int) mongoOperations.count(query, accountCollection);
		
		List<Account> resultList = mongoOperations.find(query, Account.class, accountCollection);
		//set page
		Page page = new Page();
		page.setTotalCount(totalCount);
		//page.setList(resultList);
		page.setObjects(resultList);
		page.setCurrentPage(search.getPageIndex());
		
		int maxPage = 0;
		maxPage = totalCount / pageSize;
		if((totalCount % pageSize) > 0) maxPage++;
		page.setMaxPage(maxPage);
		
		query.sort().on("userId", Order.ASCENDING);
		return page;
	}
	
	/**
	 * check admin account.
	 * @return
	 * @throws Exception
	 */
	public boolean checkAdminExist() throws Exception{
		List<Account> list = mongoOperations.find(query(where("userType").is("Administrator")), Account.class, accountCollection);
		if(list.size() >= 1)
			return true;
		else
			return false;
	}
	
}
