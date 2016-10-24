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
package org.anyframe.logmanager.bundle.service;

import java.io.InputStream;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public interface LogAgentBundleService {

	/**
	 * @return
	 * @throws Exception
	 */
	public String ping(String message) throws Exception;
	
	/**
	 * @return
	 * @throws Exception
	 */
	public void stopHarvestManager() throws Exception;
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public void restartHarvestManager() throws Exception;
	
	/**
	 * @param LoggingPolicyFilePath
	 * @return
	 * @throws Exception
	 */
	public InputStream getLoggingPolicyFileInfo(String LoggingPolicyFilePath) throws Exception;
	
	/**
	 * @param LoggingPolicyFilePath
	 * @throws Exception
	 */
	public void saveLoggingPolicyFileInfo(String LoggingPolicyFilePath, String LoggingPolicyFileText) throws Exception;
	
}
