package org.anyframe.logmanager.bundle.core;

import java.net.InetAddress;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private String agentId;
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

		agentId = InetAddress.getLocalHost().getHostAddress() + ":" + bundleContext.getProperty("org.osgi.service.http.port");

		System.out.println("Anyfrmae Log Agent Service [" + agentId + "] is started.");

		context = bundleContext;

		String dbName = context.getProperty("mongo.database.name");
		
		initMongo(context);
		
		DB db = mongo.getDB(dbName);
		
		HarvestManager.init(agentId, context, db);
		
		HarvestManager.startManager();
		
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		HarvestManager.stopManager();
		
		closeMongo();
		
		context = null;
		
		System.out.println("Goodbye!");
	}
	
	/**
	 * @param context
	 * @throws Exception
	 */
	private void initMongo(BundleContext context) throws Exception {
		
		if(context == null) throw new Exception("Context is null.");
		
		String mongoSvr = context.getProperty("mongo.host");
		int mongoPort = Integer.parseInt(context.getProperty("mongo.port"));
		System.out.println("MongoDB connect to - " + mongoSvr + ":" + mongoPort);
		
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
		
		if(mongo != null) {
			System.out.println("MongoDB connected - " + addr.getHost() + ":" + addr.getPort());
		} else {
			throw new Exception("MongoDB connection is null.");
		}
	}

	/**
	 * @throws Exception
	 */
	private void closeMongo() throws Exception {
		if(mongo != null) {
			mongo.close();
		}
	}
}
