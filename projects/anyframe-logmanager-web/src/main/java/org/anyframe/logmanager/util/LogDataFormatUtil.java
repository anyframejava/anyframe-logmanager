/* 
 * Copyright (C) 2002-2012 the original author or authors.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 * @author jaehyoung.eum
 *
 */
public class LogDataFormatUtil {

	private static final String REGEXP_DATE = "\\$([^ ][A-Za-z]*)\\{(.+?)\\}";
	private static final String REGEXP_NORMAL = "\\$[^ ][A-Za-z]*";
	
	public static Map<String, Object> convertLogDataFormat(String expression) {
		Pattern patternDate = null;
		Pattern patternNormal = null;
		Matcher matcherDate = null;
		Matcher matcherDate2 = null;
		Matcher matcherNormal = null;
		String temp = expression;
		String result = temp;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> columnNameList = new ArrayList<String>();
		List<String> dateFormatList = new ArrayList<String>();
		Map<String, String> dateFormatMap = new HashMap<String, String>();
		
		patternDate = Pattern.compile(REGEXP_DATE);
		patternNormal = Pattern.compile(REGEXP_NORMAL);
		
		matcherDate = patternDate.matcher(temp);
		
		matcherNormal = patternNormal.matcher(temp);
		while(matcherNormal.find()){
			String columnName = matcherNormal.group().substring(1);
			columnNameList.add(columnName);
		}
		
		while(matcherDate.find()) {
			String s = matcherDate.group();
			matcherDate2 = patternDate.matcher(s);
			if(matcherDate2.matches()) {
				String columnName = matcherDate2.replaceAll("$1");
				String dateFormat = matcherDate2.replaceAll("$2");
				result = result.replaceAll(escapeColumnFormat(s), "(" + convertDateFormat(dateFormat) + ")");
				dateFormatMap.put(columnName, dateFormat);
			}
		}
		
		matcherNormal = patternNormal.matcher(result);
		while(matcherNormal.find()) {
			String s = matcherNormal.group();
			result = result.replaceAll(escapeColumnFormat(s), "(.+?)");
			dateFormatMap.put(s.substring(1), "");
		}
		
		Iterator<String> i = columnNameList.iterator();
		while(i.hasNext()) {
			String key = i.next();
			dateFormatList.add(dateFormatMap.get(key));
		}
		
		resultMap.put("columnNameList", columnNameList);
		resultMap.put("dateFormatList", dateFormatList);
		resultMap.put("regexp", "^" + result + "$");
		return resultMap;
	}
	
	private static String convertDateFormat(String f) {
		Pattern p = null;
		Matcher m = null;
		String[] regex = new String[]{"yyyy", "yy", "y", "MMMM", "MMM", "MM", "M", "dddd", "ddd", "dd", "d", "HH", "H", "hh", "h", "mm", "m", "ss", "s", "t", "x", "SSS"};
		for(int i=0;i<regex.length;i++) {
			p = Pattern.compile(regex[i]);
			m = p.matcher(f);
			if(m.find()) {
				f = m.replaceAll("\\\\N{" + regex[i].length() + "}");
			}
		}
		return f.replaceAll("N", "\\\\d");
	}
	
	/**
	 * @param param
	 * @return
	 */
	private static String escapeColumnFormat(String param) {
		return param.replaceAll("\\$", "\\[\\$\\]").replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Map<String, Object> r = LogDataFormatUtil.convertLogDataFormat("\\[$level\\] \\[$timestamp{yyyy-MM-dd HH:mm:ss,SSS}\\] $logger $message");
//		System.out.println(r.get("regexp").toString());
	}

}
