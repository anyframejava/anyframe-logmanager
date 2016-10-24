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
package org.anyframe.logmanager.common;

import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * This LogManagerExceptionResolver class is exception resolver.
 * 
 * @author Youngmin Jo

 */
public class LogManagerExceptionResolver extends SimpleMappingExceptionResolver {

	@Inject
	private MessageSource messageSource;
	
	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.SimpleMappingExceptionResolver#doResolveException(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
	 */
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex){
		
		Log logger = LogFactory.getLog(handler.getClass());
		LogManagerException logManagerException;
		
		if (!(ex instanceof LogManagerException)) {
			String className = handler.getClass().getSimpleName().toLowerCase();
			logger.error(ex.getMessage(), ex);
			
			try{
				messageSource.getMessage("error." + className, new String[] {}, Locale.getDefault());
				logManagerException = new LogManagerException("error." + className);
			} catch(Exception e){
				logManagerException = new LogManagerException("error.common");
			}
					
		}else{
			logManagerException = (LogManagerException) ex;
		}
		return super.doResolveException(request, response, handler, logManagerException);
	}
}
