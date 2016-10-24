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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.datatype.SearchVO;
import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.Account;
import org.anyframe.logmanager.service.AccountService;
import org.anyframe.pagination.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This AccountController class is a Controller class to provide account crud
 * functionality.
 * 
 * @author Heewon Jung
 */
@Controller("accountController")
@RequestMapping("/account.do")
public class AccountController {

	@Inject
	@Named("accountService")
	private AccountService accountService;
	
	
	/**
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=view")
	public String view(Model model) throws Exception{
		model.addAttribute("search", new SearchVO());
		model.addAttribute("account",new Account());
		model.addAttribute("searchUserTypeList", LogManagerConstant.SEARCH_USER_TYPES);
		model.addAttribute("userTypeList", LogManagerConstant.DETAIL_USER_TYPES);
		return "logmanager/accountList";
	}
	
	/**
	 * check for account duplication
	 * @param userId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=checkAccountExist")
	public String checkAccountExist(@RequestParam("userId") String userId, Model model) throws Exception{
		Account account = accountService.get(userId);
		if(account == null) {
			model.addAttribute("isExist", false);
		}else{
			model.addAttribute("isExist", true);
		}
		return "jsonView";
	}
	
	/**
	 * 
	 * @param account
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=list")
	public String list(SearchVO search, Model model)
			throws Exception {
		if(search.getPageIndex() <=0)
			search.setPageIndex(1);
		Page resultPage = accountService.getList(search);
		Map<String, Object> jsonModel = new HashMap<String, Object>();

		jsonModel.put("page", resultPage.getCurrentPage());
		jsonModel.put("total", resultPage.getTotalCount());
		jsonModel.put("records", resultPage.getMaxPage());
		jsonModel.put("rows", resultPage.getList());

		model.addAllAttributes(jsonModel);
		return "jsonView";
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=remove")
	public String remove(@RequestParam("userId") String userId) throws Exception {
		accountService.remove(userId);
		return "jsonView";
	}


	/**
	 * @param session
	 * @param model
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=save")
	public String save(Account account) throws Exception {
		accountService.save(account);
		return "jsonView";
	}
	
	/**
	 * 
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params= "method=create")
	public String create(Account account) throws Exception{
		accountService.create(account);
		return "jsonView";
	}
}
