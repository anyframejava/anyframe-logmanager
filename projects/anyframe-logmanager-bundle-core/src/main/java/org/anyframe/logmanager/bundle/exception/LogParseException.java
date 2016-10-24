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
package org.anyframe.logmanager.bundle.exception;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1804769009622730913L;
	
	/**
	 * constructor
	 * 
	 * @param message
	 */
	public LogParseException(String message) {
		super(message);
	}

}
