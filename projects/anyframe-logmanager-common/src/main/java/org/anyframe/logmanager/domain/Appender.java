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
 * @author Jaehyung Eum
 *
 */
public class Appender {
	
	private String name;
	private String appenderClass;
	private List<Param> params;
	private Layout layout;
	private List<String> appenderRefs;
	
	/**
	 * @return the appenderRefs
	 */
	public List<String> getAppenderRefs() {
		return appenderRefs;
	}

	/**
	 * @param appenderRefs the appenderRefs to set
	 */
	public void setAppenderRefs(List<String> appenderRefs) {
		this.appenderRefs = appenderRefs;
	}

	/**
	 * @param appenderRefs the appenderRefs to set
	 */
	public void setAppenderRef(String appenderRef) {
		if(this.appenderRefs == null) {
			this.appenderRefs = new ArrayList<String>();
		}
		this.appenderRefs.add(appenderRef);
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getAppenderClass() {
		return appenderClass;
	}

	/**
	 * @param appenderClass
	 */
	public void setAppenderClass(String appenderClass) {
		this.appenderClass = appenderClass;
	}

	/**
	 * @return
	 */
	public List<Param> getParams() {
		return params;
	}

	/**
	 * @param params
	 */
	public void setParams(List<Param> params) {
		this.params = params;
	}
	
	/**
	 * @param paramName
	 * @return
	 */
	public Param getParam(String paramName) {
		int paramSize = -1;
		if(params != null) {
			paramSize = this.params.size();
			for(int i=0;i<paramSize;i++) {
				if(paramName.equals(params.get(i).getName())){
					return params.get(i);
				}
			}
			return null;
		}else{
			return null;
		}
		
	}
	
	/**
	 * @param param
	 */
	public void setParam(Param param) {
		if(this.params == null) this.params = new ArrayList<Param>();
		this.params.add(param);
	}

	/**
	 * @return
	 */
	public Layout getLayout() {
		return layout;
	}

	/**
	 * @param layout
	 */
	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Appender [name=" + name + ", appenderClass=" + appenderClass
				+ ", params=" + params + ", layout=" + layout + "]";
	}
}
