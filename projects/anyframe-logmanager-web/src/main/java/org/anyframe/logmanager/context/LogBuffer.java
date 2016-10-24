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
package org.anyframe.logmanager.context;

import java.util.ArrayList;
import java.util.List;


/**
 * This is LogBuffer class.
 * 
 * @author Jaehyoung Eum
 */
public class LogBuffer {

	private List<String> buffer;
	
	public LogBuffer() {
		super();
		buffer = new ArrayList<String>();
	}

	/**
	 * @return the buffer
	 */
	public List<String> getBuffer() {
		return buffer;
	}

	/**
	 * @param buffer the buffer to set
	 */
	public void setBuffer(List<String> buffer) {
		this.buffer = buffer;
	}
	
	/**
	 * @param message
	 */
	public void add(String message) {
		this.buffer.add(message);
	}
	
	/**
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public LogBuffer slice(int start, int end) throws Exception {
		LogBuffer newBuffer = new LogBuffer();
		newBuffer.setBuffer(this.buffer.subList(start, end));
		return newBuffer;
	}
	
	/**
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public LogBuffer splice(int start, int end) throws Exception {
		LogBuffer r = new LogBuffer();
		int limit = end - start;
		for(int i=0;i<limit;i++) {
			r.add(this.buffer.get(start));
			this.buffer.remove(start);	
		}
		return r;
	}
	
	/**
	 * @throws Exception
	 */
	public void removeAll() throws Exception {
		this.buffer = new ArrayList<String>();
	}
	
	/**
	 * @return
	 */
	public String serialize() {
		StringBuffer sb = new StringBuffer();
		int size = 0;
		
		if(this.buffer != null) {
			size = this.buffer.size();
			for(int i=0;i<size;i++) {
				sb.append(this.buffer.get(i));
				if(i != size - 1) {
					sb.append("\n");
				}
			}
		}else{
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.buffer.isEmpty();
	}
	
	/**
	 * @return
	 */
	public int size() {
		return this.buffer.size();
	}
	
}
