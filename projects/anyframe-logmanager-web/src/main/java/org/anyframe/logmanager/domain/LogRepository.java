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

import org.springframework.data.annotation.Id;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogRepository {
	@Id
	private String id;
	private String repositoryName;
	private int monitorLevel;
	private boolean active;
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
	 * @return the repositoryName
	 */
	public String getRepositoryName() {
		return repositoryName;
	}
	/**
	 * @param repositoryName the repositoryName to set
	 */
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	/**
	 * @return the monitorLevel
	 */
	public int getMonitorLevel() {
		return monitorLevel;
	}
	/**
	 * @param monitorLevel the monitorLevel to set
	 */
	public void setMonitorLevel(int monitorLevel) {
		this.monitorLevel = monitorLevel;
	}
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "LogRepository [id=" + id + ", repositoryName=" + repositoryName + ", monitorLevel=" + monitorLevel + ", active=" + active + "]";
	}
}
