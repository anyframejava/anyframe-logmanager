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
package org.anyframe.logmanager.bundle.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.anyframe.logmanager.bundle.core.LogCollectionManager;
import org.anyframe.logmanager.bundle.exception.LogManagerBundleException;
import org.anyframe.logmanager.bundle.service.LogAgentBundleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is LogAgentBundleServiceImpl class.
 * 
 * @author Jaehyoung Eum
 */
public class LogAgentBundleServiceImpl implements LogAgentBundleService {

	private static Logger logger = LoggerFactory.getLogger(LogAgentBundleServiceImpl.class);
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anyframe.logmanager.bundle.service.LogAgentService#ping()
	 */
	public String ping(String message) throws Exception {
		String r = "I'm alive";
		logger.info("LogAgentBundleServiceImpl.ping():" + message);
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#
	 * stopHarvestManager()
	 */
	public void stopHarvestManager() throws Exception {
		LogCollectionManager.stopManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#
	 * restartHarvestManager()
	 */
	public void restartHarvestManager() throws Exception {
		LogCollectionManager.stopManager();
		LogCollectionManager.startManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#
	 * getLoggingPolicyFileInfo(java.lang.String)
	 */
	public InputStream getLoggingPolicyFileInfo(String loggingPolicyFilePath) throws Exception {

		File f = new File(loggingPolicyFilePath);
		if (!f.exists())
			throw new LogManagerBundleException(loggingPolicyFilePath + " does not exist.");

		return new FileInputStream(f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#
	 * saveLoggingPolicyFileInfo(java.lang.String)
	 */
	public void saveLoggingPolicyFileInfo(String loggingPolicyFilePath, String loggingPolicyFileText) throws Exception {

		FileOutputStream fos = new FileOutputStream(loggingPolicyFilePath);
		fos.write(loggingPolicyFileText.getBytes("utf-8"));
		fos.close();

	}
}
