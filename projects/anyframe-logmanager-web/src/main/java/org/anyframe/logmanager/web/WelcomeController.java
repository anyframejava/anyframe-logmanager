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
package org.anyframe.logmanager.web;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.logmanager.domain.Account;
import org.anyframe.logmanager.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This WelcomeController class is a Controller class to provide show welcome
 * page functionality.
 * 
 * @author Heewon Jung
 */
@Controller
@RequestMapping("/welcome.do")
public class WelcomeController {

	@Inject
	@Named("accountService")
	private AccountService accountService;

	/**
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=view")
	public String checkAdminAccountExist(Model model) throws Exception {
		boolean isAdminExist = accountService.checkAdminExist();
		model.addAttribute("isAdminExist", isAdminExist);
		return "forward:/anyframe.jsp";
	}

	/**
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=save")
	public String create(Account account) throws Exception {
		accountService.create(account);
		return "jsonView";
	}
}
