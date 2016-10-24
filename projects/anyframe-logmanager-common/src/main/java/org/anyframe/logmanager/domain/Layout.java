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
public class Layout {
	private String layoutClass;
	private List<Param> params;
	
	/**
	 * 
	 * @return
	 */
	public String getLayoutClass() {
		return layoutClass;
	}
	
	/**
	 * @param layoutClass
	 */
	public void setLayoutClass(String layoutClass) {
		this.layoutClass = layoutClass;
	}
	
	/**
	 * @return
	 */
	public List<Param> getParams() {
		return params;
	}
	
	/**
	 * @param param
	 */
	public void setParams(List<Param> params) {
		this.params = params;
	}

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
	
	public void setParam(Param param) {
		if(this.params == null) this.params = new ArrayList<Param>();
		this.params.add(param);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[layoutClass=" + layoutClass + ", params=" + params + "]";
	}
}
