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
package org.anyframe.logmanager.bundle.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.anyframe.logmanager.bundle.core.HarvestManager;
import org.anyframe.logmanager.bundle.service.LogAgentBundleService;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogAgentBundleServiceImpl implements LogAgentBundleService {
	
	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.bundle.service.LogAgentService#ping()
	 */
	public String ping(String message) throws Exception {
		String r = "I'm alive";
		System.out.println("LogAgentBundleServiceImpl.ping():"  + message);
		return r;
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#stopHarvestManager()
	 */
	public void stopHarvestManager() throws Exception {
		HarvestManager.stopManager();
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#restartHarvestManager()
	 */
	public void restartHarvestManager() throws Exception {
		HarvestManager.stopManager();
		HarvestManager.startManager();
	}


	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#getLog4jXmlInfo(java.lang.String)
	 */
	public InputStream getLog4jXmlInfo(String log4jXmlPath) throws Exception {
		
		File f = new File(log4jXmlPath);
		if(!f.exists()) throw new Exception(log4jXmlPath + " does not exist.");
		
		return new FileInputStream(f);
	}

	/* (non-Javadoc)
	 * @see org.anyframe.logmanager.bundle.service.LogAgentBundleService#saveLog4jXmlInfo(java.lang.String)
	 */
	public void saveLog4jXmlInfo(String log4jXmlPath, String log4jXmlText) throws Exception {
		
		FileOutputStream fos = new FileOutputStream(log4jXmlPath);
		fos.write(log4jXmlText.getBytes("utf-8"));
		fos.close();
		
	}
}
