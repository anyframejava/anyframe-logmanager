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
package org.anyframe.logmanager.bundle.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.bundle.exception.LogManagerBundleException;
import org.anyframe.logmanager.bundle.util.LogDataFormatUtil;
import org.bson.types.ObjectId;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This is HarvestManager class.
 * 
 * @author Jaehyoung Eum
 */
public class LogCollectionManager {

	private static Logger logger = LoggerFactory.getLogger(LogCollectionManager.class);
	
	private static BundleContext context;

	private static Timer timer;

	private static String agentId;
	private static Map<String, TimerTask> timerTaskMap;

	private static DB db;

	private static DBCollection logApplication;
	private static DBCollection logAgent;
	private static DBCollection targetCollection;
	private static DBCollection logCollection;
	private static DBCollection logCollectionResult;
	private static String defaultCollectionName;

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
	public static Map<String, TimerTask> getTimerTaskMap() {
		return timerTaskMap;
	}


	public static void init(String agentId, BundleContext context, DB db) throws Exception {
		LogCollectionManager.agentId = agentId;
		LogCollectionManager.context = context;

		String logApplicationName = context.getProperty("mongo.app.collection");
		String logCollectionName = context.getProperty("mongo.logcollection.collection");
		String logCollectionResultName = context.getProperty("mongo.logcollectionresult.collection");
		String logAgentName = context.getProperty("mongo.agent.collection");
		defaultCollectionName = context.getProperty("mongo.default.collection");
		
		timerTaskMap = new HashMap<String, TimerTask>();

		if (db != null) {
			logApplication = db.getCollection(logApplicationName);
			logCollection = db.getCollection(logCollectionName);
			logCollectionResult = db.getCollection(logCollectionResultName);
			logAgent = db.getCollection(logAgentName);
			LogCollectionManager.db = db;
		} else {
			throw new LogManagerBundleException("MongoDB connection is null.");
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

		if (r != null) {
			r.put("status", status);
			r.put("lastUpdate", new Date());
			logAgent.update(param, r);
		} else {
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

		logger.info("Application count is {}", appCursor.count());
		if (appCursor.count() > 0) {

			if (timer != null)
				timer.cancel();
			timer = new Timer();

			Iterator<DBObject> i = appCursor.iterator();
			while (i.hasNext()) {
				String appName = i.next().get("appName").toString();
				logger.info("Application name is {}", appName);

				DBCursor logCollectionCursor = logCollection.find(new BasicDBObject("appName", appName)
					.append("agentId", agentId)
					.append("setLogCollectionActive", true));

				while (logCollectionCursor.hasNext()) {
					setTimerTask(logCollectionCursor.next(), appName);
				}
				logCollectionCursor.close();
			}
		}
		appCursor.close();
		logger.info("HarvestManager is started.");
		updateAgentInfo(LogManagerConstant.AGENT_STATUS_ACTIVE);
	}

	/**
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private static long getPollingTime(String term, String unit) throws Exception {
		long pollingTime = -1;
		String temp = null;

		if (term == null) {
			temp = context.getProperty("default.polling.time");
		} else {
			temp = term;
		}

//		logger.info("Polling time is " + temp + unit);

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
		if (timer != null) {
			timer.cancel();
		}
		logger.info("HarvestManager is stopped.");
		updateAgentInfo(LogManagerConstant.AGENT_STATUS_INACTIVE);
	}

	/**
	 * @param appender
	 * @param pollingTime
	 * @throws Exception
	 */
	private static void setTimerTask(DBObject logCollectionObject, String appName) throws Exception {

		String id = null;
		String pathOfLog = null;
		String logFileName = null;
		String regularExp = null;
		String targetCollectionName = null;
		String collectionTerm = null;
		String collectionTermUnit = null;
		boolean isRegExp = false; 
		
		long pollingTime = getPollingTime(logCollectionObject.get("collectionTerm").toString(), logCollectionObject.get("collectionTermUnit").toString());
		long lastTimestamp = -1;
		
		id = logCollectionObject.get("_id").toString();
		pathOfLog = logCollectionObject.get("pathOfLog").toString();
		logFileName = logCollectionObject.get("logFileName").toString();
		regularExp = logCollectionObject.get("regularExp").toString();
		if(!isRegExp) {
			Map<String, Object> convertMap = LogDataFormatUtil.convertLogDataFormat(regularExp);
			regularExp = (String)convertMap.get("regexp");	
		}
		isRegExp = ((Boolean)logCollectionObject.get("isRegExp")).booleanValue();
		BasicDBList columnsInfo = (BasicDBList)logCollectionObject.get("columnsInfo");
		collectionTerm = logCollectionObject.get("collectionTerm").toString();
		collectionTermUnit = logCollectionObject.get("collectionTermUnit").toString();
		lastTimestamp = (Long)logCollectionObject.get("lastUpdate");
		targetCollectionName = (String)logCollectionObject.get("repositoryName");
		if(targetCollectionName == null) {
			targetCollectionName = defaultCollectionName;
		}
		logger.info("-------------------------------------------------------");
		logger.info("task id is {}", id);
		logger.info("pathOfLog is {}", pathOfLog);
		logger.info("logFileName is {}", logFileName);
		logger.info("regularExp is {}", regularExp);
		logger.info("isRegExp is {}", isRegExp);
		logger.info("columnsInfo is {}", columnsInfo.toString());
		logger.info("pollint time is {} {}", collectionTerm, collectionTermUnit);
		logger.info("lastTimestamp is {}", lastTimestamp);
		logger.info("targetCollectionName is {}", targetCollectionName);
		logger.info("-------------------------------------------------------");
		
		logCollectionResult.remove(new BasicDBObject("collectionId", new ObjectId(id)));
		logger.info("previous log collection result data was initialized.");
		
		targetCollection = db.getCollection(targetCollectionName);
		LogCollector h = new LogCollector(id, appName, targetCollection, logCollection, logCollectionResult, pathOfLog, logFileName, regularExp, columnsInfo, lastTimestamp);
		timer.schedule(h, new Date(), pollingTime);
		timerTaskMap.put(id, h);
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