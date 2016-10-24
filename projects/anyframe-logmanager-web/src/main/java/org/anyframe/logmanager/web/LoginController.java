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


import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.anyframe.logmanager.domain.Account;
import org.anyframe.logmanager.service.AccountService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * This LoginController class is a Controller class to provide user login
 * functionality.
 * 
 * @author Heewon Jung
 */
@Controller("loginController")
public class LoginController {

	@Inject
	@Named("accountService")
	private AccountService accountService;
	
	@Inject
	private MessageSource messageSource;

	/**
	 * 
	 * @param account
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/login.do")
	public String login(HttpServletRequest request, Account account, Model model, HttpSession session) throws Exception {

		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Account resultAccount = accountService.get(account.getUserId());
		String language = request.getParameter("language");
	      Locale locale = null;
	         if( language.equals("ko") ){
	             locale = Locale.KOREAN;
	         } else if( language.equals("en") ) {
	             locale = Locale.ENGLISH;
	         } else {
	             locale = Locale.KOREAN;
	         }
	 
		if (resultAccount != null && resultAccount.getPassword().equals(account.getPassword())) {
			session.setAttribute("loginAccount", resultAccount);
			session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
			return "redirect:/logManager.do?method=analysis4gridForm";
		} else {
			model.addAttribute("rejectMessage", messageSource.getMessage("logmanager.runtime.Logincontroller.login.authentication", new String[]{}, currenLocale));
			return "forward:/welcome.do?method=view";
		}
	}
}
