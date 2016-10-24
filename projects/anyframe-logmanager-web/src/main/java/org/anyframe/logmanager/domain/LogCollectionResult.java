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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogCollectionResult {

	private String id;
	private String collectionId;
	private int iterationOrder;
	private int status;
	private Map<String, List<String>> message;
	private long parseCount;
	private long insertCount;
	private Date timestamp;
	
	/**
	 * 
	 */
	public LogCollectionResult() {
		super();
	}
	
	/**
	 * @param collectionId
	 * @param status
	 */
	public LogCollectionResult(String collectionId, int status) {
		super();
		this.collectionId = collectionId;
		this.status = status;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the collectionId
	 */
	public String getCollectionId() {
		return collectionId;
	}
	/**
	 * @param collectionId the collectionId to set
	 */
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	/**
	 * @return the iterationOrder
	 */
	public int getIterationOrder() {
		return iterationOrder;
	}
	/**
	 * @param iterationOrder the iterationOrder to set
	 */
	public void setIterationOrder(int iterationOrder) {
		this.iterationOrder = iterationOrder;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the message
	 */
	public Map<String, List<String>> getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(Map<String, List<String>> message) {
		this.message = message;
	}
	/**
	 * @return the parseCount
	 */
	public long getParseCount() {
		return parseCount;
	}
	/**
	 * @param parseCount the parseCount to set
	 */
	public void setParseCount(long parseCount) {
		this.parseCount = parseCount;
	}
	/**
	 * @return the insertCount
	 */
	public long getInsertCount() {
		return insertCount;
	}
	/**
	 * @param insertCount the insertCount to set
	 */
	public void setInsertCount(long insertCount) {
		this.insertCount = insertCount;
	}
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return "LogCollection [id=" + id + ", collectionId=" + collectionId + ", iterationOrder=" + iterationOrder 
				+ ", status=" + status + ", parseCount=" + parseCount + ", insertCount="
				+ insertCount + ", timestamp=" + timestamp + ", message=" + message + "]";
	}
	
}
