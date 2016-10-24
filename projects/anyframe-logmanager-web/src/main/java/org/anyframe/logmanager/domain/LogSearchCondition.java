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

import java.util.Date;

/**
 * @author Jaehyoung Eum
 */
public class LogSearchCondition extends AnalysisLog {
	private int pageIndex = -1;
	private int pageSize = -1;
	private long totalCount = -1;
	
	private Date fromDateTime;
	private String fromDate;
	private String fromHour;
	private String fromMinute;
	private Date toDateTime;
	private String toDate;
	private String toHour;
	private String toMinute;
	private int logLevelDirection = 0;
	
	private String clientIp;
	private String userId;
	
	private String messageText;
	private boolean collectionBased = false;
	private boolean advancedOptions = false;
	private boolean matchedLogOnly = false;
	private boolean useFromDate = false;
	private boolean useToDate = false;
	private boolean logTailingMode = false;
	private String collection;
	private String durationTemplate;
	
	private String previousTimestamp;
	private String appenderName;
	
	/**
	 * @return the clientIp
	 */
	public String getClientIp() {
		return clientIp;
	}

	/**
	 * @param clientIp the clientIp to set
	 */
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the collectionBased
	 */
	public boolean isCollectionBased() {
		return collectionBased;
	}

	/**
	 * @param collectionBased the collectionBased to set
	 */
	public void setCollectionBased(boolean collectionBased) {
		this.collectionBased = collectionBased;
	}

	/**
	 * @return the appenderName
	 */
	public String getAppenderName() {
		return appenderName;
	}

	/**
	 * @param appenderName the appenderName to set
	 */
	public void setAppenderName(String appenderName) {
		this.appenderName = appenderName;
	}

	/**
	 * @return the previousTimestamp
	 */
	public String getPreviousTimestamp() {
		return previousTimestamp;
	}
	
	/**
	 * @param previousTimestamp the previousTimestamp to set
	 */
	public void setPreviousTimestamp(String previousTimestamp) {
		this.previousTimestamp = previousTimestamp;
	}
	
	/**
	 * @return the durationTemplate
	 */
	public String getDurationTemplate() {
		return durationTemplate;
	}
	
	/**
	 * @param durationTemplate the durationTemplate to set
	 */
	public void setDurationTemplate(String durationTemplate) {
		this.durationTemplate = durationTemplate;
	}
	
	/**
	 * @return the totalCount
	 */
	public long getTotalCount() {
		return totalCount;
	}
	
	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	
	/**
	 * @return the collection
	 */
	public String getCollection() {
		return collection;
	}
	
	/**
	 * @param collection the collection to set
	 */
	public void setCollection(String collection) {
		this.collection = collection;
	}
	
	/**
	 * @return the logTailingMode
	 */
	public boolean isLogTailingMode() {
		return logTailingMode;
	}
	/**
	 * @param logTailingMode the logTailingMode to set
	 */
	public void setLogTailingMode(boolean logTailingMode) {
		this.logTailingMode = logTailingMode;
	}
	
	/**
	 * @return the useFromDate
	 */
	public boolean isUseFromDate() {
		return useFromDate;
	}
	
	/**
	 * @param useFromDate the useFromDate to set
	 */
	public void setUseFromDate(boolean useFromDate) {
		this.useFromDate = useFromDate;
	}
	
	/**
	 * @return the useToDate
	 */
	public boolean isUseToDate() {
		return useToDate;
	}
	
	/**
	 * @param useToDate the useToDate to set
	 */
	public void setUseToDate(boolean useToDate) {
		this.useToDate = useToDate;
	}
	
	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}
	
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * @return the fromHour
	 */
	public String getFromHour() {
		return fromHour;
	}
	
	/**
	 * @param fromHour the fromHour to set
	 */
	public void setFromHour(String fromHour) {
		this.fromHour = fromHour;
	}
	
	/**
	 * @return the fromMinute
	 */
	public String getFromMinute() {
		return fromMinute;
	}
	
	/**
	 * @param fromMinute the fromMinute to set
	 */
	public void setFromMinute(String fromMinute) {
		this.fromMinute = fromMinute;
	}
	
	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}
	
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	/**
	 * @return the toHour
	 */
	public String getToHour() {
		return toHour;
	}
	
	/**
	 * @param toHour the toHour to set
	 */
	public void setToHour(String toHour) {
		this.toHour = toHour;
	}
	
	/**
	 * @return the toMinute
	 */
	public String getToMinute() {
		return toMinute;
	}
	
	/**
	 * @param toMinute the toMinute to set
	 */
	public void setToMinute(String toMinute) {
		this.toMinute = toMinute;
	}
	
	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	/**
	 * @return the fromDate
	 */
	public Date getFromDateTime() {
		return fromDateTime;
	}
	
	/**
	 * @param fromDateTime the fromDateTime to set
	 */
	public void setFromDateTime(Date fromDateTime) {
		this.fromDateTime = fromDateTime;
	}
	
	/**
	 * @return the toDateTime
	 */
	public Date getToDateTime() {
		return toDateTime;
	}
	
	/**
	 * @param toDateTime the toDateTime to set
	 */
	public void setToDateTime(Date toDateTime) {
		this.toDateTime = toDateTime;
	}
	
	/**
	 * @return the logLevelDirection
	 */
	public int getLogLevelDirection() {
		return logLevelDirection;
	}
	
	/**
	 * @param logLevelDirection the logLevelDirection to set
	 */
	public void setLogLevelDirection(int logLevelDirection) {
		this.logLevelDirection = logLevelDirection;
	}
	
	/**
	 * @return the messageText
	 */
	public String getMessageText() {
		return messageText;
	}
	
	/**
	 * @param messageText the messageText to set
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	/**
	 * @return the advancedOptions
	 */
	public boolean isAdvancedOptions() {
		return advancedOptions;
	}
	
	/**
	 * @param advancedOptions the advancedOptions to set
	 */
	public void setAdvancedOptions(boolean advancedOptions) {
		this.advancedOptions = advancedOptions;
	}
	
	/**
	 * @return the matchedLogOnly
	 */
	public boolean isMatchedLogOnly() {
		return matchedLogOnly;
	}
	
	/**
	 * @param matchedLogOnly the matchedLogOnly to set
	 */
	public void setMatchedLogOnly(boolean matchedLogOnly) {
		this.matchedLogOnly = matchedLogOnly;
	}
	
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.domain.AnalysisLog#toString()
	 */
	public String toString() {
		return super.toString() + "\nLogSearchCondition [pageIndex=" + pageIndex + ", pageSize=" + pageSize + ", fromDateTime=" + fromDateTime + ", toDateTime=" + toDateTime + ", logLevelDirection=" + logLevelDirection
		+ ", advancedOptions=" + advancedOptions + ", matchedLogOnly=" + matchedLogOnly + ",messageText=" + messageText
		+ ", fromDate=" + fromDate + ", fromHour=" + fromHour + ", fromMinute=" + fromMinute
		+ ", toDate=" + toDate + ", toHour=" + toHour + ", toMinute=" + toMinute + ", previousTimestamp=" + previousTimestamp + ", logTailingMode=" + logTailingMode + "]";
	}
	
}
