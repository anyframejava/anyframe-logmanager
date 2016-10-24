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
package org.anyframe.logmanager.common.aspect;

import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.framework.Advised;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import org.anyframe.exception.BaseException;

import org.anyframe.logmanager.common.LogManagerException;

/**
 * This ExceptionTransfer class is an Aspect class to provide exception handling
 * on this project.
 * 
 * @author Youngmin Jo
 */
@Aspect
@Service
public class ExceptionTransfer {

	@Pointcut("execution(* org.anyframe.logmanager..*Impl.*(..))")
	public void serviceMethod() {
	}

	@Inject
	private MessageSource messageSource;

	@AfterThrowing(pointcut = "serviceMethod()", throwing = "exception")
	public void transfer(JoinPoint thisJoinPoint, Exception exception)
			throws LogManagerException {
		Object target = thisJoinPoint.getTarget();
		while (target instanceof Advised) {
			try {
				target = ((Advised) target).getTargetSource().getTarget();
			} catch (Exception e) {
				LogFactory.getLog(this.getClass()).error(
						"Fail to get target object from JointPoint.", e);
				break;
			}
		}

		String className = target.getClass().getSimpleName().toLowerCase();
		String opName = (thisJoinPoint.getSignature().getName()).toLowerCase();
		Log logger = LogFactory.getLog(target.getClass());

		if (exception instanceof LogManagerException) {
			LogManagerException logManagerEx = (LogManagerException) exception;
			logger.error(logManagerEx.getMessage(), logManagerEx);
			throw logManagerEx;
		}

		if (exception instanceof BaseException) {
			BaseException baseEx = (BaseException) exception;
			logger.error(baseEx.getMessage(), baseEx);
		}

		try{
			logger.error(messageSource.getMessage("error." + className	+ "." + opName, new String[] {}, Locale.getDefault()),
					exception);
		} catch(Exception e){
			logger.error(messageSource.getMessage("error.common", new String[] {}, Locale.getDefault()),
					exception);
			throw new LogManagerException("error.common");
		}
		throw new LogManagerException("error." + className	+ "." + opName);
	}
}
