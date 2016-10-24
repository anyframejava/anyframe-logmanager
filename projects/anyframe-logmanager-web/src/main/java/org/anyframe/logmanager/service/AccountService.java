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
package org.anyframe.logmanager.service;

import org.anyframe.datatype.SearchVO;
import org.anyframe.logmanager.domain.Account;
import org.anyframe.pagination.Page;

/**
 * This AccountService class is an Interface class to provide account crud
 * functionality.
 * 
 * @author Heewon Jung
 */
public interface AccountService {

	/**
	 * Create account
	 * 
	 * @param account
	 * @throws Exception
	 */
	void create(Account account) throws Exception;

	/**
	 * Update account
	 * 
	 * @param account
	 * @throws Exception
	 */
	void save(Account account) throws Exception;

	/**
	 * Remove account
	 * 
	 * @param userId
	 * @throws Exception
	 */
	void remove(String userId) throws Exception;

	/**
	 * Get account by userId
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	Account get(String userId) throws Exception;

	/**
	 * Get account list with paging
	 * 
	 * @param search
	 * @return
	 * @throws Exception
	 */
	Page getList(SearchVO search) throws Exception;

	/**
	 * Check admin
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean checkAdminExist() throws Exception;
}
