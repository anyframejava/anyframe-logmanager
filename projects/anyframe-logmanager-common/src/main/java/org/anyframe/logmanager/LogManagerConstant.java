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
package org.anyframe.logmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Log Manager Constant Class
 * 
 * @author Jaehyoung Eum
 *
 */
public class LogManagerConstant {
	public final static String[] LEVELS = {"FATAL", "ERROR", "WARN", "INFO", "DEBUG", "OFF"};
	public final static int LEVEL_DEBUG = 4;  
	public final static int LEVEL_INFO = 3;
	public final static int LEVEL_WARN = 2;
	public final static int LEVEL_ERROR = 1;
	public final static int LEVEL_FATAL = 0;
	public final static int LEVEL_OFF = 5;
	
	public final static String[] LEVELS_SEARCH = {"FATAL", "ERROR", "WARN", "INFO", "DEBUG"};
	
	public final static int LEVEL_THIS_ONLY = 0;
	public final static int LEVEL_INCLUDE_SUB = 1;
	public final static int LEVEL_INCLUDE_HIGH = 2;
	
	public final static String[] APP_STATUS = {"ACTIVE", "INACTIVE"};
	public final static int APP_STATUS_ACTIVE = 0;
	public final static int APP_STATUS_INACTIVE = 1;
	
	/**
	 * @deprecated fail status removed 
	 */
	public final static int APP_STATUS_FAILED = 2;
	
	public final static String[] AGENT_STATUS = {"INACTIVE", "ACTIVE"};
	public final static int AGENT_STATUS_INACTIVE = 0;
	public final static int AGENT_STATUS_ACTIVE = 1;
	
	public static final int APPENDER_STATUS_INACTIVE = 0;
	public static final int APPENDER_STATUS_ACTIVE = 1;
	
	public final static String APPENDER_CLASS_LOG4J_PATTERNLAYOUT = "org.anyframe.logmanager.log4mongo.MongoDbPatternLayoutAppender";
	public final static String APPENDER_CLASS_LOG4J = "org.anyframe.logmanager.log4j.MongoDbAppender";
	public final static String APPENDER_CLASS_LOGBACK = "org.anyframe.logmanager.logback.MongoDbAppender";
	public final static String MONITOR_LEVEL = "monitorLevel";
	public final static int MONITOR_LEVEL_ADMIN_ONLY = 0;
	public final static int MONITOR_LEVEL_DEV_VISIBLE = 1;
	
	public final static String[] SEARCH_USER_TYPES = {"All", "Administrator", "Developer"};
	public final static String[] DETAIL_USER_TYPES = {"Administrator", "Developer"};
	
	public final static int MAX_ROW_LIMIT = 5000;
	
	public final static int POLLING_DURATION_SECOND = 5;
	
	public static final String COLLECTION_BASED = "<<Collection Based>>";
	
	public final static String[] LOGGING_FRAMEWORKS = {"Log4j", "Logback"};
	
	public static final List<String> HOURS = new ArrayList<String>(){
		private static final long serialVersionUID = 974522437319548558L;
		{
			add("01");
			add("02");
			add("03");
			add("04");
			add("05");
			add("06");
			add("07");
			add("08");
			add("09");
			add("10");
			add("11");
			add("12");
			add("13");
			add("14");
			add("15");
			add("16");
			add("17");
			add("18");
			add("19");
			add("20");
			add("21");
			add("22");
			add("23");
			add("24");
		}
	};
	
	public static final List<String> MINUTES = new ArrayList<String>(){
		private static final long serialVersionUID = -5778904955764923771L;

		{
			add("00");
			add("10");
			add("20");
			add("30");
			add("40");
			add("50");
		}
	};
	
}
