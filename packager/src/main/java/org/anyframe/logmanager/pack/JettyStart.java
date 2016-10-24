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
package org.anyframe.logmanager.pack;

import java.io.File;

import org.anyframe.logmanager.pack.util.JarUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author arumb-laptop
 *
 */
public class JettyStart {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		if(args.length < 2) {
			System.out.println("Usage:java ... org.anyframe.logmanager.pack.JettyStart [port] [contextPath]");
			return;
		}
		
		String warPath = null;
		String contextPath = null;
		String port = null;
		
		// warPath = args[0];
		port = args[0];
		contextPath = args[1];
		
		File currentPath = new File(".");
		String[] currentFileList = currentPath.list();
		for(int i=0;i<currentFileList.length;i++) {
			if(currentFileList[i].indexOf("anyframe-logmanager-web") != -1 && currentFileList[i].lastIndexOf(".war") != -1) {
				warPath = currentFileList[i];
			}
		}
		
		if(warPath == null) {
			throw new Exception("anyframe-logmanager-web-XXX.war does not exist.\n check directory resources.");
		}else{
			System.out.println(warPath +" is detected.");
		}
		
		File[] files = {new File("conf/log4j.xml"), 
				new File("conf/context.properties"), 
				new File("conf/mongo.properties")};
		
		File warF = new File(warPath);
		JarUtils.addFilesToExistingJar(warF, files);

		Server server = new Server(Integer.parseInt(port));
		
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath(contextPath);
		webapp.setWar(warPath);
		
		server.setHandler(webapp);
		
		System.out.println("Jetty boot process will be start.");
		
		server.start();
		server.join();
	}

}
