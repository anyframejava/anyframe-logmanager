/* 
 * Copyright (C) 2010 Robert Stewart (robert@wombatnation.com)
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
package org.anyframe.logmanager.bundle.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.Layout;
import org.anyframe.logmanager.domain.Param;
import org.anyframe.logmanager.util.Log4jXmlBuilder;
import org.osgi.framework.BundleContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * 
 * @author jaehyoung.eum
 * 
 */
public class HarvestManager {

	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

	private static BundleContext context;
	
	private static Timer timer;

	private static String agentId;
	private static HashMap<String, TimerTask> timerTaskMap;

	private static DB db;

	private static DBCollection logApplication;
	private static DBCollection logAppender;
	private static DBCollection logAgent;
	private static DBCollection logevents;

	public static Timer getTimer() {
		return timer;
	}

	/**
	 * @return the agentId
	 */
	public static String getAgentId() {
		return agentId;
	}

	/**
	 * @return the timerTaskMap
	 */
	public static HashMap<String, TimerTask> getTimerTaskMap() {
		return timerTaskMap;
	}

	/**
	 * @return the logAppender
	 */
	public static DBCollection getLogAppender() {
		return logAppender;
	}

	/**
	 * @return the logevents
	 */
	public static DBCollection getLogevents() {
		return logevents;
	}

	public static void init(String agentId, BundleContext context, DB db) throws Exception {
		HarvestManager.agentId = agentId;
		HarvestManager.context = context;
		
		String logApplicationName = context.getProperty("mongo.app.collection");
		String logAppenderName = context.getProperty("mongo.appender.collection");
		String logAgentName = context.getProperty("mongo.agent.collection");

		timerTaskMap = new HashMap<String, TimerTask>();
		
		if (db != null) {
			logApplication = db.getCollection(logApplicationName);
			logAppender = db.getCollection(logAppenderName);
			logAgent = db.getCollection(logAgentName);
			HarvestManager.db = db;
		} else {
			throw new Exception("MongoDB connection is null.");
		}
	}
	
	/**
	 * @param db
	 * @param collectionName
	 * @throws Exception
	 */
	private static void updateAgentInfo(int status) throws Exception {
		
		BasicDBObject param = new BasicDBObject("agentId", agentId);
		DBObject r = logAgent.findOne(param);
		
		if(r != null) {
			r.put("status", status);
			r.put("lastUpdate", new Date());
			logAgent.update(param, r);
		}else{
			logAgent.insert(param.append("status", status).append("lastUpdate", new Date()));
		}
	}

	/**
	 * @throws Exception
	 */
	public static void startManager() throws Exception {
		BasicDBObject query = new BasicDBObject();
		query.put("agentId", agentId);
		query.put("status", LogManagerConstant.APP_STATUS_ACTIVE);
		DBCursor appCursor = logApplication.find(query);

		System.out.println("Application count is " + appCursor.count());
		if (appCursor.count() > 0) {

			if (timer != null)
				timer.cancel();
			timer = new Timer();

			Iterator<DBObject> i = appCursor.iterator();
			while (i.hasNext()) {
				DBObject appObject = (DBObject) i.next();
				String appName = appCursor.next().get("appName").toString();
				System.out.println("Application name is " + appName);

				DBCursor appenderCursor = logAppender.find(new BasicDBObject("appName", appName).append("agentId", agentId).append("status", LogManagerConstant.APPENDER_STATUS_ACTIVE).append("fileAppender", true));
				
				Log4jXmlBuilder log4jXml = new Log4jXmlBuilder(appObject.get("log4jXmlPath").toString());
				List<Appender> appenders = log4jXml.getLog4jAppender();
				
				while (appenderCursor.hasNext()) {
					setTimerTask(appenderCursor.next(), appenders, appName);
				}
				appenderCursor.close();
			}
		}
		appCursor.close();
		System.out.println("HarvestManager is started.");
		updateAgentInfo(LogManagerConstant.AGENT_STATUS_ACTIVE);
	}
	
	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private static long getPollingTime(String param) throws Exception {
		long pollingTime = -1;
		String unit = null;
		String temp = null;
		
		if (param == null) {
			temp = context.getProperty("default.polling.time");
		} else {
			temp = param;
		}

		unit = temp.substring(temp.length() - 1);

		temp = temp.substring(0, temp.length() - 1);
		System.out.println("Polling time is " + temp + unit);

		if ("s".equals(unit)) {
			pollingTime = Long.parseLong(temp) * 1000;
		} else if ("m".equals(unit)) {
			pollingTime = Long.parseLong(temp) * 1000 * 60;
		} else if ("h".equals(unit)) {
			pollingTime = Long.parseLong(temp) * 1000 * 60 * 60;
		} else if ("d".equals(unit)) {
			pollingTime = Long.parseLong(temp) * 1000 * 60 * 60 * 24;
		}
		
		return pollingTime;
	}

	/**
	 * 
	 */
	public static void stopManager() throws Exception {
		if (timer != null)
			timer.cancel();
		System.out.println("HarvestManager is stopped.");
		updateAgentInfo(LogManagerConstant.AGENT_STATUS_INACTIVE);
	}

	/**
	 * @param appender
	 * @param pollingTime
	 * @throws Exception
	 */
	private static void setTimerTask(DBObject appenderObject, List<Appender> appenders, String appName) throws Exception {

		String appenderName = appenderObject.get("appenderName").toString();
		String logPath = null;
		String jsonPattern = null;
		int timestampIndex = -1;
		long pollingTime = getPollingTime(appenderObject.get("pollingTime").toString());
		String conversionBsonPattern = null;
		String timestampDatePattern = null;
		long lastTimestamp = -1;

		int columnDelimiter = -1;
		int rowDelimiter = -1;

		int appenderLength = appenders.size();
		for (int i = 0; i < appenderLength; i++) {
			Appender appender = appenders.get(i);
			if (appenderName.equals(appender.getName())) {
				Layout layout = appender.getLayout();
				Param fileParam = appender.getParam("File");
				if (fileParam != null) {
					logPath = fileParam.getValue();
				} else {
					System.out.println("Invalid file appender : " + appenderName);
					return;
				}
				int layoutParamLength = layout.getParams().size();
				for (int j = 0; j < layoutParamLength; j++) {
					Param param = layout.getParams().get(j);
					if ("ColumnDelimiter".equals(param.getName())) {
						columnDelimiter = Integer.parseInt(param.getValue());
					} else if ("RowDelimiter".equals(param.getName())) {
						rowDelimiter = Integer.parseInt(param.getValue());
						// }else
						// if("ConversionPattern".equals(param.getName())){
					} else if ("JsonPattern".equals(param.getName())) {
						jsonPattern = param.getValue();
					} else if ("TimestampIndex".equals(param.getName())) {
						timestampIndex = Integer.parseInt(param.getValue());
					} else if ("TimestampDatePattern".equals(param.getName())) {
						timestampDatePattern = param.getValue();
					}
				}
			}
		}

		if (columnDelimiter == -1) {
			throw new Exception("column delimiter info does not exist.");
		}

		if (rowDelimiter == -1) {
			throw new Exception("row delimiter info does not exist.");
		}

		if (jsonPattern == null) {
			throw new Exception("conversion pattern info does not exist.");
		} else {
			conversionBsonPattern = "{\"" + jsonPattern.replaceAll(",", "\",\"").replaceAll(":", "\":\"") + "\"}";
		}

		if (timestampIndex == -1) {
			throw new Exception("timestamp info does not exist.");
		}

		if (timestampDatePattern == null) {
			timestampDatePattern = DEFAULT_DATE_PATTERN;
		}

		lastTimestamp = Long.parseLong(appenderObject.get("lastTimestamp").toString());

		System.out.println("lastTimestamp is " + lastTimestamp);
		System.out.println("conversionBsonPattern is " + conversionBsonPattern);
		System.out.println("timestampIndex is " + timestampIndex);
		System.out.println("timestampDatePattern is " + timestampDatePattern);
		System.out.println("appenderName is " + appenderName);

		logevents = db.getCollection(appenderName);
		Harvester h = new Harvester(agentId, logevents, logAppender, appName, appenderName, logPath, conversionBsonPattern, columnDelimiter, rowDelimiter, timestampIndex,
				timestampDatePattern, lastTimestamp);
		timer.schedule(h, new Date(), pollingTime);
		timerTaskMap.put(appName + ":" + appenderName, h);
	}
	
	/**
	 * @param appenderName
	 * @throws Exception
	 */
	public static void stopTask(String appenderName) throws Exception {

		TimerTask tt = timerTaskMap.get(appenderName);
		if (tt.cancel()) {
			timerTaskMap.remove(appenderName);
		}
	}

}