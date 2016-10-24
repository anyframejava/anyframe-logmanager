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

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.datatype.SearchVO;
import org.anyframe.logmanager.domain.Account;
import org.anyframe.logmanager.service.AccountService;
import org.anyframe.pagination.Page;
import org.springframework.stereotype.Service;

/**
 * This class is a Implementation class to provide account crud functionality.
 * @author Heewon Jung
 *
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService{

	@Inject
	@Named("accountDao")
	private AccountDao accountDao;

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.AccountService#create(org.anyframe.logmanager.domain.Account)
	 */
	public void create(Account account) throws Exception {
		accountDao.create(account);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.AccountService#save(org.anyframe.logmanager.domain.Account)
	 */
	public void save(Account account) throws Exception {
		accountDao.save(account);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.AccountService#remove(java.lang.String)
	 */
	public void remove(String userId) throws Exception {
		accountDao.remove(userId);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.AccountService#get(java.lang.String)
	 */
	public Account get(String userId) throws Exception {
		return accountDao.get(userId);
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.AccountService#getList(org.anyframe.datatype.SearchVO)
	 */
	public Page getList(SearchVO search) throws Exception {
		return accountDao.getList(search);
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.service.AccountService#checkAdminExist()
	 */
	public boolean checkAdminExist() throws Exception {
		return accountDao.checkAdminExist();
	}
}
