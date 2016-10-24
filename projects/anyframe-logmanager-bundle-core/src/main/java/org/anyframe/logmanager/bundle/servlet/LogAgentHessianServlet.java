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
package org.anyframe.logmanager.bundle.servlet;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.anyframe.logmanager.bundle.service.LogAgentBundleService;
import org.anyframe.logmanager.bundle.service.impl.LogAgentBundleServiceImpl;
import org.osgi.service.http.HttpService;

import com.caucho.hessian.server.HessianServlet;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogAgentHessianServlet extends HessianServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8756660616219867035L;
	
	public final static String NAME = "LogAgentService";
	
	protected void setHttpService(HttpService hs) {
		try {
			System.out.println("HttpService /" + NAME + " is started.");
			System.out.println("\nLog Agent Service is ready.");
			// HessianServlet uses currentThread's contextClassLoader to load the
			// home-api and home-class instances.
			Thread.currentThread().setContextClassLoader(LogAgentBundleServiceImpl.class.getClassLoader());
			
			Hashtable<String, String> prop  = new Hashtable<String, String>();
			prop.put("home-api", LogAgentBundleService.class.getName());
			prop.put("home-class", LogAgentBundleServiceImpl.class.getName());
			
			hs.registerServlet(
					"/" + LogAgentHessianServlet.NAME, 
					this, 
					prop, 
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void service(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		Thread.currentThread().setContextClassLoader(LogAgentBundleServiceImpl.class.getClassLoader());
		super.service(request, response);
	}
}
