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
package org.anyframe.logmanager.bundle.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Log File Name Filter Class
 *
 * @author jaehyoung.eum
 *
 */
public class LogFileNameFilter implements FilenameFilter {

	private final Pattern pattern;
	
	/**
	 * @param pattern
	 */
	public LogFileNameFilter(String regex) {
		super();
		this.pattern = Pattern.compile(regex);
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		Matcher matcher = pattern.matcher(name);
		if(matcher.matches()) {
			return true;
		}else{
			return false;	
		}
	}

}
