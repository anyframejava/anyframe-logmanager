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
package org.anyframe.logmanager.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.anyframe.logmanager.service.LogApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Log Manager dispatcher servlet
 * 
 * @author Jaehyoung Eum
 * @deprecated no more use it
 */
public class LogManagerDispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1333240608777763945L;
	private static final Logger logger = LoggerFactory.getLogger(LogManagerDispatcherServlet.class);
	
	private LogApplicationService service;
	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig sc) throws ServletException {
		logger.info("LogManagerDispatcherServlet.init() starting...");
		super.init(sc);
		try{
			// dependency lookup
			service = (LogApplicationService)getWebApplicationContext().getBean("logApplicationService");

			// log application initialize
			service.getInitLogApplication();
			
			
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
		}
		
		logger.info("LogManagerDispatcherServlet.init() started.");
	}

}
