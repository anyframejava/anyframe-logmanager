/* 
 * Copyright (C) 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.anyframe.logmanager.domain;

import java.util.List;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class Log4j {
	private List<Appender> appenders;
	private List<Logger> loggers;
	private Root root;
	/**
	 * @return the appenders
	 */
	public List<Appender> getAppenders() {
		return appenders;
	}
	/**
	 * @param appenders the appenders to set
	 */
	public void setAppenders(List<Appender> appenders) {
		this.appenders = appenders;
	}
	/**
	 * @return the loggers
	 */
	public List<Logger> getLoggers() {
		return loggers;
	}
	/**
	 * @param loggers the loggers to set
	 */
	public void setLoggers(List<Logger> loggers) {
		this.loggers = loggers;
	}
	/**
	 * @return the root
	 */
	public Root getRoot() {
		return root;
	}
	/**
	 * @param root the root to set
	 */
	public void setRoot(Root root) {
		this.root = root;
	}
	
	
}
