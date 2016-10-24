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

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This LogoutController class is a Controller class to provide user logout
 * functionality.
 * 
 * @author Heewon Jung
 */
@Controller("logoutCountroller")
public class LogoutController {
	
	
	/**
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/logout.do")
	public String logout(HttpSession session) throws Exception {
		/*Account account = (Account)session.getAttribute("loginAccount");
		if(account == null){
			throw new Exception("User information is not exist!");
		}
		else
			session.removeAttribute("loginAccount");*/
		session.invalidate();
		
		return "redirect:/welcome.do?method=view"; 
	}
}
