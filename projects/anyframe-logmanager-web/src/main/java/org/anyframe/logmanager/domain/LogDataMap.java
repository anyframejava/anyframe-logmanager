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

import java.util.HashMap;

import org.bson.types.ObjectId;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogDataMap extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -678242925975193742L;

	/* (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public Object get(Object key) {
		Object value = super.get(key);
		if(value instanceof ObjectId || "_id".equals(key)) {
			return ((ObjectId)value).toString();
		}else{
			return value;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(String key, Object value) {
		if(value instanceof ObjectId || "_id".equals(key)) {
			return super.put(key, ((ObjectId)value).toString());
		}else{
			return super.put(key, value);
		}
	}
	
}
