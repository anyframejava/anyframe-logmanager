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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.common.LogManagerException;
import org.anyframe.logmanager.domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * This class is test for AccountService
 * @author Heewon Jung
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:./src/main/resources/spring/context-*.xml" })
public class AccountServiceTest {
	
	@Inject
	@Named("accountService")
	private AccountService accountService;
	
	@Test
	@Rollback(value = true)
	public void manageAccount() throws Exception {
		// 1. create a new account
		Account account = getAccount();
		
		accountService.create(account);
		
		// 2. assert - create
		account = accountService.get(account.getUserId());
		
		assertNotNull("fail to fetch a account", account);
		assertEquals("fail to compare a user name", "test", account.getUserName());
		
		String userName = "test 2" + System.currentTimeMillis();
		
		// 3. update a userName of account
		account.setUserName(userName);
		accountService.save(account);

		// 4. assert - update
		account = accountService.get(account.getUserId());
		assertNotNull("fail to fetch a account", account);
		assertEquals("fail to compare a updated user name", userName, account.getUserName());

		// 5. remove a account
		accountService.remove(account.getUserId());

		// 6. assert - remove
		try {
			account = accountService.get(account.getUserId());
		} catch (LogManagerException e) {
			// we expect this exception
			assertNotNull("fail to remove a movie", e);
		}
	}

	private Account getAccount() throws Exception {
		Account account = new Account();
		account.setUserId("test");
		account.setUserName("test");
		account.setPassword("test123");
		account.setUserType("Developer");
		
		return account;
	}
}
