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

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.anyframe.logmanager.bundle.exception.LogManagerBundleException;
import org.anyframe.logmanager.bundle.exception.LogParseException;
import org.anyframe.logmanager.bundle.util.LogBuffer;
import org.anyframe.logmanager.bundle.util.LogFileNameFilter;
import org.anyframe.logmanager.bundle.util.ModifiedDateComparator;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * This is Harvester class.
 * 
 * @author Jaehyoung Eum
 */
public class LogCollector extends TimerTask {

	private static final String KEY_APP_NAME = "appName";
	private static final String COLUMN_INFO_NAME = "columnName";
	private static final String COLUMN_INFO_TYPE = "columnType";
	private static final String COLUMN_INFO_DATEFORMAT = "dateFormat";
	private static final String COLUMN_TYPE_TIMESTAMP = "timestamp";
	private static final String COLUMN_TYPE_DATE = "date"; 
	private static final String COLUMN_TYPE_NUMBER = "number";
	private static final String COLUMN_TYPE_IGNORE = "ignore";
	private static final int COLLECTION_STATUS_SUCCESSFUL = 0;
	private static final int COLLECTION_STATUS_WARNING = 1;
	private static final int COLLECTION_STATUS_ERROR = 2;
	
	private static Logger logger = LoggerFactory.getLogger(LogCollector.class);
	private int iterationOrder = 0;
	private final String id;
	private final String appName;
	private final DBCollection targetCollection;
	private final DBCollection logCollection;
	private final DBCollection logCollectionResult;
	private final String pathOfLog;
	private final String logFileName;
	private final String regularExp;
	private final BasicDBList columnsInfo;
	
	private long lastTimestamp;
	
	/**
	 * Constructor using field
	 * 
	 * @param id
	 * @param appName
	 * @param targetCollection
	 * @param pathOfLog
	 * @param logFileName
	 * @param regularExp
	 * @param columnsInfo
	 * @param lastTimestamp
	 */
	public LogCollector(String id, String appName, 
			DBCollection targetCollection, DBCollection logCollection, DBCollection logCollectionResult, 
			String pathOfLog, String logFileName, String regularExp, BasicDBList columnsInfo, long lastTimestamp) {
		super();
		this.id = id;
		this.appName = appName;
		this.targetCollection = targetCollection;
		this.logCollection = logCollection;
		this.logCollectionResult= logCollectionResult;
		this.pathOfLog = pathOfLog;
		this.logFileName = logFileName;
		this.regularExp = regularExp;
		this.columnsInfo = columnsInfo;
		this.lastTimestamp = lastTimestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() throws MongoException {
		// TODO Auto-generated method stub
		int insertCount = 0;
		int parseCount = 0;
		Scanner scanner = null;
		DBObject tempItem = null;
		long currentTimestamp = -1;
		Date timestamp = null;
		Matcher currentLineMatcher = null;
	    Matcher currentBufferMatcher = null;
	    String lineBuffer = "";
		LogBuffer buffer = new LogBuffer();
	    List<String> warningEvent = new ArrayList<String>();
	    List<String> errorEvent = new ArrayList<String>();
	    Pattern p = null;
	    int k = 0;
	    int lfc = 0;
		try {
			logger.info("[{}/{}] ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", appName, id);
			logger.info("[{}/{}] task started.", appName, id);
			iterationOrder++;
			File[] logList = null;
			File path = new File(pathOfLog);
			if(path.exists()) {
				if(path.isDirectory()) {
					logger.debug("[{}/{}] {} is directory.", new Object[]{appName, id, pathOfLog});
					FilenameFilter filter = new LogFileNameFilter(logFileName);
					logList = path.listFiles(filter);
					
					if (logList.length == 0) {
						// result logging
						String errorString = "[" + appName + "/" + id + "] " + path + "/" + logFileName + " does not exist.";
						errorEvent.add(errorString);
						throw new LogManagerBundleException(errorString);
					}
					
					Arrays.sort(logList, new ModifiedDateComparator());
				}else{
					logger.debug("[{}/{}] {} is file.", new Object[]{appName, id, pathOfLog});
					logList = new File[]{path};
				}	
			}else{
				// result logging
				String errorString = "[" + appName + "/" + id + "] " + path + " does not exist.";
				errorEvent.add(errorString);
				throw new LogManagerBundleException(errorString);
			}
			
			for(int i=0;i<logList.length;i++) {
				logger.debug("[{}/{}] {} < {}", new Object[]{appName, id, lastTimestamp, logList[i].lastModified()});
				if(lastTimestamp < logList[i].lastModified()) {
					currentTimestamp = lastTimestamp + 1;
					Date lastUpdateDate = new Date(logList[i].lastModified());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
					logger.info("[{}/{}] {} {} ", new Object[]{appName, id, logList[i].getName(), sdf.format(lastUpdateDate)});
					// log data parsing
					parseCount = 0;
					insertCount = 0;
					lfc = lineFeedCount(regularExp);
					p = Pattern.compile(regularExp, Pattern.DOTALL);
					logger.debug("[{}/{}](:{}) lfc={}", new Object[]{appName, id, logList[i].getName(), lfc});
					File f = logList[i];

					scanner = new Scanner(f);
					scanner.useDelimiter("\n");
					k = 0;
					while(scanner.hasNext()) {
						
						logger.debug("[{}/{}](:{}) ===={}====", new Object[]{appName, id, logList[i].getName(), k});
						
						lineBuffer = scanner.next().replaceAll("\\r", "");
						
						if(lineBuffer == null || "".equals(lineBuffer.trim())) {
							continue;
						}
						
						buffer.add(lineBuffer);
						
						if(buffer.size() <= lfc) {
							k++;
							continue;
						}
						
						logger.debug("[{}/{}](:{})lineMatcherString={}", new Object[]{appName, id, logList[i].getName(), buffer.slice(buffer.size() - lfc - 1, buffer.size()).serialize()});
						logger.debug("[{}/{}](:{})buffer={}", new Object[]{appName, id, logList[i].getName(), buffer.serialize()});
						
						currentLineMatcher = p.matcher(buffer.slice(buffer.size() - lfc - 1, buffer.size()).serialize());
						currentBufferMatcher = p.matcher(buffer.serialize());
						
						// currentMatch
						if(currentLineMatcher.matches()) {
							
							if(buffer.size() > lfc + 1) {
								logger.debug("[{}/{}](:{}) == case0 ==", new Object[]{appName, id, logList[i].getName()});
								LogBuffer writeBuffer = buffer.splice(0, buffer.size() - lfc - 1);
								logger.debug("[{}/{}](:{})writeBuffer=", new Object[]{appName, id, logList[i].getName(), writeBuffer.serialize()});
								try{
									tempItem = parseColumn(writeBuffer.serialize());
									parseCount++;
									
								}catch(LogParseException e){
									logger.warn("[{}/{}](:{}) {}", new Object[]{appName, id, logList[i].getName(), e.getMessage()});
									warningEvent.add("(" + logList[i].getName() + ") LogParseExcetion was occurred\n " + e.getMessage() + " ==>\n" + writeBuffer.serialize());
								}
								
								if(tempItem == null) {
									timestamp = null;
								}else{
									timestamp = (Date)tempItem.get("timestamp");	
								}
								
								if(timestamp != null) {
									if(currentTimestamp <= timestamp.getTime()) {
										targetCollection.insert(tempItem);
										insertCount++;
										currentTimestamp = timestamp.getTime();	
									}
								}else{
									logger.warn("[{}/{}](:{}) can not find the timestamp ==>\n{}", new Object[]{appName, id, logList[i].getName(), writeBuffer.serialize()});
									warningEvent.add("(" + logList[i].getName() + ") can not find the timestamp ==>\n" + buffer.serialize());
									continue;
								}
							}else{
								logger.debug("[{}/{}](:{}) == case1 ==", new Object[]{appName, id, logList[i].getName()});
							}
						} else {
							if(currentBufferMatcher.matches()) {
								logger.debug("[{}/{}](:{}) == case2 ==", new Object[]{appName, id, logList[i].getName()});
							}else{
								logger.debug("[{}/{}](:{}) == case3 ==", new Object[]{appName, id, logList[i].getName()});
								logger.warn("[{}/{}](:{}) does not match this data ==> {}", new Object[]{appName, id, logList[i].getName()}, buffer.serialize());
								warningEvent.add("(" + logList[i].getName() + ") does not match this data ==>\n" + buffer.serialize());
								buffer.removeAll();
							}
						}
						
						k++;
						logger.debug("[{}/{}](:{}) -----------------------------------" ,new Object[]{appName, id, logList[i].getName()});
					}
					
					try{
						tempItem = parseColumn(buffer.serialize());
						parseCount++;	
					}catch(LogParseException e){
						logger.warn("[{}/{}](:{}) {}", new Object[]{appName, id, logList[i].getName(), e.getMessage()});
						warningEvent.add("(" + logList[i].getName() + ") LogParseExcetion was occurred\n " + e.getMessage() + " ==>\n" + buffer.serialize());
					}
					
					if(tempItem == null) {
						timestamp = null;
					}else{
						timestamp = (Date)tempItem.get("timestamp");	
					}
					
					if(timestamp != null) {
						if(currentTimestamp <= timestamp.getTime()) {
							targetCollection.insert(tempItem);
							insertCount++;
							currentTimestamp = timestamp.getTime();
						}
					}else{
						logger.warn("[{}/{}](:{}) can not find the timestamp.", new Object[]{appName, id, logList[i].getName()});
						warningEvent.add("(" + logList[i].getName() + ") can not find the timestamp ==>\n" + buffer.serialize());
					}
					
					logger.info("[{}/{}](:{}) parseCount={}", new Object[]{appName, id, logList[i].getName(), parseCount});
					
					scanner.close();
					
					logger.info("[{}/{}](:{}) ==========================================================", new Object[]{appName, id, logList[i].getName()});
					logger.info("[{}/{}](:{}) Insert count is {}", new Object[]{appName, id, logList[i].getName(), insertCount});
					logger.info("[{}/{}](:{}) Last Timestamp is {}", new Object[]{appName, id, logList[i].getName(), currentTimestamp});
					logger.info("[{}/{}](:{}) ==========================================================", new Object[]{appName, id, logList[i].getName()});
				}
			}

			if(currentTimestamp != -1 && insertCount > 0) {
				lastTimestamp = currentTimestamp;
				
				BasicDBObject where = new BasicDBObject("_id", new ObjectId(id));
				BasicDBObject values = new BasicDBObject("lastUpdate", lastTimestamp);
				values.put("currentStatus", 1);
				BasicDBObject set = new BasicDBObject("$set", values);

				logCollection.update(where, set);
				logger.info("[{}/{}] timestamp was updated to {}", new Object[]{appName, id, lastTimestamp});
			}
			
			logger.info("[{}/{}] task end.", appName, id);
			logger.info("[{}/{}] ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++", appName, id);
			
			// result logging
			DBObject query = new BasicDBObject("collectionId", new ObjectId(id));
			DBObject message = new BasicDBObject("warning", warningEvent);
			message.put("error", errorEvent);

			int status = COLLECTION_STATUS_SUCCESSFUL;
			if(!warningEvent.isEmpty()) {
				status = COLLECTION_STATUS_WARNING;
			}
			
			query.put("iterationOrder", iterationOrder);
			query.put("status",	status);
			query.put("message", message);
			query.put("parseCount", parseCount);
			query.put("insertCount", insertCount);
			query.put("timestamp", new Date());
			logCollectionResult.insert(query);
			
		} catch (MongoException e){
			
			StackTraceElement[] st = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append(e.getMessage()).append("\n");
	        for (StackTraceElement element : st) {
	            sb.append("\t").append("at ").append(element.toString()).append("\n");
	        }
	        errorEvent.add(sb.toString());
			
			// result logging
			DBObject query = new BasicDBObject("collectionId", new ObjectId(id));
			DBObject message = new BasicDBObject("warning", warningEvent);
			message.put("error", errorEvent);
			int status = COLLECTION_STATUS_ERROR;
			
			query.put("iterationOrder", iterationOrder);
			query.put("status",	status);
			query.put("message", message);
			query.put("parseCount", parseCount);
			query.put("insertCount", insertCount);
			query.put("timestamp", new Date());
			logCollectionResult.insert(query);
			
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			// status update to fail
			lastTimestamp = currentTimestamp;
			BasicDBObject where = new BasicDBObject("_id", new ObjectId(id));
			BasicDBObject values = new BasicDBObject("lastUpdate", lastTimestamp);
			values.put("currentStatus", 1);
			BasicDBObject set = new BasicDBObject("$set", values);

			logCollection.update(where, set);
			logger.info("[{}/{}]timestamp was updated to {}", new Object[]{appName, id, lastTimestamp});
			
			StackTraceElement[] st = e.getStackTrace();
			StringBuffer sb = new StringBuffer();
			sb.append(e.getMessage()).append("\n");
	        for (StackTraceElement element : st) {
	            sb.append("\t").append("at ").append(element.toString()).append("\n");
	        }
	        errorEvent.add(sb.toString());
	        
			// result logging
			DBObject query = new BasicDBObject("collectionId", new ObjectId(id));
			DBObject message = new BasicDBObject("warning", warningEvent);
			message.put("error", errorEvent);
			int status = COLLECTION_STATUS_ERROR;
			
			query.put("iterationOrder", iterationOrder);
			query.put("status",	status);
			query.put("message", message);
			query.put("parseCount", parseCount);
			query.put("insertCount", insertCount);
			query.put("timestamp", new Date());
			logCollectionResult.insert(query);
			
			logger.error(e.getMessage(), e);
		} finally {
			if(scanner != null) scanner.close();
		}
	}
	

	/**
	 * @param sb
	 * @return
	 * @throws Exception
	 */
	private DBObject parseColumn(String log) throws Exception {
		Pattern p = null;
		DBObject item = null;
		p = Pattern.compile(regularExp, Pattern.DOTALL);
	    Matcher m = null;
		
		m = p.matcher(log);
		
		// currentMatch
		if(m.matches()) {
			int groupCount = m.groupCount();
			if(groupCount > 1) {
				String columnName = null;
				String columnType = null;
				String dateFormat = null;
				item = new BasicDBObject();
				item.put(KEY_APP_NAME, appName);
				for(int i=1;i<=groupCount;i++) {
					DBObject columnInfo = (DBObject)columnsInfo.get(i-1);
					columnName = columnInfo.get(COLUMN_INFO_NAME).toString();
					columnType = columnInfo.get(COLUMN_INFO_TYPE).toString();
					
					if(COLUMN_TYPE_TIMESTAMP.equals(columnType) || COLUMN_TYPE_DATE.equals(columnType)) {
						dateFormat = columnInfo.get(COLUMN_INFO_DATEFORMAT).toString();
						SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
						try{
							item.put(columnName, sdf.parse(m.replaceAll("$" + i).trim()));	
						}catch(Exception e){
							item.put(columnName, dateFormat);
						}
						
					}else if(COLUMN_TYPE_NUMBER.equals(columnType)){
						item.put(columnName, Integer.parseInt(m.replaceAll("$" + i).trim()));
					}else if(COLUMN_TYPE_IGNORE.equals(columnType)) {
						continue;
					}else{
						item.put(columnName, m.replaceAll("$" + i).trim());	
					}
				}
				logger.debug("[{}/{}] parsedData={}", new Object[]{appName, id, item.toString()});
			}
		}else{
			throw new LogParseException("\ndata is not match :" + regularExp + "==>" + log);
		}
		
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#cancel()
	 */
	@Override
	public boolean cancel() {
		logger.info("[{}/{}] is stop.", appName, id);
		return super.cancel();
	}
	
	/**
	 * @param regexp
	 * @return
	 */
	private int lineFeedCount(String regexp) {
		Pattern pattern = Pattern.compile("\\\\n");
		Matcher match = pattern.matcher(regexp);
		int count = 0;
		while(match.find()) {
			count++;
		}
		return count;
	}

}
