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
package org.anyframe.logmanager.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jaehyoung Eum
 *
 */
public class Root {
	
	private String level;
	private List<String> appenderRefs;
	
	
	/**
	 * @return
	 */
	public String getLevel() {
		return level;
	}
	
	/**
	 * @param level
	 */
	public void setLevel(String level) {
		this.level = level;
	}
	
	/**
	 * @return
	 */
	public List<String> getAppenderRefs() {
		return appenderRefs;
	}
	
	/**
	 * @param appenderRefs
	 */
	public void setAppenderRefs(List<String> appenderRefs) {
		this.appenderRefs = appenderRefs;
	}
	
	/**
	 * @param appenderRef
	 */
	public void setAppenderRef(String appenderRef) {
		if(this.appenderRefs == null) {
			this.appenderRefs = new ArrayList<String>();
		}
		this.appenderRefs.add(appenderRef);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[level=" + level + ", appenderRefs=" + appenderRefs + "]";
	}
}
