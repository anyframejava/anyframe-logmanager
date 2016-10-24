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

import java.net.InetAddress;

import org.anyframe.logmanager.bundle.exception.LogManagerBundleException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

/**
 * This is Activator class.
 * 
 * @author Jaehyoung Eum
 */
public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private static Logger logger = LoggerFactory.getLogger(Activator.class);

	private Mongo mongo;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		try{
			String agentId = InetAddress.getLocalHost().getHostAddress() + ":" + bundleContext.getProperty("org.osgi.service.http.port");

			logger.info("Anyfrmae Log Agent Service [{}] is started.", agentId);

			context = bundleContext;

			String dbName = context.getProperty("mongo.database.name");

			initMongo(context);

			DB db = mongo.getDB(dbName);

			LogCollectionManager.init(agentId, context, db);

			LogCollectionManager.startManager();
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			stop(bundleContext);
			System.exit(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		LogCollectionManager.stopManager();

		closeMongo();

		context = null;

		logger.info("Goodbye!");
	}

	/**
	 * @param context
	 * @throws Exception
	 */
	private void initMongo(BundleContext context) throws Exception {

		if (context == null) {
			throw new LogManagerBundleException("Context is null.");
		}

		String mongoSvr = context.getProperty("mongo.host");
		int mongoPort = Integer.parseInt(context.getProperty("mongo.port"));
		logger.info("MongoDB connect to - " + mongoSvr + ":" + mongoPort);

		MongoOptions options = new MongoOptions();
		options.connectionsPerHost = Integer.parseInt(context.getProperty("mongo.connectionsPerHost"));
		options.autoConnectRetry = Boolean.parseBoolean(context.getProperty("mongo.autoConnectRetry"));
		options.connectTimeout = Integer.parseInt(context.getProperty("mongo.connectTimeout"));
		options.threadsAllowedToBlockForConnectionMultiplier = Integer.parseInt(context.getProperty("mongo.threadsAllowedToBlockForConnectionMultiplier"));
		options.maxWaitTime = Integer.parseInt(context.getProperty("mongo.maxWaitTime"));
		options.connectTimeout = Integer.parseInt(context.getProperty("mongo.connectTimeout"));
		options.socketKeepAlive = Boolean.parseBoolean(context.getProperty("mongo.socketKeepAlive"));
		options.socketTimeout = Integer.parseInt(context.getProperty("mongo.socketTimeout"));

		ServerAddress addr = new ServerAddress(mongoSvr, mongoPort);

		mongo = new Mongo(addr, options);

		if (mongo != null) {
			logger.info("MongoDB connected - " + addr.getHost() + ":" + addr.getPort());
		} 
	}

	/**
	 * @throws Exception
	 */
	private void closeMongo() throws Exception {
		if (mongo != null) {
			mongo.close();
		}
	}
}
