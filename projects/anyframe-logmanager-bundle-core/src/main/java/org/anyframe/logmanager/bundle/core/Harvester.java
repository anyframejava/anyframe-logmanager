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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class Harvester extends TimerTask {

	private String agentId;
	private String appenderName;
	private String logPath;
	private String conversionBsonPattern;
	private int columnDelimiter;
	private int rowDelimiter;
	private int timestampIndex;
	private String timestampDatePattern;
	private DBCollection logevents;
	private DBCollection logappender;
	private long lastTimestamp;
	private String appName;
	/**
	 * @param appenderName
	 */
	public Harvester(String agentId, DBCollection logevents, DBCollection logappender, String appName, String appenderName, String logPath, String conversionBsonPattern,
			int columnDelimiter, int rowDelimiter, int timestampIndex, String timestampDatePattern, long lastTimestamp) {
		this.agentId = agentId;
		this.logevents = logevents;
		this.logappender = logappender;
		this.appName = appName;
		this.appenderName = appenderName;
		this.logPath = logPath;
		this.conversionBsonPattern = conversionBsonPattern;
		this.columnDelimiter = columnDelimiter;
		this.rowDelimiter = rowDelimiter;
		this.timestampIndex = timestampIndex;
		this.timestampDatePattern = timestampDatePattern;
		this.lastTimestamp = lastTimestamp;
	}
	
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("start - " + appenderName);

			File f = new File(logPath);

			if (!f.exists()) {
				throw new Exception(logPath + " doex not exist.");
			}

			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			boolean isColumnOpen = false;
			int columnIndex = 1;
			StringBuffer col = new StringBuffer();
			String json = conversionBsonPattern;
			SimpleDateFormat sdf = new SimpleDateFormat(timestampDatePattern);
			Date timestamp = null;
			DBObject bson = null;
			String timestampString = null;

			long insertCount = 0;

			while (true) {

				int ch = br.read();// 읽을 문자가 없으면 -1을 리턴
				if (ch == -1) {
					break;
				} else if (ch == rowDelimiter) {
					columnIndex = 1;
					col = new StringBuffer();
					isColumnOpen = false;
					// System.out.println(json);
					if (json.length() > 0) {
						Object obj = JSON.parse(json);
						if (obj instanceof DBObject) {
							bson = (DBObject) obj;
							if (timestamp == null) {
								bson.put("timestamp", timestampString);
							} else {
								bson.put("timestamp", timestamp);
								long currentTimestamp = timestamp.getTime();
								if (currentTimestamp > lastTimestamp) {
									lastTimestamp = currentTimestamp;
								} else {
									continue;
								}
							}

							// System.out.println(bson.toString());
							try {
								logevents.insert(bson);
								insertCount++;
							} catch (MongoException me) {
								me.printStackTrace();
							}
						}
					}
					timestamp = null;
					timestampString = null;
					bson = null;
					json = conversionBsonPattern;
					continue;
				} else if (ch == columnDelimiter) {
					if (isColumnOpen) {
						// System.out.print("[" + columnIndex + "]" +
						// col.append("\n").toString());
						if (columnIndex == timestampIndex) {
							try {
								timestampString = col.toString().trim();
								timestamp = sdf.parse(timestampString);
							} catch (ParseException pe) {
								timestamp = null;
							}

						} else {
							if (columnIndex < 10) {
								json = json.replace("%" + columnIndex, col.toString().trim());
							} else {
								// System.out.println("json=" + json);
								// System.out.println("columnIndex=" +
								// columnIndex);
								// System.out.println("col.toString().trim()="
								// + col.toString().trim());
								json = json.replace("%." + columnIndex, col.toString().trim());
							}
						}

						col = new StringBuffer();
						isColumnOpen = false;
						// System.out.println("columnIndex=" + columnIndex);
						columnIndex++;
					} else {
						isColumnOpen = true;

					}
				} else {
					if (isColumnOpen) {
						// System.out.print((char) ch);// 읽어온 문자 출력
						col.append((char) ch);
					} else {
						continue;
					}
				}
			}

			br.close();
			fr.close();

			BasicDBObject where = new BasicDBObject("appenderName", appenderName).append("agentId", agentId).append("appName", appName);
			BasicDBObject set = new BasicDBObject("$set", new BasicDBObject("lastTimestamp", lastTimestamp));

			WriteResult wr = logappender.update(where, set);
			System.out.println(wr.getN() + " is updated.");

			System.out.println("==========================================================");
			System.out.println("Insert count is " + insertCount);
			System.out.println("Last Timestamp is " + lastTimestamp);
			System.out.println("==========================================================");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#cancel()
	 */
	@Override
	public boolean cancel() {
		System.out.println("stop - " + appenderName);
		return super.cancel();
	}

}
