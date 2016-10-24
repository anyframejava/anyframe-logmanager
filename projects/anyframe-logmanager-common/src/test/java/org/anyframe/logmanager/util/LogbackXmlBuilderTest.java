/* 
 * Copyright (C) 2002-2012 Robert Stewart (robert@wombatnation.com)
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
package org.anyframe.logmanager.util;

import java.io.InputStream;
import java.util.List;

import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.Logger;
import org.anyframe.logmanager.domain.Root;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogbackXmlBuilderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		InputStream is = LogbackXmlBuilderTest.class.getResourceAsStream("/logback.xml");
		LogbackXmlBuilder logbackBuilder = new LogbackXmlBuilder(is);
		Root root = logbackBuilder.getLogbackRoot();
		List<Appender> appenders = logbackBuilder.getLogbackAppender();
		List<Logger> loggers = logbackBuilder.getLogbackLogger();
		
		int appenderSize = appenders == null ? 0 : appenders.size();
		int loggerSize = loggers == null ? 0 : loggers.size();
		System.out.println("=============== ROOT ===============");
		System.out.println("Level=" + root.getLevel());
		System.out.println("AppenderRefs=" + root.getAppenderRefs().toString());
		System.out.println("====================================");
		
		for(int i=0;i<appenderSize;i++) {
			Appender appender = appenders.get(i);
			System.out.println("<Appender name=\"" + appender.getName() + "\" class=\"" + appender.getAppenderClass() + "\">");
			System.out.println("</Appender>");
		}
		
		for(int i=0;i<loggerSize;i++) {
			Logger logger = loggers.get(i);
			System.out.println("<Logger name=\"" + logger.getName() + "\" level=\"" + logger.getLevel() + "\" additivity=\"" + logger.getAdditivity() + "\">");
			System.out.println(logger.getAppenderRefs());
			System.out.println("</Logger>");
		}
	}

}
