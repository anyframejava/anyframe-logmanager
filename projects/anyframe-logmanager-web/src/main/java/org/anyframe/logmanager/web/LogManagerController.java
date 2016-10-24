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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.anyframe.logmanager.LogManagerConstant;
import org.anyframe.logmanager.common.LogManagerException;
import org.anyframe.logmanager.domain.Account;
import org.anyframe.logmanager.domain.Appender;
import org.anyframe.logmanager.domain.ColumnsInfo;
import org.anyframe.logmanager.domain.Log4j;
import org.anyframe.logmanager.domain.LogAgent;
import org.anyframe.logmanager.domain.LogApplication;
import org.anyframe.logmanager.domain.LogCollection;
import org.anyframe.logmanager.domain.LogCollectionResult;
import org.anyframe.logmanager.domain.LogDataMap;
import org.anyframe.logmanager.domain.LogRepository;
import org.anyframe.logmanager.domain.LogSearchCondition;
import org.anyframe.logmanager.domain.Root;
import org.anyframe.logmanager.service.LogAgentService;
import org.anyframe.logmanager.service.LogApplicationService;
import org.anyframe.logmanager.service.LogCollectionService;
import org.anyframe.logmanager.service.LogRepositoryService;
import org.anyframe.logmanager.service.LogSearchService;
import org.anyframe.logmanager.util.LogDataFormatUtil;
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
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.caucho.hessian.client.HessianRuntimeException;
import com.google.gson.Gson;

/**
 * Log Manager Controller Class
 * 
 * @author Jaehyoung Eum
 */
@Controller("logManagerController")
@RequestMapping("/logManager.do")
public class LogManagerController {

	private static Logger logger = LoggerFactory.getLogger(LogManagerController.class);

	@ModelAttribute("loggingFrameworks")
	public String[] getLoggingFrameworks() throws Exception {
		return LogManagerConstant.LOGGING_FRAMEWORKS;
	}

	@Inject
	@Named("logSearchService")
	private LogSearchService service;

	@Inject
	@Named("logApplicationService")
	private LogApplicationService appService;

	@Inject
	@Named("logAgentService")
	private LogAgentService agentService;
	
	@Inject
	@Named("logCollectionService")
	private LogCollectionService logCollectionService;
	
	@Inject
	@Named("logRepositoryService")
	private LogRepositoryService logRepositoryService;
	
	@Inject
	private MessageSource messageSource;


	/**
	 * Log Application Save method Reference to Array binding... (for Ajax
	 * Request)
	 * 
	 * @param id
	 * @param loggers
	 * @param appenders
	 * @param appName
	 * @param loggingPolicyFilePath
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=saveLogApplication")
	public String saveLogApplication(LogApplication param, Model model, HttpServletRequest request) {
		
		try{
			if ("".equals(param.getId())) {
				param.setId(null);
			}

			appService.saveLogApplication(param);

		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}

	/**
	 * check for log application duplication
	 * 
	 * @param appName
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=checkApplicationExist")
	public String checkApplicationExist(LogApplication param, Model model, HttpServletRequest request) {
		try{
			LogApplication app = appService.checkLogApplicationExist(param);
			if (app == null) {
				model.addAttribute("isExist", false);
			} else {
				model.addAttribute("isExist", true);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}

	/**
	 * Log Application delete method (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=deleteApplication")
	public String deleteApplication(LogApplication param, Model model, HttpServletRequest request) {
		try{
			appService.deleteApplication(param);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}

	/**
	 * Log Application reload method (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=reloadApplication")
	public String reloadApplication(LogApplication param, Model model, HttpServletRequest request) {
		try{
			appService.reloadApplication(param);	
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}


	/**
	 * log4j.xml file load method (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "method=loadLoggingPolicyFile")
	public String loadLoggingPolicyFile(@RequestParam(value = "agentId") String agentId, @RequestParam(value = "loggingPolicyFilePath") String loggingPolicyFilePath,
			@RequestParam(value = "loggingFramework") String loggingFramework, Model model, HttpServletRequest request, HttpSession session) {
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			Map<String, Object> log4j = agentService.getLog4jXml(agentId, loggingPolicyFilePath, loggingFramework);
			if (log4j == null) {
				throw new LogManagerException(loggingPolicyFilePath + messageSource.getMessage("logmanager.runtime.LogManagercontroller.loadLoggingPolicyFile", new String[]{}, currenLocale));
			}

			model.addAttribute("appenders", (List<Appender>)log4j.get("appenders"));
			model.addAttribute("loggers", (List<Logger>)log4j.get("loggers"));
			model.addAttribute("root", (Root)log4j.get("root"));
		} catch (HessianRuntimeException e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", messageSource.getMessage("logmanager.runtime.LogManagercontroller.loadLoggingPolicyFile.agentstatus", new String[]{}, currenLocale));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}

		return "jsonView";
	}
	
	/**
	 * @param agentId
	 * @param loggingPolicyFilePath
	 * @param loggingFramework
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(params = "method=loadLoggingPolicyFileString")
	public String loadLoggingPolicyFileString(@RequestParam(value = "agentId") String agentId, 
			@RequestParam(value = "loggingPolicyFilePath") String loggingPolicyFilePath,
			@RequestParam(value = "loggingFramework") String loggingFramework, 
			Model model, HttpServletRequest request, HttpSession session) {
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		String loggingPolicyXml = null;
		try {
			loggingPolicyXml = agentService.getLoggingPolicyFileInfoString(agentId, loggingPolicyFilePath, loggingFramework);
			model.addAttribute("xmlString", loggingPolicyXml);
		} catch (HessianRuntimeException e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", loggingPolicyFilePath + messageSource.getMessage("logmanager.runtime.LogManagercontroller.loadLoggingPolicyFileString.agentstatus", new String[]{}, currenLocale));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}

		return "jsonView";
	}

	/**
	 * view log application detail info... (for Ajax Request)
	 * 
	 * @param param
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=viewAppDetail")
	public String viewAppDetail(LogApplication param, Model model, HttpSession session) {
		LogApplication app = null;
		try{
			app = appService.getLogApplicationById(param);
			model.addAttribute("app", app);	
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
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
	@RequestMapping(params = "method=getAgentList")
	public String getAgentList(Model model) {
		List<LogAgent> agentList = null;
		try{
			agentList = agentService.getLogAgentList();
			model.addAttribute("agentList", agentList);	
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}

	/**
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=getLogData")
	public String getLogData(@RequestParam("id") String id, @RequestParam("repositoryName") String repositoryName, Model model) {
		try{
			LogDataMap log = service.getLogData(id, repositoryName);
			model.addAttribute("log", log);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}

	/**
	 * log search for text type view (for Ajax Request)
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=analysis")
	public String analysis(LogSearchCondition searchCondition, Model model, HttpServletRequest request) {
		logger.debug("searchCondition=\n{}", searchCondition.toString());
		List<LogDataMap> list = null;
		try{
			list = getLogList(searchCondition, model, request);
			if (list == null)
				list = new ArrayList<LogDataMap>();
			model.addAttribute("searchCondition", searchCondition);
			model.addAttribute("listCount", list.size());
			model.addAttribute("list", list);	
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
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
	public String analysisForm(LogSearchCondition searchCondition, Model model, HttpServletRequest request, HttpSession session) throws Exception {
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		
		Account currentAccount = (Account)session.getAttribute("loginAccount");
		
		List<String> appNameList = new ArrayList<String>();
		appNameList.add(messageSource.getMessage("logmanager.analysis.app.select", new String[]{}, currenLocale));
		appNameList.addAll(service.getActiveLogApplicationNameList());
		List<String> repositoryList = new ArrayList<String>();
		repositoryList.add(messageSource.getMessage("logmanager.analysis.repository.select", new String[]{}, currenLocale));
		repositoryList.addAll(logRepositoryService.getActiveLogRepositoryNameList(currentAccount.getUserType()));

		Date currentDateTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		String currentDate = dateFormat.format(currentDateTime);
		String currentHour = hourFormat.format(currentDateTime);
		String currentMinute = Integer.toString((int) (Math.floor((Integer.parseInt(minuteFormat.format(currentDateTime)) / 10)) * 10));
		if ("0".equals(currentMinute))
			currentMinute = "00";

		if (searchCondition.getFromDate() == null) {
			searchCondition.setFromDate(currentDate);
			searchCondition.setFromHour(currentHour);
			searchCondition.setFromMinute(currentMinute);
		}

		if (searchCondition.getToDate() == null) {
			searchCondition.setToDate(currentDate);
			searchCondition.setToHour(currentHour);
			searchCondition.setToMinute(currentMinute);
		}

		if (searchCondition.getAppName() == null) {
			searchCondition.setMatchedLogOnly(true);
		}

		model.addAttribute("pollingTerm", LogManagerConstant.POLLING_DURATION_SECOND);
		model.addAttribute("appNameList", appNameList);
		model.addAttribute("repositoryList", repositoryList);
		model.addAttribute("levels", LogManagerConstant.LEVELS_SEARCH);
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
		
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		
		logger.debug("searchCondition4gridForm=\n{}", searchCondition.toString());
		
		Account currentAccount = (Account)session.getAttribute("loginAccount");
		
		List<String> appNameList = new ArrayList<String>();
		appNameList.add(messageSource.getMessage("logmanager.analysis.app.select", new String[]{}, currenLocale));
		appNameList.addAll(service.getActiveLogApplicationNameList());
		List<String> repositoryList = new ArrayList<String>();
		repositoryList.add(messageSource.getMessage("logmanager.analysis.repository.select", new String[]{}, currenLocale));
		repositoryList.addAll(logRepositoryService.getActiveLogRepositoryNameList(currentAccount.getUserType()));

		Date currentDateTime = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
		String currentDate = dateFormat.format(currentDateTime);
		String currentHour = hourFormat.format(currentDateTime);
		String currentMinute = Integer.toString((int) (Math.floor((Integer.parseInt(minuteFormat.format(currentDateTime)) / 10)) * 10));

		if ("0".equals(currentMinute))
			currentMinute = "00";

		if (searchCondition.getFromDate() == null) {
			searchCondition.setFromDate(currentDate);
			searchCondition.setFromHour(currentHour);
			searchCondition.setFromMinute(currentMinute);
		}

		if (searchCondition.getToDate() == null) {
			searchCondition.setToDate(currentDate);
			searchCondition.setToHour(currentHour);
			searchCondition.setToMinute(currentMinute);
		}

		if (searchCondition.getAppName() == null) {
			searchCondition.setMatchedLogOnly(true);
		}

		searchCondition.setPageIndex(1);
		searchCondition.setPageSize(10);

		model.addAttribute("appNameList", appNameList);
		model.addAttribute("repositoryList", repositoryList);
		model.addAttribute("levels", LogManagerConstant.LEVELS_SEARCH);
		model.addAttribute("hours", LogManagerConstant.HOURS);
		model.addAttribute("minutes", LogManagerConstant.MINUTES);
		model.addAttribute("searchCondition", searchCondition);
		
		return "logmanager/analysis4gridForm";
	}

	/**
	 * load log data for grid type view (for Ajax Request)
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=analysis4grid")
	public String analysis4grid(LogSearchCondition searchCondition, Model model, HttpServletRequest request) {

		List<LogDataMap> list = null;
		try{
			list = getLogList(searchCondition, model, request);

			Map<String, Object> jsonModel = new HashMap<String, Object>();

			int maxPage = 0;
			maxPage = (int) (searchCondition.getTotalCount() / searchCondition.getPageSize());
			if ((searchCondition.getTotalCount() % searchCondition.getPageSize()) > 0)
				maxPage++;

			jsonModel.put("page", searchCondition.getPageIndex() + "");
			jsonModel.put("total", maxPage + "");
			jsonModel.put("records", searchCondition.getTotalCount() + "");
			jsonModel.put("rows", list);

			model.addAllAttributes(jsonModel);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}

		return "jsonView";
	}

	/**
	 * log data export for text file type
	 * 
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
		sb.append(searchCondition.getAppName().substring(searchCondition.getAppName().lastIndexOf("/") + 1));
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
		searchCondition.setCollection(searchCondition.getRepositoryName());

		String fileName = null;
		String sDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", new Locale("ko_KR"));
		sDate = sdf.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append(searchCondition.getAppName().substring(searchCondition.getAppName().lastIndexOf("/") + 1));
		sb.append("_").append(searchCondition.getCollection()).append("_").append(sDate).append(".xls");
		fileName = sb.toString();

		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-ddHHmm");
		logger.debug("from:{}", searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute());
		logger.debug("to:{}", searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute());
		if (searchCondition.isUseFromDate())
			searchCondition.setFromDateTime(dateTimeFormat.parse(searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute()));
		if (searchCondition.isUseToDate())
			searchCondition.setToDateTime(dateTimeFormat.parse(searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute()));

		List<LogDataMap> resultList = service.searchAnalysisLog(searchCondition);

		response.reset();
		response.setContentType("application/x-msexcel;charset=MS949");
		// response.setContentType("application/octet-stream");
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
			HSSFRow row = null;
			HSSFRow headerRow = null;

			HSSFDataFormat df = workbook.createDataFormat();

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
			HSSFCell headerCell;

			short width = 265;
			
			Iterator<String> j = null;
			String key = null;
			int cellIndex = 0;
			int listSize = 0;
			
			String level = null;
			Date timestamp = null;
			String message = null;
			
			if(resultList != null) {
				listSize = resultList.size();
				for (int i = 0; i < listSize; i++) {
					LogDataMap log = (LogDataMap) resultList.get(i);
					if(i == 0) {
						headerRow = sheet.createRow(i); // level header
						sheet.setColumnWidth(0, 7 * width);
						headerCell = headerRow.createCell(0);
						HSSFRichTextString headerValue = new HSSFRichTextString("level");
						headerCell.setCellValue(headerValue);
						headerCell.setCellStyle(headerStyle);
						
						headerCell = headerRow.createCell(1); // time stamp header
						sheet.setColumnWidth(1, 24 * width);
						headerValue = new HSSFRichTextString("timestamp");
						headerCell.setCellValue(headerValue);
						headerCell.setCellStyle(headerStyle);
						
						headerCell = headerRow.createCell(2); // message header
						sheet.setColumnWidth(2, 70 * width);
						headerValue = new HSSFRichTextString("message");
						headerCell.setCellValue(headerValue);
						headerCell.setCellStyle(headerStyle);
					}

					row = sheet.createRow(i + 1);
					
					// level
					level = (String)log.get("level");
					cell = row.createCell(0);
					cell.setCellStyle(style);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(level);
					
					// timestamp
					timestamp = (Date)log.get("timestamp");
					cell = row.createCell(1);
					cell.setCellStyle(dateStyle);
					cell.setCellValue(timestamp);
					
					// message
					message = (String)log.get("message");
					HSSFRichTextString messageValue = new HSSFRichTextString(message);
					cell = row.createCell(2);
					cell.setCellStyle(messageStyle);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(messageValue);
					
					cellIndex = 3;
					j = log.keySet().iterator();
					while(j.hasNext()) {
						key = j.next();
						if("_id".equals(key) || "message".equals(key) || "timestamp".equals(key) || "level".equals(key)) {
							continue;
						}
						//logger.debug("key=" + key);
						if(i == 0) {
							sheet.setColumnWidth(cellIndex, 20 * width);	
							
							headerCell = headerRow.createCell(cellIndex);
							HSSFRichTextString headerValue = new HSSFRichTextString(key);
							headerCell.setCellValue(headerValue);
							headerCell.setCellStyle(headerStyle);
						}
						cell = row.createCell(cellIndex);
						Object value = log.get(key);
						if(value instanceof Date) {
							cell.setCellStyle(dateStyle);
							cell.setCellValue((Date)value);
						}else{
							cell.setCellStyle(style);
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							cell.setCellValue((String)log.get(key));
						}
						
						cellIndex++;
					}
				}
				workbook.write(fileOut);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fileOut != null) {
					fileOut.flush();
					fileOut.close();
				}
			} catch (IOException ex) {
				logger.warn(ex.getMessage(), ex);
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
	public String agentList(@RequestParam(value = "refresh", defaultValue = "false") boolean refresh, Model model, HttpServletRequest request) throws Exception {
		List<LogAgent> list = null;

		// before
		list = agentService.getLogAgentList();

		if (refresh) {
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
	public String deleteAgent(@RequestParam(value = "agentId") String agentId, Model model) {
		try{
			agentService.deleteLogAgent(agentId);	
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}

	/**
	 * @param agentId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=restartAgent")
	public String restartAgent(@RequestParam(value = "agentId") String agentId, Model model) {
		try {
			agentService.restartLogAgent(agentId);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}

	/**
	 * @param param
	 * @param loggingPolicyFileText
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=saveLoggingPolicyFileText")
	public String saveLoggingPolicyFileText(LogApplication param, @RequestParam(value = "loggingPolicyFileText") String loggingPolicyFileText, Model model, HttpSession session) {
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			LogApplication logApp = appService.getLogApplication(param);
			agentService.saveLoggingPolicyFileText(logApp, loggingPolicyFileText);
		} catch (HessianRuntimeException e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", messageSource.getMessage("logmanager.runtime.LogManagercontroller.saveLoggingPolicyFileText.agentstatus", new String[]{}, currenLocale));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}

	/**
	 * @param param
	 * @param loggingPolicyFileJson
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=saveLog4jXml")
	public String saveLog4jXml(LogApplication param, @RequestParam(value = "loggingPolicyFileJson") String loggingPolicyFileJson, Model model, HttpSession session) {
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		try {
			LogApplication logApp = appService.getLogApplication(param);
			Gson gson = new Gson();
			Log4j log4j = gson.fromJson(loggingPolicyFileJson, Log4j.class);
			// System.out.println(log4j.toString());
			agentService.saveLog4jXml(logApp, log4j.getAppenders(), log4j.getLoggers(), log4j.getRoot());
		} catch (HessianRuntimeException e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", messageSource.getMessage("logmanager.runtime.LogManagercontroller.saveLog4jXml.agentstatus", new String[]{}, currenLocale));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=getLogCollectionResultList")
	public String getLogCollectionResultList(LogCollectionResult param, Model model) {
		
		try{
			List<LogCollectionResult> list = logCollectionService.getLogCollectionResultList(param.getCollectionId());
			model.addAttribute("list", list);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}
	
	@RequestMapping(params = "method=getResultMessage")
	public String getResultMessage(@RequestParam("collectionId") String collectionId, @RequestParam("iterationOrder") int iterationOrder, @RequestParam("messageType") String messageType, Model model) {
		
		try{
			List<String> messages = logCollectionService.getResultMessage(collectionId, iterationOrder, messageType);
			model.addAttribute("messages", messages);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}

	/**
	 * @param param
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=getLogCollectionList")
	public String getLogCollectionList(LogCollection param, Model model) {
		try{
			List<LogCollection> logCollectionList = logCollectionService.getLogCollectionListByAppName(param);
			model.addAttribute("logCollectionList", logCollectionList);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param collectionId
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=getLogCollection")
	public String getLogCollection(@RequestParam(value = "collectionId")String collectionId, Model model) {
		try{
			LogCollection logCollection = logCollectionService.getLogCollection(collectionId);
			model.addAttribute("logCollection", logCollection);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param collectionId
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=deleteLogCollection")
	public String deleteLogCollection(@RequestParam(value = "collectionId")String collectionId, Model model) {
		try{
			logCollectionService.deleteLogCollection(collectionId);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param regularExp
	 * @param sampleData
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "method=checkRegularExpression")
	public String checkRegularExpression(@RequestParam("regularExp") String regularExp, @RequestParam("logDataSample") String logDataSample, 
			@RequestParam("isRegExp") boolean isRegExp, Model model, HttpSession session) {
		Locale currenLocale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		Pattern p = null;
		Matcher m = null;
		List<String> columnNames = new ArrayList<String>();
		List<String> dateFormats = new ArrayList<String>();
		String convertedExpression = null;
		try{
			logger.debug("received expression=" + regularExp);
			
			if(isRegExp){
				p = Pattern.compile(regularExp, Pattern.DOTALL);
				m = p.matcher(logDataSample);
			}else{
				Map<String, Object> convertMap = LogDataFormatUtil.convertLogDataFormat(regularExp);
				
				columnNames = (List<String>)convertMap.get("columnNameList");
				dateFormats = (List<String>)convertMap.get("dateFormatList");
				convertedExpression = (String)convertMap.get("regexp");
				logger.debug("converted expression=" + convertedExpression);
				p = Pattern.compile(convertMap.get("regexp").toString(), Pattern.DOTALL);
				m = p.matcher(logDataSample);
			}
			
			if(m.matches()) {
				int groupCount = m.groupCount();
				List<String> columns = new ArrayList<String>();
				for(int i=1;i<=groupCount;i++){
					columns.add(m.replaceAll("$" + i));
				}
				model.addAttribute("columnNames", columnNames);
				model.addAttribute("dateFormats", dateFormats);
				model.addAttribute("columns", columns);
				model.addAttribute("columnCount", groupCount);
			}else{
				model.addAttribute("isFail", true);
				model.addAttribute("failMessage", messageSource.getMessage("logmanager.runtime.LogManagercontroller.checkRegularExpression.dontpattern1", new String[]{}, currenLocale));
			}	
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", messageSource.getMessage("logmanager.runtime.LogManagercontroller.checkRegularExpression.dontpattern2", new String[]{}, currenLocale));
		}
		
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=saveSetLogCollection")
	public String saveSetLogCollection(LogCollection param, @RequestParam("isRegExp") boolean isRegExp,
			@RequestParam(value = "columnName[]", required = false) String[] columnNames,
			@RequestParam(value = "columnType[]", required = false) String[] columnTypes, 
			@RequestParam(value = "dateFormat[]", required = false) String[] dateFormats, Model model) {
		
		try{
			param.setRegExp(isRegExp);
			logger.debug(param.toString());
			if(columnNames != null) {
				ColumnsInfo[] colsInfo = new ColumnsInfo[columnNames.length];
				for(int i=0;i<columnNames.length;i++) {
					colsInfo[i] = new ColumnsInfo();
					colsInfo[i].setColumnName(columnNames[i]);
					colsInfo[i].setColumnType(columnTypes[i]);
					if("date".equals(columnTypes[i]) || "timestamp".equals(columnTypes[i])) {
						colsInfo[i].setDateFormat(dateFormats[i]);
					}else{
						colsInfo[i].setDateFormat(null);	
					}
				}
				param.setColumnsInfo(colsInfo);
				
				if ("".equals(param.getId())) {
					param.setId(null);
				}else{
					LogCollection previousLogCollection = logCollectionService.getLogCollection(param.getId());
					param.setLastUpdate(previousLogCollection.getLastUpdate()); // last update 유지
				}
				logCollectionService.saveLogCollection(param);
				List<LogCollection> logCollectionsList = logCollectionService.getLogCollectionListByAppName(param);
				model.addAttribute("logCollectionsList", logCollectionsList);
			}else{
				throw new LogManagerException("columnNames is null.");
			}
			
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=getActiveLogRepositoryList")
	public String getActiveLogRepositoryList(LogRepository param, Model model, HttpSession session)	 {
		try{
			Account currentAccount = (Account)session.getAttribute("loginAccount");
			param.setActive(true);
			List<LogRepository> repoList = logRepositoryService.getActiveLogRepositoryList(currentAccount.getUserType());
			model.addAttribute("repositoryList", repoList);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=saveLogRepository")
	public String saveLogRepository(LogRepository param, Model model) {
		
		try{
			logger.debug(param.toString());
			
			if ("".equals(param.getId())) {
				param.setId(null);
			}
			logRepositoryService.saveLogRepository(param);
			model.addAttribute("param", param);
			
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=deleteLogRepository")
	public String deleteLogRepository(LogRepository param, Model model) {
		try{
			logger.debug(param.toString());
			logRepositoryService.removeLogRepository(param);
			model.addAttribute("param", param);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param param
	 * @param model
	 * @return
	 */
	@RequestMapping(params = "method=getLogRepository")
	public String getLogRepository(LogRepository param, Model model) {
		try{
			logger.debug(param.toString());
			LogRepository repository = logRepositoryService.getLogRepository(param);
			model.addAttribute("repository", repository);
			model.addAttribute("param", param);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("isFail", true);
			model.addAttribute("failMessage", e.getLocalizedMessage());
		}
		return "jsonView";
	}
	
	/**
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params = "method=repositoryListForm")
	public String getLogRepositoryListForm(Model model) throws Exception {
		List<LogRepository> list = logRepositoryService.getAllLogRepositoryList();
		model.addAttribute("repositoryList", list);
		return "logmanager/repositoryList";
	}
	
	/**
	 * private method for log search function
	 * 
	 * @param searchCondition
	 * @param model
	 * @param request
	 * @throws Exception
	 */
	private List<LogDataMap> getLogList(LogSearchCondition searchCondition, Model model, HttpServletRequest request) throws Exception {
		List<LogDataMap> list = null;

		if (searchCondition.getAppName() != null) {
			if (searchCondition.isLogTailingMode()) {
				if (!"".equals(searchCondition.getPreviousTimestamp())) {
					searchCondition.setUseToDate(false);
					searchCondition.setUseFromDate(true);
					SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss, SSS");
					Date f = dateTimeFormat.parse(searchCondition.getPreviousTimestamp());
					f.setTime(f.getTime() + ((long) 1));
					searchCondition.setFromDateTime(f);
					logger.debug("from:{}", searchCondition.getFromDateTime());
				} else {
					return new ArrayList<LogDataMap>();
				}
			} else {
				SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-ddHHmm");
				logger.debug("from:{}", searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute());
				logger.debug("to:{}", searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute());
				if (searchCondition.isUseFromDate())
					searchCondition.setFromDateTime(dateTimeFormat.parse(searchCondition.getFromDate() + searchCondition.getFromHour() + searchCondition.getFromMinute()));
				if (searchCondition.isUseToDate())
					searchCondition.setToDateTime(dateTimeFormat.parse(searchCondition.getToDate() + searchCondition.getToHour() + searchCondition.getToMinute()));
			}
			searchCondition.setCollection(searchCondition.getRepositoryName());
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
}
