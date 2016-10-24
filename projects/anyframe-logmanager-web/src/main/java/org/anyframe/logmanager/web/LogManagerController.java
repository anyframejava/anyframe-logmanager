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
package org.anyframe.logmanager.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.common.LogManagerException;
import org.anyframe.logmanager.domain.Account;
import org.anyframe.logmanager.domain.AnalysisLog;
import org.anyframe.logmanager.domain.LogAgent;
import org.anyframe.logmanager.domain.LogAppender;
import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.domain.LogSearchCondition;
import org.anyframe.logmanager.service.LogAgentService;
import org.anyframe.logmanager.service.LogApplicationService;
import org.anyframe.logmanager.service.LogSearchService;
import org.anyframe.logmanager.util.ExcelInfoHandler;
import org.anyframe.logmanager.util.ExcelInfoHandler.ColumnInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;

/**
 * Log Manager Controller Class
 * 
 * @author Jaehyoung Eum
 * 
 */
@Controller("logManagerController")
@RequestMapping("/logManager.do")
public class LogManagerController {

	private static Logger logger = LoggerFactory.getLogger(LogManagerController.class);

	@Inject
	@Named("logSearchService")
	private LogSearchService service;

	@Inject
	@Named("logApplicationService")
	private LogApplicationService appService;
	
	@Inject
	@Named("logAgentService")
	private LogAgentService agentService;

	/**
	 * Log Application Save method 
	 * Reference to Array binding...
	 * (for Ajax Request)
	 * @param id
	 * @param loggers
	 * @param appenders
	 * @param appName
	 * @param log4jXmlPath
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=saveLogApplication")
	public String saveLogApplication(@RequestParam(value = "id") String id, 
			@RequestParam(value = "appenders[]", required = false) String[] appenders, 
			@RequestParam(value = "pollingTimes[]", required = false) String[] pollingTimes,
			@RequestParam(value = "monitorLevels[]", required = false) int[] monitorLevels,
			@RequestParam(value = "fileAppenders[]", required = false) boolean[] fileAppenders,
			@RequestParam(value = "collectionNames[]", required = false) String[] collectionNames,
			@RequestParam(value = "status[]", required = false) int[] status,
			@RequestParam(value = "appName") String appName, 
			@RequestParam(value = "agentId") String agentId,
			@RequestParam(value = "log4jXmlPath") String log4jXmlPath,
			Model model, HttpServletRequest request) throws Exception {
		
		LogApplication param = new LogApplication();
		if ("".equals(id)) {
			param.setId(null);
		} else {
			param.setId(id);
		}
		param.setAppName(appName);
		param.setLog4jXmlPath(log4jXmlPath);
		param.setAgentId(agentId);
		param.setStatus(LogManagerConstant.APP_STATUS_INACTIVE);

		appService.saveLogApplication(param, appenders, pollingTimes, monitorLevels, fileAppenders, collectionNames, status);

		return "jsonView";
	}
	
	/**
	 * check for log application duplication
	 * @param appName
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=checkApplicationExist")
	public String checkApplicationExist(LogApplication param, Model model, HttpServletRequest request) throws Exception {
		LogApplication app = appService.checkLogApplicationExist(param);
		if(app == null) {
			model.addAttribute("isExist", false);
		}else{
			model.addAttribute("isExist", true);
		}
		return "jsonView";
	}

	/**
	 * Log Application delete method
	 * (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=deleteApplication")
	public String deleteApplication(LogApplication param, Model model, HttpServletRequest request) throws Exception {
		appService.deleteApplication(param);
		return "jsonView";
	}

	/**
	 * Log Application reload method 
	 * (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=reloadApplication")
	public String reloadApplication(LogApplication param, Model model, HttpServletRequest request) throws Exception {
		appService.reloadApplication(param);
		return "jsonView";
	}

	/**
	 * Get Appender List method
	 * (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=getAppenderList")
	public String getAppenderList(LogApplication param, Model model, HttpSession session) throws Exception {
		Account loginAccount = (Account)session.getAttribute("loginAccount");
		List<LogAppender> appenders = null;
		if(LogManagerConstant.COLLECTION_BASED.equals(param.getAppName())){
			appenders = appService.getAppenderList(loginAccount.getUserType());
		}else{
			appenders = appService.getAppenderList(param.getAppName(), loginAccount.getUserType());	
		}
		
		model.addAttribute("appenders", appenders);
		return "jsonView";
	}
	
	/**
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=getAppList")
	public String getAppList(Model model) throws Exception {
		model.addAttribute("apps", service.getActiveLogApplicationList());
		return "jsonView";
	}
	
	/**
	 * @param collectionName
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=getAppListByCollection")
	public String getAppListByCollection(@RequestParam(value="collectionName") String collectionName, Model model, HttpSession session) throws Exception {
		Account account = (Account)session.getAttribute("loginAccount");
		List<LogAppender> appenders = appService.getLogAppenderListByCollection(collectionName, account.getUserType());
		
		model.addAttribute("apps", appenders);
		return "jsonView";
	}

	/**
	 * log4j.xml file load method
	 * (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=loadLog4jXml")
	public String loadLog4jXml(@RequestParam(value="agentId") String agentId, 
			@RequestParam(value="log4jXmlPath") String log4jXmlPath, 
			Model model, HttpServletRequest request) throws Exception {
		
//		LogApplication app = null;
//		app = appService.loadLog4jXml(param);
//		model.addAttribute("app", app);
		try{
			LogApplication logApplication = agentService.getLog4jXmlInfo(agentId, log4jXmlPath);
			String log4jxml = agentService.getLog4jXmlInfoString(agentId, log4jXmlPath);
			if(logApplication == null) throw new Exception(log4jXmlPath + " info is null.");
			
			model.addAttribute("logApplication", logApplication);
			model.addAttribute("xmlString", log4jxml);
		}catch(LogManagerException e){
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
			return "jsonView";
		}
		
		return "jsonView";
	}

	/**
	 * view log application detail info...
	 * (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=viewAppDetail")
	public String viewAppDetail(LogApplication param, Model model, HttpSession session) throws Exception {
		LogApplication app = null;
		List<LogAppender> currentAppenders = null;
		Account loginAccount = (Account)session.getAttribute("loginAccount");
		app = appService.getLogApplicationById(param);
		try{
			LogApplication r = agentService.getLog4jXmlInfo(app.getAgentId(), app.getLog4jXmlPath());
			app.setAppenders(r.getAppenders());
			model.addAttribute("isConnect", true);
		}catch(LogManagerException e) {
			model.addAttribute("isConnect", false);
		}
		currentAppenders = appService.getAppenderList(app.getAgentId(), app.getAppName(), loginAccount.getUserType());
		model.addAttribute("currAppenders", currentAppenders);
		model.addAttribute("app", app);
		return "jsonView";
	}

	/**
	 * get log application list
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=appList")
	public String appList(LogApplication param, Model model, HttpServletRequest request) throws Exception {
		List<LogApplication> list = null;
		list = appService.getLogApplicationList(param);
		model.addAttribute("list", list);
		return "logmanager/appList";
	}
	
	/**
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=getActiveAgentList")
	public String getActiveAgentList(Model model) throws Exception {
		List<LogAgent> agentList = null;
		agentList = agentService.getLogAgentList(LogManagerConstant.AGENT_STATUS_ACTIVE);
		model.addAttribute("agentList", agentList);
		return "jsonView";
	}

	/**
	 * private method for log search function
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @throws Exception
	 */
	private List<AnalysisLog> getLogList(LogSearchCondition searchCondition, Model model, HttpServletRequest request) throws Exception {
		List<AnalysisLog> list = null;
		
		if (searchCondition.getAppName() != null) {
			if(searchCondition.isLogTailingMode()){
				if(!"".equals(searchCondition.getPreviousTimestamp())) {
					searchCondition.setUseToDate(false);
					searchCondition.setUseFromDate(true);
					SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss, SSS");
					Date f = dateTimeFormat.parse(searchCondition.getPreviousTimestamp());
					f.setTime(f.getTime() + ((long)1));
					searchCondition.setFromDateTime(f);
					logger.debug("from:{}", searchCondition.getFromDateTime());
				}else{
					return new ArrayList<AnalysisLog>();
				}
			}else{
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-ddHHmm");
				logger.debug("from:{}", searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute());
				logger.debug("to:{}", searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute());
				if (searchCondition.isUseFromDate())
					searchCondition.setFromDateTime(dateTimeFormat.parse(searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute()));
				if (searchCondition.isUseToDate())
					searchCondition.setToDateTime(dateTimeFormat.parse(searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute()));
			}
			searchCondition.setCollection(searchCondition.getAppenderName());
			list = service.searchAnalysisLog(searchCondition);
		} else {
			Date currentDateTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
			SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
			String currentDate = dateFormat.format(currentDateTime);
			String currentHour = hourFormat.format(currentDateTime);
			String currentMinute = Integer.toString((int) (Math.floor((Integer.parseInt(minuteFormat.format(currentDateTime)) / 10)) * 10));
			if ("0".equals(currentMinute))
				currentMinute = "00";

			searchCondition.setFromDate(currentDate);
			searchCondition.setFromHour(currentHour);
			searchCondition.setFromMinute(currentMinute);

			searchCondition.setToDate(currentDate);
			searchCondition.setToHour(currentHour);
			searchCondition.setToMinute(currentMinute);

			searchCondition.setMatchedLogOnly(true);
		}
		
		return list;
	}
	
	/**
	 * log search for text type view
	 * (for Ajax Request)
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=analysis")
	public String analysis(LogSearchCondition searchCondition, Model model, HttpServletRequest request) throws Exception {
		logger.debug("searchCondition=\n{}", searchCondition.toString());
		List<AnalysisLog> list = null;
		list = getLogList(searchCondition, model, request);
		if(list == null) list = new ArrayList<AnalysisLog>();
		model.addAttribute("searchCondition", searchCondition);
		model.addAttribute("listCount", list.size());
		model.addAttribute("list", list);
		return "jsonView";
	}
	
	/**
	 * log search form load for text type view
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=analysisForm")
	public String analysisForm(LogSearchCondition searchCondition, Model model, HttpServletRequest request) throws Exception {
		List<LogApplication> appList = service.getActiveLogApplicationList();
		List<String> appNameList = new ArrayList<String>();
		for (int i = 0; i < appList.size(); i++) {
			appNameList.add(appList.get(i).getAppName());
		}
		
		Date currentDateTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		String currentDate = dateFormat.format(currentDateTime);
		String currentHour = hourFormat.format(currentDateTime);
		String currentMinute = Integer.toString((int) (Math.floor((Integer.parseInt(minuteFormat.format(currentDateTime)) / 10)) * 10));
		if ("0".equals(currentMinute))
			currentMinute = "00";

		if(searchCondition.getFromDate() == null) {
			searchCondition.setFromDate(currentDate);
			searchCondition.setFromHour(currentHour);
			searchCondition.setFromMinute(currentMinute);
		}
		
		if(searchCondition.getToDate() == null) {
			searchCondition.setToDate(currentDate);
			searchCondition.setToHour(currentHour);
			searchCondition.setToMinute(currentMinute);
		}
		
		if(searchCondition.getAppName() == null) {
			searchCondition.setMatchedLogOnly(true);
		}
		
		model.addAttribute("pollingTerm", LogManagerConstant.POLLING_DURATION_SECOND);
		model.addAttribute("appNameList", appNameList);
		model.addAttribute("levels", LogManagerConstant.LEVELS);
		model.addAttribute("hours", LogManagerConstant.HOURS);
		model.addAttribute("minutes", LogManagerConstant.MINUTES);
		model.addAttribute("searchCondition", searchCondition);
		return "logmanager/analysis";
	}
	
	/**
	 * log search form load for grid type view
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=analysis4gridForm")
	public String analysis4gridForm(LogSearchCondition searchCondition, Model model, HttpSession session) throws Exception {
		logger.debug("searchCondition4gridForm=\n{}", searchCondition.toString());
		List<LogApplication> appList = service.getActiveLogApplicationList();
		List<String> appNameList = new ArrayList<String>();
		for (int i = 0; i < appList.size(); i++) {
			appNameList.add(appList.get(i).getAppName());
		}
		
		Date currentDateTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		String currentDate = dateFormat.format(currentDateTime);
		String currentHour = hourFormat.format(currentDateTime);
		String currentMinute = Integer.toString((int) (Math.floor((Integer.parseInt(minuteFormat.format(currentDateTime)) / 10)) * 10));

		if ("0".equals(currentMinute))
			currentMinute = "00";
		
		if(searchCondition.getFromDate() == null) {
			searchCondition.setFromDate(currentDate);
			searchCondition.setFromHour(currentHour);
			searchCondition.setFromMinute(currentMinute);
		}
		
		if(searchCondition.getToDate() == null) {
			searchCondition.setToDate(currentDate);
			searchCondition.setToHour(currentHour);
			searchCondition.setToMinute(currentMinute);
		}
		
		if(searchCondition.getAppName() == null) {
			searchCondition.setMatchedLogOnly(true);
		}

		searchCondition.setPageIndex(1);
		searchCondition.setPageSize(10);
		
		model.addAttribute("appNameList", appNameList);
		model.addAttribute("levels", LogManagerConstant.LEVELS);
		model.addAttribute("hours", LogManagerConstant.HOURS);
		model.addAttribute("minutes", LogManagerConstant.MINUTES);
		model.addAttribute("searchCondition", searchCondition);
		return "logmanager/analysis4gridForm";
	}
	
	/**
	 * load log data for grid type view
	 * (for Ajax Request)
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=analysis4grid")
	public String analysis4grid(LogSearchCondition searchCondition, Model model, HttpServletRequest request) throws Exception {
		
		List<AnalysisLog> list = null;
		
		list = getLogList(searchCondition, model, request);
		
		Map<String, Object> jsonModel = new HashMap<String, Object>();
		
		int maxPage = 0;
		maxPage = (int)(searchCondition.getTotalCount() / searchCondition.getPageSize());
		if((searchCondition.getTotalCount() % searchCondition.getPageSize()) > 0) maxPage++;
		
		jsonModel.put("page", searchCondition.getPageIndex() + "");
		jsonModel.put("total", maxPage + "");
		jsonModel.put("records", searchCondition.getTotalCount() + "");
		jsonModel.put("rows", list);
		
		model.addAllAttributes(jsonModel);
		
		return "jsonView";
	}

	/**
	 * log data export for text file type
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=txtExport")
	public String txtExport(LogSearchCondition searchCondition, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		searchCondition.setPageIndex(-1);
		analysis(searchCondition, model, request);
		String fileName = null;
		String sDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("ko_KR"));
		sDate = sdf.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(searchCondition.getAppName().substring(searchCondition.getAppName().lastIndexOf("/")+1));
		sb.append("_").append(searchCondition.getCollection()).append("_").append(sDate).append(".txt");
		fileName = sb.toString();
		response.reset();
		response.setContentType("text/plain");
		String userAgent = request.getHeader("User-Agent");

		if (userAgent.indexOf("MSIE 5.5") > -1) {
			response.setHeader("Content-Disposition", "filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
		} else if (userAgent.indexOf("MSIE") > -1) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + "\";");
		} else {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("euc-kr"), "latin1") + "\";");
		}

		response.setHeader("Content-Transfer-Encoding", "binary;");
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		return "logmanager/export/txtExportForm";
	}

	/**
	 * log data export for excel file type
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=xlsExport")
	public void xlsExport(LogSearchCondition searchCondition, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		searchCondition.setPageIndex(-1);
		searchCondition.setCollection(searchCondition.getAppenderName());
		
		String fileName = null;
		String sDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("ko_KR"));
		sDate = sdf.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(searchCondition.getAppName().substring(searchCondition.getAppName().lastIndexOf("/")+1));
		sb.append("_").append(searchCondition.getCollection()).append("_").append(sDate).append(".xls");
		fileName = sb.toString();
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-ddHHmm");
		logger.debug("from:{}", searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute());
		logger.debug("to:{}", searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute());
		if (searchCondition.isUseFromDate())
			searchCondition.setFromDateTime(dateTimeFormat.parse(searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute()));
		if (searchCondition.isUseToDate())
			searchCondition.setToDateTime(dateTimeFormat.parse(searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute()));
		
		List<AnalysisLog> resultList = service.searchAnalysisLog(searchCondition);
		
		response.reset();
		response.setContentType("application/x-msexcel;charset=MS949");
		//response.setContentType("application/octet-stream");
		String userAgent = request.getHeader("User-Agent");

		if (userAgent.indexOf("MSIE 5.5") > -1) {
			response.setHeader("Content-Disposition", "filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\";");
		} else if (userAgent.indexOf("MSIE") > -1) {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + "\";");
		} else {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("euc-kr"), "latin1") + "\";");
		}
		response.setHeader("Content-Description", "JSP Generated Data");
		response.setHeader("Content-Transfer-Encoding", "binary;");
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");
		
		
		HSSFWorkbook workbook = new HSSFWorkbook();
    	HSSFSheet sheet = workbook.createSheet(fileName);
    	
    	OutputStream fileOut = null;
    	try {
    		fileOut = response.getOutputStream();
    		HSSFRow row;
    		
    		HSSFDataFormat  df = workbook.createDataFormat();
    		
    		HSSFCellStyle headerStyle = workbook.createCellStyle();
    		HSSFFont boldFont = workbook.createFont();
    		boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
    		headerStyle.setFont(boldFont);
    		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    		
    		HSSFCellStyle style = workbook.createCellStyle();
    		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
    		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    		
    		HSSFCellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(df.getFormat("yyyy-mm-dd h:mm:ss.000"));
			dateStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			dateStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			
			HSSFCellStyle messageStyle = workbook.createCellStyle();
			messageStyle.setWrapText(true);
			messageStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
			messageStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
			
    		HSSFCell cell;
    		
    		row = sheet.createRow(0);
    		
    		List<ColumnInfo> columnInfoList = getColumnInfo(request);
    		
    		int columnCount = columnInfoList.size();
    		String[] header = new String[columnCount];
    		int[] cellwidth = new int[columnCount];
    		String[] fieldName = new String[columnCount];
    		String[] columnType = new String[columnCount];
    		String[] mask = new String[columnCount];
    		
    		ColumnInfo columnInfo = null;
    		short width = 265;
    		
    		for( int i = 0 ; i < columnCount ; i++ ){
    			columnInfo = columnInfoList.get(i);
    			header[i] = columnInfo.getColumnName();
    			cellwidth[i] = columnInfo.getColumnWidth();
    			fieldName[i] = columnInfo.getFieldName();
    			columnType[i] = columnInfo.getColumnType();
    			mask[i] = columnInfo.getMask();
    			sheet.setColumnWidth(i, cellwidth[i] * width);
    			cell = row.createCell(i);
    			cell.setCellValue(new HSSFRichTextString(header[i]));
    			cell.setCellStyle(headerStyle);
    		}
    		
    		for( int i = 0 ; i < resultList.size() ; i++ ){
    			row = sheet.createRow(i + 1);
    			AnalysisLog log = (AnalysisLog)resultList.get(i);
    			
	    		for( int j = 0 ; j < columnCount ; j++ ) {
	    			cell = row.createCell(j);
	    			
	    			if(columnType[j].equals("Date")){
	    				Date _date = (Date)PropertyUtils.getProperty(log, fieldName[j]);
	    				if(_date == null) {
	    					cell.setCellValue("");
	    				}else{
		        			cell.setCellStyle(dateStyle);
		        			cell.setCellValue(_date);
	    				}
	        			
	    			}else if("message".equals(fieldName[j])) {
	    				HSSFRichTextString richValue = new HSSFRichTextString((String)PropertyUtils.getProperty(log, fieldName[j]));
	    				cell.setCellStyle(messageStyle);
	    				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	    				cell.setCellValue(richValue);
	    			}else{
	    				cell.setCellValue(BeanUtils.getProperty(log, fieldName[j]));
	    				cell.setCellStyle(style);
	    			}
//	    			cell.setCellValue(BeanUtils.getProperty(log, fieldName[j]));
//	    			cell.setCellStyle(style);
	    		}
    		}
    		workbook.write(fileOut);
    	}catch (Exception e){
    		throw e;
    	} finally {
    		try{
    			if (fileOut != null) {
    				fileOut.flush();
    				fileOut.close();
    			}
    		}catch(IOException ex){
    			throw ex;
    		}
    	}
	}
	
	/**
	 * @param refresh
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=agentList")
	public String agentList(@RequestParam(value="refresh", defaultValue="false") boolean refresh, Model model, HttpServletRequest request) throws Exception {
		List<LogAgent> list = null;
		
		//before
		list = agentService.getLogAgentList();
		
		if(refresh) {
			logger.info("refresh is true");
			agentService.refreshAgent(list);
		}
		// after
		list = agentService.getLogAgentList();
		
		model.addAttribute("list", list);
		return "logmanager/agentList";
	}
	
	/**
	 * @param agentId
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=deleteAgent")
	public String deleteAgent(@RequestParam(value="agentId") String agentId) throws Exception {
		agentService.deleteLogAgent(agentId);
		return "jsonView";
	}
	
	/**
	 * @param agentId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=restartAgent")
	public String restartAgent(@RequestParam(value="agentId") String agentId, Model model) throws Exception {
		try{
			agentService.restartLogAgent(agentId);
		}catch(LogManagerException e){
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param log4jXmlText
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=saveLog4jXmlText")
	public String saveLog4jXmlText(LogApplication param, @RequestParam(value="log4jXmlText") String log4jXmlText, Model model) throws Exception {
		try{
			LogApplication logApp = appService.getLogApplication(param);
			agentService.saveLog4jXml(logApp, log4jXmlText);
		}catch(LogManagerException e){
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	@RequestMapping(params = "method=saveLog4jXml")
	public String saveLog4jXml(LogApplication param, @RequestParam(value="log4jXmlJson") String log4jXmlJson, Model model) throws Exception {
		try{
			LogApplication logApp = appService.getLogApplication(param);
			Gson gson = new Gson();
			LogApplication log4j = gson.fromJson(log4jXmlJson, LogApplication.class);
			//System.out.println(log4j.toString());
			logApp.setRoot(log4j.getRoot());
			logApp.setAppenders(log4j.getAppenders());
			logApp.setLoggers(log4j.getLoggers());
			agentService.saveLog4jXml(logApp);
		}catch(LogManagerException e){
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * get column info for excel export
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private List<ColumnInfo> getColumnInfo(HttpServletRequest request) throws Exception{
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
		    SAXParser saxParser = factory.newSAXParser();
			
			ExcelInfoHandler handler = new ExcelInfoHandler();
			File file = new File(request.getSession().getServletContext().getRealPath("/WEB-INF/classes/excel/logExport.xml"));
			
			if(file.canWrite()){
				saxParser.parse(file, handler);
			}else{
				throw new Exception("cannot find logExport.xml");
			}
			return handler.columnInfoList;
		} catch (Exception e) {
			throw new Exception("fail to get column info", e);
		}
	}
}
