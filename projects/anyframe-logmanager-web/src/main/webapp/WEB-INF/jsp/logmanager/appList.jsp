<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ page import="org.anyframe.logmanager.LogManagerConstant" %>

<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><spring:message code="logmanager.application.title"/></title>

<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/layout.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/common.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/logmanager.css'/>"/>

<!-- for jquery -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery-1.6.2.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery/validation/jquery.validate.js'/>"></script>

<!-- jquery ui -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery-ui/jquery-ui-1.8.16.custom.min.js'/>"></script>
<link id='uiTheme' href="<c:url value='/jquery/jquery/jquery-ui/themes/cape/jquery-ui-1.8.16.custom.css'/>" rel="stylesheet" type="text/css" />

<!-- ACE Editor -->
<script src="<c:url value='/ace-0.2.0/src/ace-uncompressed.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/ace-0.2.0/src/theme-eclipse.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/ace-0.2.0/src/theme-twilight.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/ace-0.2.0/src/mode-xml.js'/>" type="text/javascript" charset="utf-8"></script>
<script src="<c:url value='/ace-0.2.0/src/mode-javascript.js'/>" type="text/javascript" charset="utf-8"></script>

<script type="text/javascript" src="<c:url value='/logmanager/javascript/InputCalendar.js'/>"></script>
<script type="text/javascript" src="<c:url value='/logmanager/javascript/logmanager.js'/>"></script>
<script type="text/javascript">
<!--
function fncLogout(){
	var msg = '<spring:message code="logmanager.confirm.logout"/>';
    ans = confirm(msg);
    if (ans) {
    	document.location.href='<c:url value="/logout.do"/>';
    } else {
        return false;
    }
}

var editor = null;

var APPENDERs = null;
var LOGGERs = null;
var ROOT = null;

var currentAgentId = null;
var currentAppName = null;

var CONSOLE_APPENDER_CLASS = 'org.apache.log4j.ConsoleAppender';
var DAILY_ROLLING_APPENDER_CLASS = 'org.apache.log4j.DailyRollingFileAppender';
var ROLLING_APPENDER_CLASS = 'org.apache.log4j.RollingFileAppender';
var MONGODB_APPENDER_CLASS = 'org.anyframe.logmanager.log4j.MongoDbAppender';

var DEFAULT_CONVERSIONPATTERN = '[%-5p] %d{yyyy-MM-dd HH:mm:ss} %c %n%m%n';
var PATTERN_LAYOUT_CLASS = 'org.apache.log4j.PatternLayout';
var DEFAULT_DATEFORMAT = 'yyyy-MM-dd HH:mm:ss,SSS';

var LOGCOLLECTION_RESULT_ICON = ['ico_normal.png', 'ico_warning.png', 'ico_error.png']; 
var LOGCOLLECTION_RESULT_MESSAGE = ['Normal', 'Warning', 'Error'];

REQUEST_CONTEXT = '<%=request.getContextPath()%>';

var regExp = /[^a-zA-Z0-9_\/]/;

var LOG_DATA_SAMPLE = ['[INFO ] 2012-07-27 14:12:19,123 org.springframework.web.context.support.StandardServletEnvironment \nMapping column \'GENRE_ID\' to property \'genreId\' of type class java.lang.String', 
                       '2012-07-25 15:44:07,456 (2012-06-01 06:44:07.789317000Z): subject=TEST.APP, message={DATA="JobPrep Facility=T1 Environment=TEST Sender_Node=MES Send_Process=UI Listen_Process=NFW LotID=BRM0001 Job="LotId=BRM0001 MachineId=BRMS01 PortId=1 RecipeId=Test" eventuser=test eventcommnet= data=(A,B,C)"}'];
var EXPRESSION_SAMPLE = ['\\[$level\\] $timestamp{yyyy-MM-dd HH:mm:ss,SSS} $logger \\n$message', 
                     '$timestamp{yyyy-MM-dd HH:mm:ss,SSS} \\($startTerm\\): subject=$subject, message=\\{$message\\}'];
var REGEXP_SAMPLE = ['^\\[(.+?)\\] (\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) (.+?) \\n(.+?)$', 
                     '^(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) \\((.+?)\\): subject=(.+?), message=\\{(.+?)\\}$'];
/**
 * save
 */
function save() {
	var agentId = $('#selectAgentId').val();
	
	loading($('#app-detail-form'));
	$.post('<c:url value="/logManager.do?method=saveLogApplication"/>',
			{'id' : $('#hiddenId').val(),
			'agentId' : agentId,
			'appName' : $.trim($('#txtAppName').val()), 
			'loggingFramework' : $('#selectLoggingFramework').val(),
			'status' : $('#chkStatus').attr('checked') ? 0 : 1,
			'loggingPolicyFilePath' : $.trim($('#txtLoggingPolicyFilePath').val())}, 
		function(data) {
				alert('<spring:message code="logmanager.logapplication.alert.successful"/>');
				loadingClose($('#app-detail-form'));
				$( "#app-detail-form" ).dialog('close');
				self.location.reload(true);
			}).error(function(jqXHR, textStatus, errorThrown) {
		        alert("Error: " + errorThrown);
		        loadingClose($('#app-detail-form'));
			});

	currentAppName = null;
	currentAgentid = null;
}

/**
 * accordionRefresh
 */
function accordionRefresh(acc) {
	
	$('#' + acc).accordion('destroy');
	$('#' + acc).accordion({
		autoHeight : false,
		header: "> div > h3"
	});
}

/**
 * generateRoot
 */
function generateRoot(root) {

	ROOT = root;
	var html = '<div id="root" class="view poplayer margin_top10 root">\n';
	html += '<h3><a href="#"><spring:message code="logmanager.logapplication.root.logger"/></a></h3>\n';
	html += '<div>\n';
	html += '<table id="tbl_Root">\n';
	html += '<colgroup>\n';
	html += '<col style="width:25%;" />\n';
	html += '<col style="width:69%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '</colgroup>\n';
	html += '<tr type="level" parentType="Root" parentId="Root"><th class="topline"><spring:message code="logmanager.logapplication.level"/></th><td class="topline">' + root.level 
		+ '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
	for(var i=0;i<root.appenderRefs.length;i++) {
		var appRef = root.appenderRefs[i];
		html += '<tr type="appender-ref" parentType="Root" parentId="Root"><th><spring:message code="logmanager.logapplication.appenderref"/></th><td>' + appRef 
			+ '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
	}
	html += '</table>\n';
	html += '<div class="btncontainer_right margin_top5">\n';
	html += '<span class="button tablein small">\n';
	html += '<button type="button"  id="btnAppenderRefAdd_Root"><spring:message code="logmanager.logapplication.button.add.appenderref"/></button>\n';
	html += '</span>\n';
	html += '</div>\n';
	html += '</div>\n';
	html += '</div>';
	//alert(html);
	return html;
}

/**
 * generateLogger
 */
function generateLogger(logger, id) {

	LOGGERs.put(id, logger);
	
	var html = '<div id="' + id + '" class="view poplayer margin_top10 logger">\n';
	html += '<h3><a href="#"><spring:message code="logmanager.logapplication.logger"/> : ' + logger.name + '</a></h3>\n';
	html += '<div>\n';
	html += '<table id="tbl_' + id + '">\n';
	html += '<colgroup>\n';
	html += '<col style="width:25%;" />\n';
	html += '<col style="width:69%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '</colgroup>\n';
	html += '<tr type="additivity" parentType="Logger" parentId="' + id + '"><th class="topline"><spring:message code="logmanager.logapplication.additivity"/></th><td class="topline">' + logger.additivity 
		+ '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
	html += '<tr type="level" parentType="Logger" parentId="' + id + '"><th><spring:message code="logmanager.logapplication.level"/></th><td>' + logger.level + '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon">&nbsp;</td></tr>\n';
	for(var i=0;i<logger.appenderRefs.length;i++) {
		var appRef = logger.appenderRefs[i];
		html += '<tr type="appender-ref" parentType="Logger" parentId="' + id + '"><th><spring:message code="logmanager.logapplication.appenderref"/></th><td>' + appRef 
			+ '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
	}
	html += '</table>\n';
	html += '<div class="btncontainer_right margin_top5">\n';
	html += '<span class="button tablein small">\n';
	html += '<button type="button"  id="btnLoggerDelete_' + id + '" type="Logger" parentId="' + id + '"><spring:message code="logmanager.logapplication.button.delete"/></button>\n';
	html += '</span>\n';
	html += '<span class="button tablein small">\n';
	html += '<button type="button"  id="btnAppenderRefAdd_' + id + '" parentId="' + id + '"><spring:message code="logmanager.logapplication.button.add.appenderref"/></button>\n';
	html += '</span>\n';
	html += '</div>\n';
	html += '</div>\n';
	html += '</div>\n';
	//alert(html);
	return html;
}

/**
 * generateAppender
 */
function generateAppender(appender, id) {

	APPENDERs.put(id, appender);
	var html = '<div id="' + id + '" class="view poplayer margin_top10 appender">\n';
	html += '<h3><a href="#">Appender : ' + appender.name + '</a></h3>\n';
	html += '<div>\n';
	html += '<table id="tbl_' + id + '">\n';
	html += '<colgroup>\n';
	html += '<col style="width:25%;" />\n';
	html += '<col style="width:69%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '</colgroup>\n';
	html += '<tr type="class" parentType="Appender" parentId="' + id + '"><th class="topline"><spring:message code="logmanager.logapplication.class"/></th><td class="topline class">' + appender.appenderClass 
		+ '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
	if(appender.appenderRefs) {
		for(var i=0;i<appender.appenderRefs.length;i++) {
			var appRef = appender.appenderRefs[i];
			html += '<tr type="appender-ref" parentType="Appender" parentId="' + id + '"><th><spring:message code="logmanager.logapplication.appenderref"/></th><td>' + appRef 
				+ '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
		}
	}
	
	for(var i=0;i<appender.params.length;i++) {
		var param = appender.params[i];
		html += '<tr type="param" parentType="Appender" parentId="' + id + '"><th>' + param.name + '</th><td class="value">' + param.value 
			+ '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
	}
	html += '</table>\n';

	if(appender.layout && typeof(appender.layout) != "undefined") {
		html += '<table id="tbl_layout_' + id + '">\n';
		html += '<colgroup>\n';
		html += '<col style="width:25%;" />\n';
		html += '<col style="width:69%;" />\n';
		html += '<col style="width:3%;" />\n';
		html += '<col style="width:3%;" />\n';
		html += '</colgroup>\n';
		html += '<tr type="Layout" parentType="Appender" parentId="' + id + '"><td colspan="4" class="layout"><spring:message code="logmanager.logapplication.layout"/></td></tr>\n';
		html += '<tr type="class" parentType="Layout" parentId="' + id + '"><th class="topline"><spring:message code="logmanager.logapplication.class"/></th><td class="topline class">' + appender.layout.layoutClass 
			+ '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
		if(appender.layout.params && typeof(appender.layout.params) != "undefined") {
			for(var i=0;i<appender.layout.params.length;i++) {
				var param = appender.layout.params[i];
				html += '<tr type="param" parentType="Layout" parentId="' + id + '"><th>' + param.name + '</th><td class="value">' + param.value 
				+ '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
			}
		}
		html += '</table>\n';
	}
	html += '<div class="btncontainer_right margin_top5">\n';
	html += '<span class="button tablein small">\n';
	html += '<button type="button"  id="btnAppenderDelete_' + id + '" type="Appender" parentId="' + id + '"><spring:message code="logmanager.logapplication.button.delete"/></button>\n';
	html += '</span>\n';
	html += '<span class="button tablein small">\n';
	html += '<button type="button"  id="btnParamAdd_' + id + '" parentId="' + id + '"><spring:message code="logmanager.logapplication.button.add.param"/></button>\n';
	html += '</span>\n';
	
	if(appender.appenderClass != MONGODB_APPENDER_CLASS) {
		html += '<span class="button tablein small">\n';
		html += '<button type="button"  id="btnLayoutParamAdd_' + id + '" parentId="' + id + '"><spring:message code="logmanager.logapplication.button.add.layout.param"/></button>\n';
		html += '</span>\n';
	}
	
	html += '</div>\n';
	html += '</div>\n';
	html += '</div>';
	//alert(html);
	return html;
}

/**
 *  check result error
 */
function checkError(data) {
	if(data.isFail) {
		//alert('check agent status.');
		alert(data.failMessage);
		loadingClose($('body'));
		return true;
	}else{
		return false;
	}
}

/**
 * editFormClose
 */
function editFormClose() {
	$('#gui-edit-form').dialog('close');
	$('#text-edit-form').editorDialog('close');
}

/**
 * addAppenderRef
 */
function addAppenderRef(type, parentId, value) {
	if(type == 'Root') {
		ROOT.appenderRefs.push(value);
	}else if(type == 'Logger'){
		LOGGERs.get(parentId).appenderRefs.push(value);
	}
	var t = $('#tbl_' + parentId + ' tbody');
	var html = '<tr type="appender-ref" parentType="' + type + '" parentId="' + parentId + '"><th><spring:message code="logmanager.logapplication.appenderref"/></th><td>' + value 
		+ '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>';
	t.append(html);
	$('.removeBtn').click(function(e) {
		removeEventHandler(e);
	});
	$('#tbl_' + parentId + ' td.bottomline').removeClass('bottomline');
	$('#tbl_' + parentId + ' tr:last th').addClass('bottomline');
	$('#tbl_' + parentId + ' tr:last td').addClass('bottomline');
}

/**
 * appenderRefAddForm
 */
function appenderRefAddForm(type, id) {

	var a = APPENDERs.keys();
	var b = null;
	if(type == 'Root') {
		b = ROOT.appenderRefs;
	}else if(type == 'Logger'){
		b = LOGGERs.get(id).appenderRefs;
	}
	if(b.length >= a.length) {
		alert('No more add appender-ref');
		return;
	}
	var html = '<div id="appender-ref-form" title="<spring:message code="logmanager.logapplication.appenderref"/>">';
	html += '<div class="view poplayer margin_top10">';
	html += '<table><colgroup><col style="width:30%"/><col style="width:70%"/></colgroup><tr><th><spring:message code="logmanager.logapplication.appenderref"/></th><td>';
	html += '<select class="select-search">';
	
	for(var i=0;i<a.length;i++) {
		var is = false;
		for(var j=0;j<b.length;j++){
			if(a[i] == b[j]) {
				is = true;
				break;
			}
		}
		if(!is) {
			html += '<option value="' + a[i] + '">' + a[i] + '</option>';
		}
	}
	html+='</select>';
	html += '</td></tr></table></div></div>';
	$('body').append(html);
	$('#appender-ref-form').dialog({
		position:['center', 100],
		autoOpen: false,
		width: 400,
		height: "auto",
		modal: true,
		resizable:true,
		buttons : {
			'Add' : function(){
				addAppenderRef(type, id, $('#appender-ref-form select').val());
				$(this).dialog('close');
			},
			'<spring:message code="logmanager.logapplication.button.cancel"/>' : function(){
				$(this).dialog('close');
			}
		},
		close : function(e, ui) {
			$(this).dialog('destroy');
			$(this).remove();
		}
	});
	$('#appender-ref-form').dialog('open');
}

/**
 * add param event handler
 */
function addParam(type, parentId, name, value) {
	var a = APPENDERs.get(parentId);
	var b = null;
	var t = null;
	
	if(type == 'Appender') {
		a.params.push({'name' : name, 'value' : value});
		t = '#tbl_' + parentId;
	}else if(type == 'Layout'){
		a.layout.params.push({'name' : name, 'value' : value});
		t = '#tbl_layout_' + parentId;
	}
	
	var html = '<tr type="param" parentType="' + type + '" parentId="' + parentId + '"><th>' + name + '</th><td>' + value + 
		'</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td>' + 
		'<td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>';
	$(t).append(html);
	$('.removeBtn').click(function(e) {
		removeEventHandler(e);
	});
	$('.editBtn').click(function(e) {
		editEventHandler(e);
	});
	$(t + ' .bottomline').removeClass('bottomline');
	$(t + ' tr:last > th').addClass('bottomline');
	$(t + ' tr:last > td').addClass('bottomline');
}

/**
 * param add form call
 */
function paramAddForm(type, id) {
	var a = APPENDERs.get(id);
	var b = null;
	if(type == 'Appender') {
		b = a.params;
	}else if(type == 'Layout'){
		b = a.layout.params;
	}
	var html = '<div id="param-edit-form" title="<spring:message code="logmanager.logapplication.param.edit.title"/>">';
	html += '<div class="view poplayer margin_top10">';
	html += '<table><colgroup><col style="width:25%"/><col style="width:75%"/></colgroup>';
	html += '<tr><th><input type="text" id="txtParamName_' + id + '" class="param-name"/></th>';
	html += '<td><input type="text" id="txtParamValue_' + id + '" class="param-value"/>';
	html += '</td></tr></table></div></div>';
	$('body').append(html);
	$('#param-edit-form').dialog({
		position:['center', 100],
		autoOpen: false,
		width: 600,
		height: "auto",
		modal: true,
		resizable:true,
		buttons : {
			'<spring:message code="logmanager.logapplication.button.add"/>' : function(){
				var name = $('#param-edit-form input:text:first').val();
				var value = $('#param-edit-form input:text:last').val();
				var a = null;
				var isDup = false;
				if(type == 'Appender') {
					a = APPENDERs.get(id).params;
				}else if(type == 'Layout') {
					a = APPENDERs.get(id).layout.params;
				}
				for(var i=0;i<a.length;i++) {
					if(a[i].name == name) {
						isDup = true;
						break;
					}
				}
				
				if(name == '') {
					alert('<spring:message code="logmanager.logapplication.alert.param.name"/>');
					$('#param-edit-form input:text:first').focus();
					return;
				}
				
				if(value == '') {
					alert('<spring:message code="logmanager.logapplication.alert.param.value"/>');
					$('#param-edit-form input:text:last').focus();
					return;
				}
				
				if(isDup) {
					alert(name + ' is aleady exist.');
					return;
				}
				addParam(type, id, name, value);
				$(this).dialog('close');
			},
			'<spring:message code="logmanager.logapplication.button.cancel"/>' : function(){
				$(this).dialog('close');
			}
		},
		close : function(e, ui) {
			$(this).dialog('destroy');
			$(this).remove();
		}
	});
	$('#param-edit-form').dialog('open');
}

/**
 * edit icon click event handler
 */
function editEventHandler(e) {
	var self = $(e.target);
	var parentTR = self.parent().parent();
	var type = parentTR.attr('type');
	var parentType = parentTR.attr('parentType');
	var parentId = parentTR.attr('parentId');
	var value = parentTR.children('td:first').text();
	var a = null;
	var html = '';
	if(type == 'level') {
		html += '<select id="selectLogLevel' + parentType + '_' + parentId + '" style="width:100%;">';
		html += '<option value="FATAL">FATAL</option>';
		html += '<option value="ERROR">ERROR</option>';
		html += '<option value="WARN">WARN</option>';
		html += '<option value="INFO">INFO</option>';
		html += '<option value="DEBUG">DEBUG</option>';
		html += '<option value="OFF">OFF</option>';
		html += '</select>';
		parentTR.children('td:first').html(html);
		$('#selectLogLevel' + parentType + '_' + parentId).val(value);
	}else if(type == 'class') {
		html += '<input type="text" id="txtClass' + parentType + '_' + parentId + '" style="width:100%;"/>';
		parentTR.children('td:first').html(html);
		$('#txtClass' + parentType + '_' + parentId).val(value);
	}else if(type == 'param') {
		var prefix = parentTR.children('th:first').text();
		html += '<input type="text" id="txtParam' + parentType + '_' + parentId + '_' + prefix + '" style="width:100%;"/>';
		parentTR.children('td:first').html(html);
		$('#txtParam' + parentType + '_' + parentId + '_' + prefix).val(value);
	}else if(type == 'additivity') {
		html += '<select id="selectAdditivity' + parentType + '_' + parentId + '" style="width:100%;">';
		html += '<option value="false">false</option>';
		html += '<option value="true">true</option>';
		html += '</select>';
		parentTR.children('td:first').html(html);
		$('#selectAdditivity' + parentType + '_' + parentId).val(value);
		
	}
	self.attr('src', '<c:url value="/logmanager/images/btn_save_i.gif"/>');
	self.attr('alt', '<spring:message code="logmanager.logapplication.button.save"/>');
	self.attr('title', '<spring:message code="logmanager.logapplication.button.save"/>');
	self.unbind('click');
	self.click(function(e) {
		saveEventHandler(e);
	});
}

/**
 * save icon click event handler
 */
function saveEventHandler(e){
	var self = $(e.target);
	var parent = self.parent();
	var type = parent.parent().attr('type');
	var parentType = parent.parent().attr('parentType');
	var parentId = parent.parent().attr('parentId');

	var v = null;
	
	if(type == 'level') {
		v = $('#selectLogLevel' + parentType + '_' + parentId).val();
	}else if(type == 'class') {
		v = $('#txtClass' + parentType + '_' + parentId).val();
	}else if(type == 'param') {
		var prefix = parent.parent().children('th:first').text();
		v = $('#txtParam' + parentType + '_' + parentId + '_' + prefix).val();
	}else if(type == 'additivity') {
		v = $('#selectAdditivity' + parentType + '_' + parentId).val();
	}

	if(parentType == 'Root') {
		if(type == 'level') ROOT.level = v;
	}else if(parentType == 'Logger') {
		if(type == 'level') {
			LOGGERs.get(parentId).level = v;
		}else if(type == 'additivity') {
			LOGGERs.get(parentId).additivity = v;
		}
	}else if(parentType == 'Appender') {
		if(type == 'class') {
			APPENDERs.get(parentId).appenderClass = v;
		}else if(type == 'param') {
			var prefix = parent.parent().children('th:first').text();
			var params = APPENDERs.get(parentId).params;
			for(var i=0;i<params.length;i++) {
				if(params[i].name == prefix) params[i].value = v;
			}
		}
	}else if(parentType == 'Layout') {
		if(type == 'class') {
			APPENDERs.get(parentId).layout.layoutClass = v;
		}else if(type == 'param') {
			var prefix = parent.parent().children('th:first').text();
			var params = APPENDERs.get(parentId).layout.params;
			for(var i=0;i<params.length;i++) {
				if(params[i].name == prefix) params[i].value = v;
			}
		}
	}
	
	parent.parent().children('td:first').html(v);
	
	self.attr('src', '<c:url value="/logmanager/images/btn_edit_i.gif"/>');
	self.attr('alt', 'Edit');
	self.attr('title', 'Edit');
	self.unbind('click');
	self.click(function(e) {
		editEventHandler(e);
	});
}

/**
 * remove event handler
 */
function removeEventHandler(e) {

	if(!$(e.target).attr('type')) {
		var parentTR = $(e.target).parent().parent();
		var type = parentTR.attr('type');
		var parentType = parentTR.attr('parentType');
		var parentId = parentTR.attr('parentId');
		var value = parentTR.children('td:first').text();
		var a = null;

		if(type == 'appender-ref') {
			if(parentType == 'Logger') {
				a = LOGGERs.get(parentId).appenderRefs;
			}else if(parentType == 'Root') {
				a = ROOT.appenderRefs;
			}
			for(var i=0;i<a.length;i++) {
				if(a[i] == value) {
					a.splice(i, 1);
					break;
				}
			}
		}else if(type == 'param') {
			if(parentType == 'Appender') {
				a = APPENDERs.get(parentId).params;
			}else if(parentType == 'Layout') {
				a = APPENDERs.get(parentId).layout.params;
				parentId = 'layout_' + parentId;
			}
			for(var i=0;i<a.length;i++) {
				if(a[i].value == value) {
					a.splice(i, 1);
					break;
				}
			}
		}
		parentTR.remove();

		$('#tbl_' + parentId + ' .bottomline').removeClass('bottomline');
		$('#tbl_' + parentId + ' tr:last > th').addClass('bottomline');
		$('#tbl_' + parentId + ' tr:last > td').addClass('bottomline');
	}else {
		var type = $(e.target).attr('type');
		var parentId = $(e.target).attr('parentId');
		
		if(type == 'Logger') {
			LOGGERs.remove(parentId);
		}else if(type == 'Appender') {
			APPENDERs.remove(parentId);
		}
		$('#' + parentId).remove();
		
		accordionRefresh('accordionLoggers');
		accordionRefresh('accordion');
		
	}
}

/**
 * add appedner
 */
function addAppender() {
	var name = $('#txtAppenderName').val();
	var appenderClass = null;
	var layoutClass = null;
	var id = name.replace(/\./g, '_');
	var conversionPattern = $('#txtConversionPattern').val();

	if($('#txtAppenderClass').val() == '' || $('#selectAppenderClass').val() == 'MI') {
		appenderClass = $('#selectAppenderClass').val();
	}else{
		appenderClass = $('#txtAppenderClass').val(); 
	}

	if($('#txtLayoutClass').val() == '' || $('#selectLayoutClass').val() == 'MI') {
		layoutClass = $('#selectLayoutClass').val();
	}else{
		layoutClass = $('#txtLayoutClass').val(); 
	}

	var mongoParams = $('#tblAppenderAdd tr.mongodb input:text');
	var dailyParams = $('#tblAppenderAdd tr.daily input:text');
	var rollingParams = $('#tblAppenderAdd tr.rolling input:text');
	var logmanagerParams = $('#tblLayoutAdd tr.logmanager input:text');

	var appenderParams = new Array();
	for(var i=0;i<mongoParams.length;i++) {
		appenderParams.push({'name' : mongoParams[i].id, 'value' : mongoParams[i].value});
	}
	for(var i=0;i<dailyParams.length;i++) {
		appenderParams.push({'name' : dailyParams[i].id, 'value' : dailyParams[i].value});
	}
	for(var i=0;i<rollingParams.length;i++) {
		appenderParams.push({'name' : rollingParams[i].id, 'value' : rollingParams[i].value});
	}

	var layoutParams = new Array();
	layoutParams.push({'name' : 'ConversionPattern', 'value' : conversionPattern});
	for(var i=0;i<logmanagerParams.length;i++) {
		layoutParams.push({'name' : logmanagerParams[i].id, 'value' : logmanagerParams[i].value});
	}
	
	var appender = {
		'name' : name,
		'appenderClass' : appenderClass,
		'params' : appenderParams,
		'layout' : {
			'layoutClass' : layoutClass,
			'params' : layoutParams
		} 
	};
	
	if(appender.appenderClass == MONGODB_APPENDER_CLASS) {
		appender.layout = null;
	}
	
	var html = '';
	
	if(!APPENDERs.get(id)) { 
		APPENDERs.put(id, appender);

		html = generateAppender(appender, id);
		//$('#accordion').append(html);
		$(html).insertBefore('#accordion > div.logger');

		/** 
		 * btnParamAdd_ParamXX click handler
		 **/
		$('#btnParamAdd_' + id).click(function(e) {
			paramAddForm('Appender', $(e.target).attr('parentId'));
		});
		
		if(appender.appenderClass != MONGODB_APPENDER_CLASS) {
			/** 
			 * btnLayoutParamAdd_ParamXX click handler
			 **/
			$('#btnLayoutParamAdd_' + id).click(function(e) {
				paramAddForm('Layout', $(e.target).attr('parentId'));
			});
		}
		
		/**
		 * btnAppenderDelete_XXX click event handler
		 **/
		$('#btnAppenderDelete_' + id).click(function(e) {
			removeEventHandler(e);
		});

		/**
		 * remove icon click handler
		 **/
		$('.removeBtn').click(function(e){
			removeEventHandler(e);
		});
	
		/**
		 * edit icon click handler
		 **/
		$('.editBtn').click(function(e) {
			editEventHandler(e);
		});
		
		accordionRefresh('accordion');
		
		return true;
	}else{
		alert('<spring:message code="logmanager.logapplication.alert.logger.already.exist"/>');
		return false;
	}
}

/**
 * add logger event handler
 */
function addLogger() {
	var name = $('#txtLoggerName').val();
	var additivity = $('#selectAdditivity').val();
	var logLevel = $('#selectLoggerLogLevel').val();
	var id = name.replace(/\./g, '_');

	var html = '';
	
	if(!LOGGERs.get(id)) {
		var logger = {
				'name' : name,
				'additivity' : additivity,
				'level' : logLevel,
				'appenderRefs' : new Array()
			};

		LOGGERs.put(id, logger);

		html = generateLogger(logger, id);
		$('#accordionLoggers').append(html);

		/** 
		 * btnAppenderRefAdd_AppenderXX click handler
		 **/
		$('#btnAppenderRefAdd_' + id).click(function(e) {
			appenderRefAddForm('Logger', $(e.target).attr('parentId'));
		});

		/**
		 * btnLoggerDelete_XXX click event handler
		 **/
		$('#btnLoggerDelete_' + id).click(function(e) {
			removeEventHandler(e);
		});

		/**
		 * remove icon click handler
		 **/
		$('.removeBtn').click(function(e){
			removeEventHandler(e);
		});
	
		/**
		 * edit icon click handler
		 **/
		$('.editBtn').click(function(e) {
			editEventHandler(e);
		});
		
		accordionRefresh('accordionLoggers');
		accordionRefresh('accordion');
		
		return true;
	}else{
		alert('Logger aleady exist.');
		return false;
	}
}

/**
 * get agent list
 */
function getAgentList(defalutAgentId, callback) {
	
	var url = '<c:url value="/logManager.do?method=getAgentList"/>';
	$.get(url, '', function(data) {
		var agentList = data.agentList;
		var html = '';
		if(agentList.length) {
			var selected = '';
			for(var i=0;i<agentList.length;i++) {
				if(defalutAgentId == agentList[i].agentId) {
					selected = 'selected';
				}else{
					selected = '';
				}
				html += '<option value="' + agentList[i].agentId + '" ' + selected + '>' + agentList[i].agentId + ' - ' + agentList[i].statusMessage + '</option>\n';
			}
		}else{
			html += '<option value="' + defalutAgentId + '" selected>' + defalutAgentId + '</option>\n';
		}
		$('#selectAgentId').html(html);

		if(typeof callback == 'function'){
			callback();
		}
	}).error(function(jqXHR, textStatus, errorThrown) {
        alert("Error: " + errorThrown);
        loadingClose($('body'));
	});
}

/**
 * refresh log collection info
 */
function refreshLogCollectionInfo(appName, agentId, callback) {
	var url = '<c:url value="/logManager.do?method=getLogCollectionList"/>&appName=' + appName + '&agentId=' + agentId;
	$.get(url, function(data){
		var html = '';
		
		$('#tblSetLogCollection tbody:not(:first-child)').remove();
		
		for(var i=0;i<data.logCollectionList.length;i++) {
			var displayTimestamp = null;
			var tempDate = null;
			if(data.logCollectionList[i].lastUpdate != -1) {
				tempDate = new Date(data.logCollectionList[i].lastUpdate);
				displayTimestamp = tempDate.format('yyyy-MM-dd HH:mm:ss,SSS');
			}else{
				displayTimestamp = -1;
			}
					
			html += '<tr collectionId="' + data.logCollectionList[i].id + '">';
			html += '<td>' + data.logCollectionList[i].pathOfLog + '</td>';
			html += '<td>' + data.logCollectionList[i].logFileName + '</td>';
			html += '<td>' + displayTimestamp + '</td>';
			html += '<td>' + data.logCollectionList[i].collectionTerm + ' ' + data.logCollectionList[i].collectionTermUnit + '</td>';
			html += '<td>' + data.logCollectionList[i].setLogCollectionActive + '</td>';
			html += '<td class="td-btn align_center"><img src="<c:url value="/logmanager/images/' + LOGCOLLECTION_RESULT_ICON[data.logCollectionList[i].result.status] + '"/>" class="result-status-button" alt="' 
				+ LOGCOLLECTION_RESULT_MESSAGE[data.logCollectionList[i].result.status] + '" title="' + LOGCOLLECTION_RESULT_MESSAGE[data.logCollectionList[i].result.status] + '"></td>';
			html += '<td class="td-btn align_center"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" class="logcollection-delete-button" agentId="' + agentId + '" appName="' + appName + '"></td>';
			html += '</tr>';	
		}
		
		$('#tblSetLogCollection').append(html);
		
		/**
		 * #tblSetLogCollection row select event handler
		 **/
		$('#tblSetLogCollection td:not(.td-btn)').click(function(e){
			$('#tblSetLogCollection td.selected').removeClass('selected');
			$(this).parent('tr').children('td').addClass('selected');
			var collectionId = $(this).parent('tr').attr('collectionId');
			var url = '<c:url value="/logManager.do?method=getLogCollection"/>&collectionId=' + collectionId;
			$.get(url, function(data){
				displayLogCollectionInfo(data.logCollection);
			}).error(function(jqXHR, textStatus, errorThrown) {
		        alert("Error: " + errorThrown);
		        loadingClose($('#set-logcollection-form'));
			});
		});
		
		/**
		 * log collection delete event handler
		 **/
		$('.logcollection-delete-button').click(function(e){
			var el = $(e.target);
			var collectionId = el.parent().parent('tr').attr('collectionId');
			var url = '<c:url value="/logManager.do?method=deleteLogCollection"/>&collectionId=' + collectionId;
			if(confirm('Do you want to delete?')) {
				loading($('#set-logcollection-form'));
				$.get(url, function(data){
					alert('delete is successful');
					loadingClose($('#set-logcollection-form'));
					refreshLogCollectionInfo(appName, agentId, function(){
						resetLogCollectionInfo();
					});
				}).error(function(jqXHR, textStatus, errorThrown) {
			        alert("Error: " + errorThrown);
			        loadingClose($('#set-logcollection-form'));
				});
			}
		});
		
		/**
		 * result-status-button click event handler
		 */
		$('.result-status-button').click(function(e){
			var el = $(e.target);
			var collectionId = el.parent().parent('tr').attr('collectionId');
			var elTr = el.parent().parent('tr');
			var url = '<c:url value="/logManager.do?method=getLogCollectionResultList"/>&collectionId=' + collectionId;
			
			if(elTr.next().attr('class') == 'collection-result-tr') {
				elTr.next().remove();
			}else{
				loading($('#set-logcollection-form'));
				$.get(url, function(data){
					loadingClose($('#set-logcollection-form'));
					var table = '<table>';
					table += '<tr>';
					table += '<th><spring:message code="logmanager.logapplication.iteration.order"/></th><th><spring:message code="logmanager.logapplication.status"/></th><th><spring:message code="logmanager.logapplication.parsed.count"/></th><th><spring:message code="logmanager.logapplication.inserted.count"/></th><th><spring:message code="logmanager.logapplication.timestamp"/></th><th>&nbsp;</th>';
					table += '</tr>';
					for(var i=0;i<data.list.length;i++) {
						table += '<tr>';
						var tempDate = new Date(data.list[i].timestamp);
						var displayTimestamp = tempDate.format('yyyy-MM-dd HH:mm:ss,SSS');
						table += '<td>' + data.list[i].iterationOrder + '</td><td>' 
							+ '<img src="<c:url value="/logmanager/images/' + LOGCOLLECTION_RESULT_ICON[data.list[i].status] + '"/>" class="result-status-icon">&nbsp;' + LOGCOLLECTION_RESULT_MESSAGE[data.list[i].status] + '</td><td>' 
							+ data.list[i].parseCount + '</td><td>' 
							+ data.list[i].insertCount + '</td><td>' 
							+ displayTimestamp + '</td><td collectionId="' + data.list[i].collectionId + '" iterationOrder="' + data.list[i].iterationOrder + '">'
							+ '<img src="<c:url value="/logmanager/images/ico_warning.png"/>" title="' + LOGCOLLECTION_RESULT_MESSAGE[1] + '" class="result-status-icon">&nbsp;:&nbsp;<a class="warning" href="#">'
							+ data.list[i].message.warning.length + '</a>,&nbsp;<img src="<c:url value="/logmanager/images/ico_error.png"/>" title="' + LOGCOLLECTION_RESULT_MESSAGE[2]
							+ '" class="result-status-icon">&nbsp;:&nbsp;<a class="error" href="#">' + data.list[i].message.error.length + '</a></td>';
						table += '</tr>';
					}
					table += '</table>';
					
					elTr.after('<tr class="collection-result-tr"><td colspan="7">' + table + '</td></tr>');
					
					/**
					 * warning class click event handler
					 */
					$('a.warning, a.error').click(function(e){
						var el = $(e.target);
						var className = el.attr('class');
						var collectionId = el.parent('td').attr('collectionId');
						var iterationOrder = el.parent('td').attr('iterationOrder');
						var url = '<c:url value="/logManager.do?method=getResultMessage"/>';
						$.get(url, {
							'collectionId' : collectionId,
							'iterationOrder' : iterationOrder,
							'messageType' : className
							}, function(data){
								var m = '<ul>';
								for(var i=0;i<data.messages.length;i++) {
									m += '<li><pre>' + i + ' : ' + data.messages[i] + '</pre></li>';
								}
								m += '</ul>';
								$('#tdMessage').html(m);
								$('#result-message-form').dialog('open');
						}).error(function(jqXHR, textStatus, errorThrown) {
							alert("Error: " + errorThrown);
						    loadingClose($('#set-logcollection-form'));
						});
					});
					
				}).error(function(jqXHR, textStatus, errorThrown) {
					alert("Error: " + errorThrown);
				    loadingClose($('#set-logcollection-form'));
				});
			}
		});
		callback();	
	});
}

/**
 * reset log collection form
 **/
function resetLogCollectionInfo() {
	$('#txtPathOfLog').val('');
	$('#txtLogFileName').val('');
	$('#txtCollectionTerm').val(1);
	$('input[name=collectionTermUnit]').filter('input[value=h]').attr('checked', 'checked');
	$('#taRegularExp').val('');
	$('#taLogDataSample').val('');
	$('#ulColumnsInfo li').remove();
	$('#chkSetLogCollectionActive').attr('checked', 'checked');
	$('#isValidate').val(false);
	$('#chkRegularExp').removeAttr('checked');	
	var url = '<c:url value="/logManager.do?method=getActiveLogRepositoryList"/>';
	
	$.get(url, function(data){
		var repositoryList = data.repositoryList;
		var html = '';
		
		if(repositoryList.length) {
			if(repositoryList.length == 0) {
				alert('<spring:message code="logmanager.logapplication.alert.register.first"/>');
				$('#set-logcollection-form').dialog('close');
				return;
			}
			for(var i=0;i<repositoryList.length;i++) {
				html += '<option value="' + repositoryList[i].repositoryName + '">' + repositoryList[i].repositoryName + '</option>\n';
			}
		}else{
			alert('<spring:message code="logmanager.logapplication.alert.register.first"/>');
			$('#set-logcollection-form').dialog('close');
			return;
		}
		$('#selectRepositoryName').html(html);
		$('#selectRepositoryName').removeAttr('disabled');
	}).error(function(jqXHR, textStatus, errorThrown) {
        alert("Error: " + errorThrown);
        loadingClose($('#set-logcollection-form'));
	});
}

/**
 * display log collection info
 **/
function displayLogCollectionInfo(logCollection) {
	$('#txtPathOfLog').val(logCollection.pathOfLog);
	$('#txtLogFileName').val(logCollection.logFileName);
	$('#txtCollectionTerm').val(logCollection.collectionTerm);
	$('input[name=collectionTermUnit]').filter('input[value=' + logCollection.collectionTermUnit + ']').attr('checked', 'checked');
	$('#taRegularExp').val(logCollection.regularExp);
	$('#taLogDataSample').val(logCollection.logDataSample);
	
	if(parseBoolean(logCollection.regExp)) {
		$('#chkRegularExp').attr('checked', 'checked');	
	}else{
		$('#chkRegularExp').removeAttr('checked');
	}
	
	if(parseBoolean(logCollection.setLogCollectionActive)) {
		$('#chkSetLogCollectionActive').attr('checked', 'checked');	
	}else{
		$('#chkSetLogCollectionActive').removeAttr('checked');
	}
	
	displayColumnsInfo(logCollection.columnsInfo, null, null);
	
	$('#selectRepositoryName').val(logCollection.repositoryName);
	$('#selectRepositoryName').attr('disabled', 'disabled');
	
	$('#isValidate').val(true);	
}

/**
 * display columns info
 **/
function displayColumnsInfo(columns, columnNames, dateFormats) {
	var columnCount = 0;
	var columnName = null;
	var columnType = null;
	var dateFormat = null;
	var columnData = null;
	var html = '';
	if(columns.length) {
		$('#ulColumnsInfo li').remove();
		for(var i=0;i<columns.length;i++) {
			if(typeof(columns[i]) == 'object') {
				columnName = columns[i].columnName;
				columnType = columns[i].columnType;
				if(columns[i].dateFormat) {
					dateFormat = columns[i].dateFormat;	
				}else{
					dateFormat = DEFAULT_DATEFORMAT;
				}
				columnData = '';
			}else if(typeof(columns[i]) == 'string') {
				colmunName = 'Col' + i;
				columnType = 'string';
				dateFormat = DEFAULT_DATEFORMAT;
				columnData = columns[i];
			}
			html = '<li>' + (i+1) + '&nbsp;';
			html += '<input type="text" id="txtCol' + i + '" name="columnName" index="' + i + '" value="' + columnName + '" style="width:100px;"/>&nbsp;';
			html += '<select id="selectColumnType' + i + '" index="' + i + '" name="columnType" style="width:100px;">';
			html += '<option value="string">String</option>';
			html += '<option value="numeric">Numeric</option>';
			html += '<option value="date">Date</option>';
			html += '<option value="level">level</option>';
			html += '<option value="timestamp">timestamp</option>';
			html += '<option value="message">message</option>';
			html += '<option value="logger">logger</option>';
			html += '<option value="className">className</option>';
			html += '<option value="methodName">methodName</option>';
			html += '<option value="fileName">fileName</option>';
			html += '<option value="lineNumber">lineNumber</option>';
			html += '<option value="thread">thread</option>';
			html += '<option value="ignore">--ignore--</option></select>&nbsp;';
			html += '<input type="text" id="txtDateFormat'+ i + '" name="dateFormat" value="' + dateFormat + '" style="width:150px;display:none;"/>';
			html += '<pre>' + columnData.trim().escapeHTML() + '</pre>';
			html += '</li>';
			
			$('#ulColumnsInfo').append(html);
			
			$('#selectColumnType' + i).change(function(e){
				
				if($(e.target).val() == 'string' || $(e.target).val() == 'numeric') {
					$('#txtCol' + $(e.target).attr('index')).val('Col' + $(e.target).attr('index'));
					$('#txtCol'+ $(e.target).attr('index')).removeAttr('readonly');
					$('#txtDateFormat'+ $(e.target).attr('index')).hide();
				}else if($(e.target).val() == 'date') {
					$('#txtCol' + $(e.target).attr('index')).val('Col' + $(e.target).attr('index'));
					$('#txtCol'+ $(e.target).attr('index')).removeAttr('readonly');
					$('#txtDateFormat'+ $(e.target).attr('index')).show();
				}else if($(e.target).val() == 'timestamp') {
					$('#txtCol' + $(e.target).attr('index')).val($(e.target).val());
					$('#txtCol'+ $(e.target).attr('index')).attr('readonly', 'true');
					$('#txtDateFormat'+ $(e.target).attr('index')).show();
				}else{
					$('#txtCol' + $(e.target).attr('index')).val($(e.target).val());
					$('#txtCol'+ $(e.target).attr('index')).attr('readonly', 'true');
					$('#txtDateFormat'+ $(e.target).attr('index')).hide();
				}
			});
			if(typeof(columns[i]) == 'string') {
				if(columnData.trim() == '') {
					$('#selectColumnType' + i).val('ignore');
					$('#selectColumnType' + i).trigger('change');
				}else {
					$('#selectColumnType' + i).val(columnType);
					$('#selectColumnType' + i).trigger('change');		
				}
			}else {
				$('#selectColumnType' + i).val(columnType);
				$('#selectColumnType' + i).trigger('change');
				$('#txtCol' + i).val(columnName);
			}
			
		}
	}
	
	if(columnNames != null) {
		if(columnNames.length) {
			for(var i=0;i<columnNames.length;i++) {
				if(columnNames[i] == 'timestamp' 
						|| columnNames[i] == 'level' 
						|| columnNames[i] == 'message' 
						|| columnNames[i] == 'logger' 
						|| columnNames[i] == 'className'
						|| columnNames[i] == 'methodName'
						|| columnNames[i] == 'fileName'
						|| columnNames[i] == 'lineNumber'
						|| columnNames[i] == 'thread') {
					$('#selectColumnType' + i).val(columnNames[i]);
					$('#selectColumnType' + i).trigger('change');
				}else{
					if(dateFormats[i] != '') {
						$('#selectColumnType' + i).val('date');
						$('#selectColumnType' + i).trigger('change');
					}
					$('#txtCol' + i).val(columnNames[i]);
				}
				$('#txtDateFormat' + i).val(dateFormats[i]);
			}
		}
	}
}

$(document).ready(function() {

	/**
	 * 'Add' button click event handler
	 * open log application edit form
	 **/
	$('#btnAdd').click(function(e) {
		$('#app-detail-form').dialog({
			position:['center', 100]
		});
		$('#txtAppName').removeAttr('disabled');
		$('#txtAppName').val('');
		$('#txtLoggingPolicyFilePath').val('');
		$('#tblAppender tbody tr').remove();
		$('#hiddenId').val('');

		$('#txtLoggingPolicyFilePath').removeAttr('disabled');
		$('#btnLoggingPolicyFileLoad').removeAttr('disabled');
		$('#selectLoggingFramework').removeAttr('disabled');
		$('#spanOffline').hide();
		
		getAgentList('', function() {
			$('#selectAgentId').removeAttr('disabled');
			$('#app-detail-form').dialog('open');
		});
	});

	/**
	 * log application 'Delete' button click event handler
	 * log application delete
	 **/
	$('.delete-button').click(function(e) {
		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		if(confirm('Do you want really to delete \"' + appName + '\"?')) {
			loading($('body'));
			$.get('<c:url value="/logManager.do?method=deleteApplication"/>&agentId=' + agentId + '&appName=' + appName, function(data) {
				self.location.reload(true);
			}).error(function(jqXHR, textStatus, errorThrown) {
		        alert("Error: " + errorThrown);
		        loadingClose($('body'));
			});
		}
	});

	/**
	 * log application 'Active/Inactive/Realod' button click event handler 
	 * log application active/inactive/reload
	 **/
	$('.reload-button').click(function(e) {
		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		var status = $(e.target).attr('status');
		loading($('body'));
		$.get('<c:url value="/logManager.do?method=reloadApplication"/>&agentId=' + agentId + '&status=' + status + '&appName=' + appName, function(data) {
			loadingClose($('body'));
			self.location.reload(true);
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('body'));
		});
	});

	/**
	 * editor select popup display
	 **/
	$('.editor-select').click(function(e) {
		var pos = $(e.target).offset();
		$('#editor-select-form').dialog({position:[pos.left-200, pos.top]});

		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		var loggingFramework = $(e.target).attr('loggingFramework');
		var loggingPolicyFilePath = $(e.target).attr('loggingPolicyFilePath');

		$('#gui').attr('appName', appName);
		$('#gui').attr('agentId', agentId);
		$('#gui').attr('loggingFramework', loggingFramework);
		$('#gui').attr('loggingPolicyFilePath', loggingPolicyFilePath);

		$('#text').attr('appName', appName);
		$('#text').attr('agentId', agentId);
		$('#text').attr('loggingFramework', loggingFramework);
		$('#text').attr('loggingPolicyFilePath', loggingPolicyFilePath);
		if(loggingFramework == '<%=LogManagerConstant.LOGGING_FRAMEWORKS[0]%>') {
			$("#editor-select-form").dialog('open');	
		}else{
			$('#text').trigger('click');
		}
	});
	
	/**
	 * set log collection popup display
	 **/
	$('.set-logcollection').click(function(e){
		var pos = $(e.target).offset();
		$('#set-logcollection-form').dialog({position:['center', pos.top]});

		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		//var loggingFramework = $(e.target).attr('loggingFramework');
		//var loggingPolicyFilePath = $(e.target).attr('loggingPolicyFilePath');

		$('#tdAppName').html(appName);
		$('#tdAgentId').html(agentId);
		
		refreshLogCollectionInfo(appName, agentId, function(){
			resetLogCollectionInfo();
			$('#set-logcollection-form').dialog('open');
		});
	});

	/**
	 * 'Text' button click event handler
	 * open text type policy(logging policy file) edit form
	 **/
	$('#text').click(function(e){
		$("#editor-select-form").dialog('close');
		
		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		var loggingFramework = $(e.target).attr('loggingFramework');
		var loggingPolicyFilePath = $(e.target).attr('loggingPolicyFilePath');

		currentAppName = appName;
		currentAgentId = agentId;

		loading($('body'));
		$.get('<c:url value="/logManager.do?method=loadLoggingPolicyFileString"/>&agentId=' + agentId + '&loggingPolicyFilePath=' + loggingPolicyFilePath + '&loggingFramework=' + loggingFramework, function(data){
			if(checkError(data)) {
				loadingClose($('body'));
				return;
			}
			loadingClose($('body'));
			var xmlString = data.xmlString;
			var html = '';

			$('#text-edit-form').editorDialog('open');
			
			$('#xmlEditor textarea').remove();
			
			if($.browser.msie) { // IE case
				$('#xmlEditor').append('<textarea id="taXmlEditor"></textarea>');
				
				editor = {
					getSession : function(){
						return {
							setValue : function(xml) {
								$('#taXmlEditor').val(xml);
							},
							getValue : function() {
								return $('#taXmlEditor').val();
							}
						};
					}	
				};
				editor.getSession().setValue(xmlString);
				
				$("#text-edit-form").resizable({
					'stop': function(event, ui) {
						var w = ui.size.width;
						var h = ui.size.height;
						// text area resize
						$('#xmlEditor').width(w-4);
						$('#xmlEditor').height(h-75);
					} 
				});
			}else{
				editor = ace.edit("xmlEditor");
			    editor.setTheme("ace/theme/eclipse");
			    
			    var XmlMode = require("ace/mode/xml").Mode;
			    editor.getSession().setMode(new XmlMode());
				editor.getSession().setValue(xmlString);
				editor.getSession().setUseWrapMode(false);
				
				$("#text-edit-form").resizable({
					'stop': function(event, ui) {
						var w = ui.size.width;
						var h = ui.size.height;
						$('#xmlEditor').width(w-4);
						$('#xmlEditor').height(h-75);
						editor.resize();
					} 
				});
			}
			 
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('body'));
		});
	});

	/**
	 * 'GUI' button click event handler
	 * open gui type policy(logging policy file) edit form
	 */
	$('#gui').click(function(e){

		$("#editor-select-form").dialog('close');
		
		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		var loggingFramework = $(e.target).attr('loggingFramework');
		var loggingPolicyFilePath = $(e.target).attr('loggingPolicyFilePath');

		currentAppName = appName;
		currentAgentId = agentId;

		loading($('body'));
		$.get('<c:url value="/logManager.do?method=loadLoggingPolicyFile"/>&agentId=' + agentId + '&loggingPolicyFilePath=' + loggingPolicyFilePath + '&loggingFramework=' + loggingFramework, function(data){

			if(checkError(data)) {
				loadingClose($('body'));
				return;
			}
			loadingClose($('body'));
			var loggers = data.loggers;
			var appenders = data.appenders;
			var root = data.root;
			var html = '';

			ROOT = root;
			APPENDERs = new Map();
			LOGGERs = new Map();
						
			$('#accordion').append(generateRoot(root));
			$('#tbl_Root tr:last > th').addClass('bottomline');
			$('#tbl_Root tr:last > td').addClass('bottomline');
			/** 
			 * btnAppenderRefAdd_Root click handler
			 **/
			$('#btnAppenderRefAdd_Root').click(function(e) {
				appenderRefAddForm('Root', 'Root');
			});
			
			for(var i=0;i<appenders.length;i++) {
				var id = (appenders[i].name).replace(/\./g, '_');
				$('#accordion').append(generateAppender(appenders[i], id));
				$('#tbl_' + id + ' tr:last > th').addClass('bottomline');
				$('#tbl_' + id + ' tr:last > td').addClass('bottomline');
				$('#tbl_layout_' + id + ' tr:last > th').addClass('bottomline');
				$('#tbl_layout_' + id + ' tr:last > td').addClass('bottomline');
				
				/** 
				 * btnAppenderRefAdd_ParamXX click handler
				 **/
				$('#btnParamAdd_' + id).click(function(e) {
					paramAddForm('Appender', $(e.target).attr('parentId'));
				});
				
				/** 
				 * btnLayoutParamAdd_ParamXX click handler
				 **/
				$('#btnLayoutParamAdd_' + id).click(function(e) {
					paramAddForm('Layout', $(e.target).attr('parentId'));
				});

				/**
				 * btnAppenderDelete_XXX click event handler
				 **/
				$('#btnAppenderDelete_' + id).click(function(e) {
					removeEventHandler(e);
				});
			}

			var html = '<div class="view poplayer margin_top10 logger">\n';
			html += '<h3><a href="#"><spring:message code="logmanager.logapplication.logger"/></a></h3>\n';
			html += '<div>\n';
			html += '<div id="accordionLoggers"></div>\n';
			html += '</div>\n';
			html += '</div>\n';
			$('#accordion').append(html);
			
			for(var i=0;i<loggers.length;i++) {
				var id = (loggers[i].name).replace(/\./g, '_');
				$('#accordionLoggers').append(generateLogger(loggers[i], id));
				$('#tbl_' + id + ' tr:last > th').addClass('bottomline');
				$('#tbl_' + id + ' tr:last > td').addClass('bottomline');
				/** 
				 * btnAppenderRefAdd_AppenderXX click handler
				 **/
				$('#btnAppenderRefAdd_' + id).click(function(e) {
					appenderRefAddForm('Logger', $(e.target).attr('parentId'));
				});

				/**
				 * btnLoggerDelete_XXX click event handler
				 **/
				$('#btnLoggerDelete_' + id).click(function(e) {
					removeEventHandler(e);
				});
			}

			/**
			 * remove icon click handler
			 **/
			$('.removeBtn').click(function(e){
				removeEventHandler(e);
			});

			/**
			 * edit icon click handler
			 **/
			$('.editBtn').click(function(e) {
				editEventHandler(e);
			});
			
			$('#gui-edit-form').dialog('open');
			$('#gui-edit-form').dialog({position:['center', 100]});
			
			accordionRefresh('accordionLoggers');
			accordionRefresh('accordion');
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('body'));
		});
	});

	/**
	 * list item click event handler
	 * log application info load and open log application edit form
	 */
	$('.app-name').click(function(e) {
		loading($('body'));
		$.get('<c:url value="/logManager.do?method=viewAppDetail"/>&id=' + $(this).attr('id'), function(data){

			if(checkError(data)){
				loadingClose($('body'));
				return;
			}
			loadingClose($('body'));
			
			var app = data.app;

			currentAppName = app.appName;
			currentAgentid = app.agentId;
			
			$('#txtAppName').val(app.appName);
			$('#txtAppName').attr('disabled', 'disabled');
			$('#txtLoggingPolicyFilePath').val(app.loggingPolicyFilePath);
			$('#selectLoggingFramework').val(app.loggingFramework);
			$('#selectLoggingFramework').attr('disabled', 'disabled');
			if(app.status == '<%=LogManagerConstant.APP_STATUS_ACTIVE%>') {
				$('#chkStatus').attr('checked', 'checked');
			}else{
				$('#chkStatus').removeAttr('checked');
			}
			$('#hiddenId').val(app.id);

			getAgentList('', function() {
				$('#selectAgentId').val(app.agentId);
				$('#selectAgentId').attr('disabled', 'true');
				$('#app-detail-form').dialog('open');
				$('#app-detail-form').dialog({position:['center', 100]});
			});
			
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('body'));
		});
	});

	/**
	 * 'Save' button click event handler
	 * db save and logging policy file apply
	 */
	$('#btnSave').click(function(e) {

		if($('#txtAppName').val() == '') {
			alert('<spring:message code="logmanager.logapplication.alert.checklogappname"/>');
			$('#txtAppName').select();
			return;
		}
		
		if(regExp.test($('#txtAppName').val())) {
			alert('<spring:message code="logmanager.logapplication.alert.checkalphachar"/>');
			$('#txtRepositoryName').select();
			return;
		}
		
		if($('#selectAgentId option').length == 0) {
			alert('<spring:message code="logmanager.logapplication.alert.notfindagent"/>');
			return;
		}

		if($('#txtLoggingPolicyFilePath').val() == '') {
			alert('<spring:message code="logmanager.logapplication.alert.checklogpolicypath"/>');
			$('#txtLoggingPolicyFilePath').select();
			return;
		}
		
		if($('#hiddenId').val() == '') {
			loading($('#app-detail-form'));
			$.get('<c:url value="/logManager.do?method=checkApplicationExist"/>&agentId=' + $('#selectAgentId').val() + '&appName=' + $.trim($('#txtAppName').val()), function(data){
				if(data.isExist) {
					alert('<spring:message code="logmanager.dup.logapplication.name"/>');
					loadingClose($('#app-detail-form'));
					$('#txtAppName').select();
					return false;
				}else{
					loadingClose($('#app-detail-form'));
					save();
				}
			}).error(function(jqXHR, textStatus, errorThrown) {
		        alert("Error: " + errorThrown);
		        loadingClose($('#app-detail-form'));
		    });
		}else{
			save();
		}
	});


	/**
	 * ui tool edit result save handler
	 **/
	$('#btnGuiSave').click(function(e){
		var url = '<c:url value="/logManager.do?method=saveLog4jXml"/>';
		var log4j = {
			'appenders' : APPENDERs.values(),
			'loggers' : LOGGERs.values(),
			'root' : ROOT	
		}; 

		var loggingPolicyFileJson = JSON.stringify(log4j);
		loading($('#gui-edit-form'));
		$.post(url, {'agentId' : currentAgentId, 'appName' : currentAppName, 'loggingPolicyFileJson' : loggingPolicyFileJson}, function(data){
			if(checkError(data)) {
				loadingClose($('#gui-edit-form'));
				return;
			}else{
				alert('<spring:message code="logmanager.logapplication.alert.successful"/>');
				loadingClose($('#gui-edit-form'));
				editFormClose();
			}
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('#gui-edit-form'));
		});
	});

	/**
	 * edit form close handler
	 **/
	$('#btnGuiCancel').click(function(e){
		editFormClose();
	});
	
	/**
	 * text edit result save handler
	 **/
	$('#btnTextSave').click(function(e){
		var url = '<c:url value="/logManager.do?method=saveLoggingPolicyFileText"/>';
		loading($('#text-edit-form'));
		var loggingPolicyFileText = editor.getSession().getValue();
		$.post(url, { 'agentId' : currentAgentId, 'appName': currentAppName, 'loggingPolicyFileText': loggingPolicyFileText}, function(data){
			if(checkError(data)) {
				loadingClose($('#text-edit-form'));
				return;
			}else{
				alert('<spring:message code="logmanager.logapplication.alert.successful"/>');
				loadingClose($('#text-edit-form'));
				editFormClose();
			}
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('#text-edit-form'));
		});
	});

	/**
	 * edit form close handler
	 **/
	$('#btnTextCancel').click(function(e){
		editFormClose();
	});

	/**
	 * app edit form close handler
	 **/
	$('#btnCancel').click(function(e) {
		$("#app-detail-form").dialog('close');
	});

	/**
	 * appender create form click handler
	 **/
	$('#btnAppenderAdd').click(function(e) {
		$('#txtAppenderName').val('');
		$('#txtAppenderClass').val('');
		$('#txtLayoutClass').val('');
		$('#txtConversionPattern').val(DEFAULT_CONVERSIONPATTERN);
		$('#txtAppenderName').select();
		$('#selectAppenderClass').val(CONSOLE_APPENDER_CLASS).trigger('change');
		$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
		$('#tblLayoutAdd').show();
		$('#appender-create-form').dialog('open');
	});

	/**
	 * select appender class change event handler
	 **/
	$('#selectAppenderClass').change(function(e){
		var html = '';
		var self = $(e.target);

		if(self.val() == 'MI') {
			self.css('display', 'none');
			$('#txtAppenderClass').css('display', 'inline');
			$('#appenderClassCancel').css('display', 'inline');

			$('#tblAppenderAdd tr.mongodb').remove();
			
			$('#tblAppenderAdd tr:last > th').addClass('bottomline');
			$('#tblAppenderAdd tr:last > td').addClass('bottomline');

			$('#selectLayoutClass > option').remove();
			html = '<option value="org.apache.log4j.PatternLayout"><spring:message code="logmanager.logapplication.patternlayout"/></option>';
			html += '<option value="MI"><spring:message code="logmanager.logapplication.manually.input"/></option>';
			$('#selectLayoutClass').append(html);
			$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
			$('#tblLayoutAdd').show();
		}else if(self.val() == CONSOLE_APPENDER_CLASS) {

			$('#selectLayoutClass').css('display', 'inline');
			$('#txtLayoutClass').css('display', 'none');
			$('#layoutClassCancel').css('display', 'none');
			
			$('#tblAppenderAdd tr.mongodb').remove();
			
			$('#tblAppenderAdd tr:last > th').addClass('bottomline');
			$('#tblAppenderAdd tr:last > td').addClass('bottomline');

			$('#selectLayoutClass > option').remove();
			html = '<option value="org.apache.log4j.PatternLayout"><spring:message code="logmanager.logapplication.patternlayout"/></option>';
			html += '<option value="MI"><spring:message code="logmanager.logapplication.manually.input"/></option>';
			$('#selectLayoutClass').append(html);
			$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
			$('#tblLayoutAdd').show();
		}else if(self.val() == DAILY_ROLLING_APPENDER_CLASS) {

			$('#selectLayoutClass').css('display', 'inline');
			$('#txtLayoutClass').css('display', 'none');
			$('#layoutClassCancel').css('display', 'none');
			
			$('#tblAppenderAdd tr.mongodb').remove();
			$('#tblAppenderAdd tr.rolling').remove();
			$('#tblAppenderAdd tr.daily').remove();

			html = '<tr class="daily"><th>File</th><td><input type="text" id="File" value="/usr/app/logs/anyframe.log" style="width:100%;"/></td></tr>\n';			
			html += '<tr class="daily"><th>DatePattern</th><td><input type="text" id="DatePattern" value="\'.\'yyyy-MM-dd" style="width:100%;"/></td></tr>\n';

			$('#tblAppenderAdd th.bottomline').removeClass('bottomline');
			$('#tblAppenderAdd td.bottomline').removeClass('bottomline');
			$('#tblAppenderAdd').append(html);
			$('#tblAppenderAdd tr:last > th').addClass('bottomline');
			$('#tblAppenderAdd tr:last > td').addClass('bottomline');

			$('#selectLayoutClass > option').remove();
			html = '<option value="org.apache.log4j.PatternLayout"><spring:message code="logmanager.logapplication.patternlayout"/></option>';
			html += '<option value="MI"><spring:message code="logmanager.logapplication.manually.input"/></option>';
			$('#selectLayoutClass').append(html);
			$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
			$('#tblLayoutAdd').show();
		}else if(self.val() == ROLLING_APPENDER_CLASS) {
			$('#selectLayoutClass').css('display', 'inline');
			$('#txtLayoutClass').css('display', 'none');
			$('#layoutClassCancel').css('display', 'none');
			
			$('#tblAppenderAdd tr.mongodb').remove();
			$('#tblAppenderAdd tr.rolling').remove();
			$('#tblAppenderAdd tr.daily').remove();

			html = '<tr class="rolling"><th>File</th><td><input type="text" id="File" value="/usr/app/logs/anyframe.log" style="width:100%;"/></td></tr>\n';			
			html += '<tr class="rolling"><th>MaxFileSize</th><td><input type="text" id="MaxFileSize" value="10240KB" style="width:100%;"/></td></tr>\n';
			html += '<tr class="rolling"><th>MaxBackupIndex</th><td><input type="text" id="MaxBackupIndex" value="10" style="width:100%;"/></td></tr>\n';

			$('#tblAppenderAdd th.bottomline').removeClass('bottomline');
			$('#tblAppenderAdd td.bottomline').removeClass('bottomline');
			$('#tblAppenderAdd').append(html);
			$('#tblAppenderAdd tr:last > th').addClass('bottomline');
			$('#tblAppenderAdd tr:last > td').addClass('bottomline');

			$('#selectLayoutClass > option').remove();
			html = '<option value="org.apache.log4j.PatternLayout"><spring:message code="logmanager.logapplication.patternlayout"/></option>';
			html += '<option value="MI"><spring:message code="logmanager.logapplication.manually.input"/></option>';
			$('#selectLayoutClass').append(html);
			$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
			$('#tblLayoutAdd').show();
		}else if(self.val() == MONGODB_APPENDER_CLASS) {
			$('#tblLayoutAdd').hide();
			
			$('#tblAppenderAdd tr.mongodb').remove();
			$('#tblAppenderAdd tr.rolling').remove();
			$('#tblAppenderAdd tr.daily').remove();
			
			html = '<tr class="mongodb"><th>hostname</th><td><input type="text" id="hostname" value="localhost" style="width:100%;"/></td></tr>\n';			
			html += '<tr class="mongodb"><th>port</th><td><input type="text" id="port" value="27017" style="width:100%;"/></td></tr>\n';
			html += '<tr class="mongodb"><th>userName</th><td><input type="text" id="userName" value="" style="width:100%;"/></td></tr>\n';
			html += '<tr class="mongodb"><th>password</th><td><input type="text" id="password" value="" style="width:100%;"/></td></tr>\n';
			html += '<tr class="mongodb"><th>databaseName</th><td><input type="text" id="databaseName" value="logs" style="width:100%;"/></td></tr>\n';
			html += '<tr class="mongodb"><th>collectionName</th><td><input type="text" id="collectionName" value="log4jlogs" style="width:100%;"/></td></tr>\n';

			$('#tblAppenderAdd th.bottomline').removeClass('bottomline');
			$('#tblAppenderAdd td.bottomline').removeClass('bottomline');
			$('#tblAppenderAdd').append(html);
			$('#tblAppenderAdd tr:last > th').addClass('bottomline');
			$('#tblAppenderAdd tr:last > td').addClass('bottomline');
		}

	});

	/**
	 * appender class cancel button event handler
	 **/
	$('#appenderClassCancel').click(function(e) {
		$('#selectAppenderClass').val(CONSOLE_APPENDER_CLASS).trigger('change');
		$('#selectAppenderClass').css('display', 'inline');
		$('#txtAppenderClass').css('display', 'none');
		$(e.target).css('display', 'none');
		$('#txtAppenderClass').val('');
	});

	/**
	 * laycout class change event handler
	 **/
	$('#selectLayoutClass').change(function(e){
		var html = '';
		var self = $(e.target);
		if(self.val() == 'MI') {
			self.css('display', 'none');
			$('#txtLayoutClass').css('display', 'inline');
			$('#layoutClassCancel').css('display', 'inline');

			$('#txtConversionPattern').val(DEFAULT_CONVERSIONPATTERN);
			$('#tblLayoutAdd tr.logmanager').remove();
			
			$('#tblLayoutAdd tr:last > th').addClass('bottomline');
			$('#tblLayoutAdd tr:last > td').addClass('bottomline');
			
		}else if(self.val() == PATTERN_LAYOUT_CLASS) {
			
			$('#txtConversionPattern').val(DEFAULT_CONVERSIONPATTERN);
			$('#tblLayoutAdd tr.logmanager').remove();
			
			$('#tblLayoutAdd tr:last > th').addClass('bottomline');
			$('#tblLayoutAdd tr:last > td').addClass('bottomline');
			
		}
	});

	/**
	 * layout class cancen click event handler
	 **/
	$('#layoutClassCancel').click(function(e) {
		$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
		$('#selectLayoutClass').css('display', 'inline');
		$('#txtLayoutClass').css('display', 'none');
		$(e.target).css('display', 'none');
		$('#txtLayoutClass').val('');
	});

	/**
	 * logger create form click handler
	 **/
	$('#btnLoggerAdd').click(function(e) {
		$('#selectAdditivity').val(false);
		$('#selectLoggerLogLevel').val('INFO');
		$('#txtLoggerName').val('');
		$('#txtLoggerName').select();
		$('#logger-create-form').dialog('open');
	});
	
	/**
	 * if the values has changed, need to re-validate.
	 **/
	$('#taRegularExp').keyup(function(e){
		$('#isValidate').val(false);
	});
	
	/**
	 * if the values has changed, need to re-validate.
	 **/
	$('#taLogDataSample').keyup(function(e){
		$('#isValidate').val(false);
	});
	
	/**
	 * regular expression validate click event handler
	 **/
	$('#btnRegularExpValidate').click(function(e) {
		var regularExp = $('#taRegularExp').val().trim();
		var logDataSample = $('#taLogDataSample').val().trim();
		var isRegExp = $('#chkRegularExp').attr('checked');
		var url = '<c:url value="/logManager.do?method=checkRegularExpression"/>';
		
		if(regularExp == '' || logDataSample == '') {
			alert('<spring:message code="logmanager.logapplication.alert.invalid"/>');
			return;
		}
		
		loading($('#set-logcollection-form'));
		$.post(url, {
			'regularExp' : regularExp,
			'logDataSample' : logDataSample,
			'isRegExp' : isRegExp == 'checked' ? true : false		
		}, function(data){
			if(checkError(data)) {
				loadingClose($('#set-logcollection-form'));
				return;
			}
			loadingClose($('#set-logcollection-form'));
			$('#isValidate').val(true);
			var html = '';
			if(data.columnCount > 0) {
				displayColumnsInfo(data.columns, data.columnNames, data.dateFormats);
			}
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('#set-logcollection-form'));
		});
	});
	
	/**
	 * set ui reset click event handler
	 **/
	$('#btnSetLogCollectionNew').click(function(e){
		$('#tblSetLogCollection td.selected').removeClass('selected');
		resetLogCollectionInfo();
	});
	
	/**
	 * set log collection save event handler
	 **/
	$('#btnSetLogCollectionSave').click(function(e){
		
		if($('#txtPathOfLog').val() == '') {
			alert('<spring:message code="logmanager.logapplication.alert.checklogpath"/>');
			$('#txtPathOfLog').select();
			return;
		}
		
		if($('#txtLogFileName').val() == '') {
			alert('<spring:message code="logmanager.logapplication.alert.checklogfilename"/>');
			$('#txtLogFileName').select();
			return;
		}
		
		var num_regx=/^[0-9]*$/;
		
		if(!num_regx.test($('#txtCollectionTerm').val()) ) {
			alert('<spring:message code="logmanager.logapplication.alert.than0"/>');
			$('#txtCollectionTerm').select();
	    	return;
	    }
		
		if($('#taRegularExp').val() == '') {
			alert('<spring:message code="logmanager.logapplication.alert.checkexpcondition"/>');
			$('#taRegularExp').select();
			return;
		}
		
		if($('#isValidate').val() == 'false') {
			alert('<spring:message code="logmanager.logapplication.alert.perform.validate"/>');
			return;
		}
		
		var columnNameArray = new Array();
		var columnTypeArray = new Array();
		var dateFormatArray = new Array();
		
		$('input[name=columnName]').each(function(e){
			columnNameArray.push($(this).val());
		});
		$('select[name=columnType]').each(function(e){
			columnTypeArray.push($(this).val());
		});
		$('input[name=dateFormat]').each(function(e){
			dateFormatArray.push($(this).val());
		});
		
		var id = $('td.selected').parent('tr').attr('collectionId');
		if(typeof(id) == 'undefined') {
			id = '';
		}
		loading($('#set-logcollection-form'));
		var url = '<c:url value="/logManager.do?method=saveSetLogCollection"/>';
		$.post(url, {
			'id' : id,
			'appName' : $('#tdAppName').text(),
			'agentId' : $('#tdAgentId').text(),
			'pathOfLog' : $('#txtPathOfLog').val(),
			'logFileName' : $('#txtLogFileName').val(),
			'collectionTerm' : $('#txtCollectionTerm').val(),
			'collectionTermUnit' : $(':input:radio[name=collectionTermUnit]]:checked').val(),
			'regularExp' : $('#taRegularExp').val(),
			'logDataSample' : $('#taLogDataSample').val(),
			'setLogCollectionActive' : $('#chkSetLogCollectionActive').attr('checked') == 'checked' ? true : false,
			'columnName' : columnNameArray,
			'columnType' : columnTypeArray,
			'dateFormat' : dateFormatArray,
			'isRegExp' : $('#chkRegularExp').attr('checked') == 'checked' ? true : false,
			'repositoryName' : $('#selectRepositoryName').val()
		}, function(data){
			if(checkError(data)) {
				loadingClose($('#set-logcollection-form'));
				return;
			}
			alert('<spring:message code="logmanager.logapplication.alert.successful"/>');
			loadingClose($('#set-logcollection-form'));
			
			refreshLogCollectionInfo($('#tdAppName').text(), $('#tdAgentId').text(), function() {
				resetLogCollectionInfo();
			});
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + errorThrown);
	        loadingClose($('#set-logcollection-form'));
		});
	});
	
	/**
	 * set log collection cancel event handler
	 **/
	$('#btnSetLogCollectionClose').click(function(e){
		$('#set-logcollection-form').dialog('close');
	});
	
	/**
	 * btnSample1 click event handler
	 **/
	$('#btnSample1').click(function(e){
		if($('#chkRegularExp').attr('checked') == 'checked') {
			$('#taRegularExp').val(REGEXP_SAMPLE[0]);
			$('#taLogDataSample').val(LOG_DATA_SAMPLE[0]);			
		}else{
			$('#taRegularExp').val(EXPRESSION_SAMPLE[0]);
			$('#taLogDataSample').val(LOG_DATA_SAMPLE[0]);
		}
		$('#isValidate').val(false);
	});

	/**
	 * btnSample2 click event handler
	 **/
	$('#btnSample2').click(function(e){
		if($('#chkRegularExp').attr('checked') == 'checked') {
			$('#taRegularExp').val(REGEXP_SAMPLE[1]);
			$('#taLogDataSample').val(LOG_DATA_SAMPLE[1]);			
		}else{
			$('#taRegularExp').val(EXPRESSION_SAMPLE[1]);
			$('#taLogDataSample').val(LOG_DATA_SAMPLE[1]);
		}
		$('#chkRegularExp').removeAttr('checked');
		$('#isValidate').val(false);
	});
	// ################################ dialog form definition ########################################

	/**
	 * log application edit form dialog define
	 **/
	$( "#app-detail-form" ).dialog({
		autoOpen: false,
		width: 400,
		height:"auto",
		modal: true,
		resizable:true,
		close : function(e, ui) {
			currentAppName = null;
			currentAgentid = null;
		}
	});

	/**
	 * gui type edit form dialog define
	 **/
	$( "#gui-edit-form" ).dialog({
		autoOpen: false,
		width: 800,
		height: "auto",
		modal: true,
		resizable:true,
		close : function(e, ui) {
			$('#accordionLoggers').accordion('destroy');
			$('#accordion').accordion('destroy');
			$('#accordion > div').remove();
			currentAppName = null;
			currentAgentid = null;
		}
	});

	/**
	 * text type edit form dialog define
	 **/
	$( "#text-edit-form" ).editorDialog({
		width: 800,
		height: 575,
		position:['center', 100],
		open : function() {
			loading($('body'), {image : false, caption : ''});
		},
		close : function() {
			loadingClose($('body'));
			$('#xmlEditor').html('');
			currentAppName = null;
			currentAgentid = null;
		}
	 }).resizable({
        minWidth: 600,
        minHeight: 575
    }).draggable({
    	handle : '#text-edit-title' 
    });
	
	/**
	 * log4j appender create form dialog define
	 **/
	$("#appender-create-form").dialog({
		autoOpen: false,
		width: 600,
		height:"auto",
		modal: true,
		resizable:true,
		buttons : {
			'<spring:message code="logmanager.logapplication.button.add"/>' : function(){

				if(addAppender()) {
					$(this).dialog('close');
				}else{
					alert('<spring:message code="logmanager.logapplication.alert.appender.already.exist"/>');
					return;
				}
				$(this).dialog('close');
			},
			'<spring:message code="logmanager.logapplication.button.cancel"/>' : function(){
				$(this).dialog('close');
			}
		},
		close : function(e, ui) {
		}
	});

	/**
	 * log4j logger create form dialog define
	 **/
	$("#logger-create-form").dialog({
		autoOpen: false,
		width: 430,
		height:"auto",
		modal: true,
		resizable:true,
		buttons : {
			'<spring:message code="logmanager.logapplication.button.add"/>' : function(){
				if(addLogger()) {
					$(this).dialog('close');
				}else{
					alert('<spring:message code="logmanager.logapplication.alert.logger.already.exist"/>');
					return;
				}
			},
			'<spring:message code="logmanager.logapplication.button.cancel"/>' : function(){
				$(this).dialog('close');
			}
		},
		close : function(e, ui) {
		}
	});

	/**
	 * editor select form dialog define
	 **/
	$("#editor-select-form").dialog({
		autoOpen: false,
		width: 430,
		height:100,
		modal: true,
		resizable:false
	});
	
	/**
	 * set log collection form dialog define
	 **/
	$('#set-logcollection-form').dialog({
		autoOpen: false,
		width: 600,
		height:"auto",
		modal: true,
		resizable:true,
		close : function(e, ui) {
		}
	});
	
	/**
	 * result-message form dialog define
	 **/
	$("#result-message-form").dialog({
		autoOpen: false,
		width: 430,
		height:"auto",
		modal: true,
		resizable:true
	});

	// ########################################################################

});

//-->
</script>
</head>
<body class="logging_leftmenu">
<!--[if IE 6]><div id="wrapper" class="ie6fix"><![endif]-->
<!--[if IE 7]><div id="wrapper" class="ie7fix"><![endif]-->
<!--[if gt IE 7]><div id="wrapper" class="iefix"><![endif]-->
<!--[if !IE]><div id="wrapper"><![endif]-->
<div id="wrapper">
	<div class="skipnavigation">
		<a href="#contents">Jump up to the contents</a>
	</div>
	<hr />
	   
	<div id="header">
		<h1><img src="<c:url value="/logmanager/images/toplogo.jpg"/>" alt="Log Manager"/></h1>
        <div class="topnavi">
        	<ul>
            	<c:if test="${not empty sessionScope.loginAccount.userId}">
            		<li class="end"><a href="#" onclick="javascript:fncLogout();">
            		<c:out value="${sessionScope.loginAccount.userId}"/>&nbsp;<spring:message code="logmanager.logout.message"/></a></li>
            	</c:if>
            </ul>
		</div>
	</div>
	<hr />
	
	<div id="container_bgbox">
		<div id="container">
        	<div id="leftspace">
            	<ul>
                    <li><a href="<c:url value="/logManager.do?method=analysis4gridForm"/>">Log Analysis</a></li>
				<c:if test="${sessionScope.loginAccount.userType=='Administrator'}">
                    <li><a href="<c:url value="/logManager.do?method=agentList"/>">Log Agent Management</a></li>
                    <li><a href="<c:url value="/logManager.do?method=repositoryListForm"/>">Log Repository Management</a></li>
                    <li><a href="<c:url value="/logManager.do?method=appList"/>">Log Application Management</a></li>
                    <li><a href="<c:url value="/account.do?method=view"/>">Account Management</a></li>
                </c:if>
                </ul>
			</div>
			<hr/>
			
			<div id="contents">
				<h2><spring:message code="logmanager.logapplication.title"/></h2>
                <div id="innercontents">
                	<div class="list">
                        <table summary="">
                            
                            <caption><spring:message code="logmanager.logapplication.appname"/>, <spring:message code="logmanager.logapplication.logging.policy.file.path"/>, <spring:message code="logmanager.logapplication.status"/></caption><colgroup>
                                <col style="width:20%;"/>
                                <col style="width:13%;"/>
                                <col style="width:32%;"/>
                                <col style="width:5%;"/>
                                <col style="width:8%;"/>
                                <col style="width:22%;"/>
                            </colgroup>
                            <thead>
                                <tr>
                                    <th><spring:message code="logmanager.logapplication.appname"/></th>
                                    <th><spring:message code="logmanager.logapplication.agentid"/></th>
                                    <th colspan="3"><spring:message code="logmanager.logapplication.logging.policy.file.path"/></th>
                                    <th><spring:message code="logmanager.logapplication.status"/></th>
                                    <th><spring:message code="logmanager.logapplication.actions"/></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${list}">
                                <tr>
                                    <td><a href="#" id="${app.id}" class="app-name">${app.appName}</a></td>
                                    <td>${app.agentId}</td>
                                    <td style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;max-width:300px;" title="${app.loggingPolicyFilePath}">${app.loggingPolicyFilePath}</td>
                                    <td class="align_center">
                                    	<img src="<c:url value="/logmanager/images/btn_inadd_i.gif"/>" title="<spring:message code="logmanager.logapplication.setlogdatacollection"/>" alt="<spring:message code="logmanager.logapplication.setlogdatacollection"/>" class="set-logcollection" agentId="${app.agentId}" loggingPolicyFilePath="${app.loggingPolicyFilePath}" appName="${app.appName}" loggingFramework="${app.loggingFramework}"/>
                                    </td>
                                    <td class="align_center">
                                    	<img src="<c:url value="/logmanager/images/btn_provisioning_i.gif"/>" title="<spring:message code="logmanager.logapplication.policy.edit"/>" alt="<spring:message code="logmanager.logapplication.policy.edit"/>" class="editor-select" agentId="${app.agentId}" loggingPolicyFilePath="${app.loggingPolicyFilePath}" appName="${app.appName}" loggingFramework="${app.loggingFramework}"/>
                                    </td>
                                    <td class="align_center">${app.statusMessage}</td>
                                    <td class="align_center" style="white-space:nowrap;">
	                                    <span class="button tablein small">
	                                        <button type="button"  class="reload-button" agentId="${app.agentId}" appName="${app.appName}" status="${app.status}">
	                                        <c:if test="${app.status == 0}"><spring:message code="logmanager.logapplication.inactive"/></c:if>
	                                        <c:if test="${app.status == 1}"><spring:message code="logmanager.logapplication.active"/></c:if>
	                                        </button>
	                                    </span>
	                                    <span class="button tablein small">
	                                        <button type="button"  class="delete-button" agentId="${app.agentId}" appName="${app.appName}"><spring:message code="logmanager.logapplication.button.delete"/></button>
	                                    </span>
	                                    
                                    </td>
                                </tr>
                            </c:forEach>                
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="btncontainer_right margin_top5">
                    	<span class="button">
                            <a href="#" id="btnAdd"><spring:message code="logmanager.logapplication.button.add"/></a>
                        </span>
                    </div>
                    
                    
                    
				</div>
			</div>
		</div>
	</div>

	<hr />
	<div id="footer_wrap">
		<div id="footer"><spring:message code="logmanager.footer.copyright"/></div>
	</div>
</div>
<!--[if IE]></div><![endif]-->
<!--[if !IE]></div><![endif]--><!--// wrapper E -->

<div id="app-detail-form" title="<spring:message code="logmanager.logapplication.logapplication.edit.title"/>">
	<div class="view poplayer margin_top10">
		<form:form name="appDetailForm" id="formAppDetail" modelAttribute="logApplication" onsubmit="return false;">
		<table summary="">
		    <caption><spring:message code="logmanager.logapplication.logapplication.edit.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <th class="topline"><spring:message code="logmanager.logapplication.appname"/></th>
		        <td class="topline"><input type="text" id="txtAppName" class="input_search"/><input type="hidden" id="hiddenId" /></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.logapplication.logagent"/></th>
		        <td>
		        	<select id="selectAgentId" name="agentId" class="select_search"/>
		        </td>
		    </tr>
		    <tr>
		    	<th><spring:message code="logmanager.logapplication.framework"/></th>
		    	<td><form:select id="selectLoggingFramework" path="loggingFramework" class="select_search" items="${loggingFrameworks}"/></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.logapplication.policy.path"/></th>
		        <td>
		        	<label for="txtLoggingPolicyFilePath"></label><input type="text" id="txtLoggingPolicyFilePath" class="input_search" title="ex) D:/logmanager/logs/log4j.xml 
 or /home/logmanager/logs/logback.xml"/>
	            </td>
		    </tr>
		    <tr>
		        <th class="bottomline">&nbsp;</th>
		        <td class="bottomline">
		        	<input type="checkbox" id="chkStatus" name="status" value="1" checked/>&nbsp;<label for="chkStatus"><spring:message code="logmanager.logapplication.active"/></label>
		        </td>
		    </tr>
		</table>
		</form:form>
	</div>

	<div class="btncontainer_right margin_top5">
	    <span class="button tableout large">
	        <button type="button"  id="btnSave"><spring:message code="logmanager.logapplication.button.save"/></button>
        </span>
        <span class="button tableout large">
	        <button type="button"  id="btnCancel"><spring:message code="logmanager.logapplication.button.cancel"/></button>
	    </span>
	</div>
</div>

<div id="set-logcollection-form" title="<spring:message code="logmanager.logapplication.logdatacollection.title"/>">
	<div class="view margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.logapplication.logdatacollection.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:20%;" />
		        <col style="width:30%;" />
		        <col style="width:20%;" />
		    </colgroup>
		    <tr>
		        <th class="topline bottomline"><spring:message code="logmanager.logapplication.application.name"/></th>
		        <td class="topline bottomline" id="tdAppName">&nbsp;</td>
		        <th class="topline bottomline"><spring:message code="logmanager.logapplication.agentid"/></th>
		        <td class="topline bottomline" id="tdAgentId">&nbsp;</td>
		    </tr>
		</table>
	</div>
	<div class="list poplayer margin_top10">
	    <table id="tblSetLogCollection" summary="Path of Log, Log File, Last Time Stamp, Collection Term, Active">
	        <caption><spring:message code="logmanager.logapplication.pathoflog"/>, <spring:message code="logmanager.logapplication.logfile"/>, <spring:message code="logmanager.logapplication.lasttimestamp"/>, <spring:message code="logmanager.logapplication.term"/>, <spring:message code="logmanager.logapplication.active"/></caption>
	        <colgroup>
	            <col style="width:30%;" />
	            <col style="width:20%;" />
	            <col style="width:25%;" />
	            <col style="width:7%;" />
	            <col style="width:8%;" />
	            <col style="width:5%;" />
	            <col style="width:5%;" />
	        </colgroup>
	        <thead>
	            <tr>
	                <th><spring:message code="logmanager.logapplication.pathoflog"/></th>
	                <th><spring:message code="logmanager.logapplication.logfile"/></th>
	                <th><spring:message code="logmanager.logapplication.lasttimestamp"/></th>
	                <th><spring:message code="logmanager.logapplication.term"/></th>
	                <th><spring:message code="logmanager.logapplication.active"/></th>
	                <th><spring:message code="logmanager.logapplication.status"/></th>
	                <th>&nbsp;</th>
	            </tr>
	        </thead>
	        <tbody></tbody>
	    </table>
	</div>
	<div class="view margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.logapplication.logdatacollection.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:20%;" />
		        <col style="width:30%;" />
		        <col style="width:20%;" />
		    </colgroup>
		    <tr>
		        <th class="topline"><spring:message code="logmanager.logapplication.pathoflog"/></th>
		        <td class="topline" colspan="3"><input type="text" id="txtPathOfLog" title="ex) D:/logmanager/logs 
 or /home/logmanager/logs" value="D:/Temp/logmanager/logs"/><input type="hidden" id="hiddenId" /><input type="hidden" id="isValidate" value="false"/></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.logapplication.logfile.name"/></th>
		        <td colspan="3">
		        	<input type="text" id="txtLogFileName" name="logFileName" title="ex) common.log 
 or common.+?[log|log.\d{2}]
 or common.+?[log|log.\d{4}-\d{2}-\d{2}]"  value="commonFile.log"/>
		        </td>
		    </tr>
		    <tr>
		    	<th><spring:message code="logmanager.logapplication.collection.term"/></th>
		    	<td colspan="3">
		    		<input type="text" id="txtCollectionTerm" name="collectionTerm" value="1" style="text-align:right;padding-right:5px;" title="a number greater than 0"/>&nbsp;
		    		<input type="radio" id="rdoCollectionTermUnitSec" name="collectionTermUnit" value="s"/>&nbsp;<label for="rdoCollectionTermUnitSec"><spring:message code="logmanager.logapplication.second"/></label>&nbsp;
		    		<input type="radio" id="rdoCollectionTermUnitMin" name="collectionTermUnit" value="m"/>&nbsp;<label for="rdoCollectionTermUnitMin"><spring:message code="logmanager.logapplication.minute"/></label>&nbsp;
		    		<input type="radio" id="rdoCollectionTermUnitHr" name="collectionTermUnit" value="h" checked/>&nbsp;<label for="rdoCollectionTermUnitHr"><spring:message code="logmanager.logapplication.hour"/></label>&nbsp;
		    		<input type="radio" id="rdoCollectionTermUnitDay" name="collectionTermUnit" value="d"/>&nbsp;<label for="rdoCollectionTermUnitDay"><spring:message code="logmanager.logapplication.day"/></label>&nbsp;
		    	</td>
		    </tr>
		    <tr>
		        <th rowspan="2"><spring:message code="logmanager.logapplication.logdataformat"/></th>
		        <td colspan="3" class="span-td-top">
		        	<textarea id="taRegularExp" name="regularExp"></textarea>
	            </td>
			</tr>
			<tr>
	            <td colspan="3" class="align_right span-td-bottom">
	            	<input type="checkbox" id="chkRegularExp" name="isRegExp"></input>&nbsp;<label for="chkRegularExp"><spring:message code="logmanager.logapplication.regexp"/></label>
	            </td>
		    </tr>
		    <tr>
		        <th rowspan="2"><spring:message code="logmanager.logapplication.logdatesample"/></th>
		        <td colspan="3">
		        	<textarea id="taLogDataSample" name="logDataSample"></textarea>
	            </td>
		    </tr>
		    <tr style="height:25px;">
		    	<td colspan="2" class="align_left">
					<span class="button tablein small"><button type="button"  id="btnSample1">Sample 1</button></span>&nbsp;
		    		<span class="button tablein small"><button type="button"  id="btnSample2">Sample 2</button></span></span>		    	
		    	</td>
		    	<td class="align_right">
		        	<span class="button tableout small"><button type="button"  id="btnRegularExpValidate"><spring:message code="logmanager.logapplication.button.validate"/></button></span>
	            </td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.logapplication.columnsinfo"/></th>
		        <td colspan="3"><div id="divColumnsInfo"><ul id="ulColumnsInfo"></ul></div></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.logapplication.logrepository"/></th>
		        <td colspan="3"><select name="repositoryName" id="selectRepositoryName"></select></td>
		    </tr>
		    <tr>
		    	<th	class="bottomline">&nbsp;</td>
		    	<td class="bottomline align_left"><input type="checkbox" id="chkSetLogCollectionActive" name="setLogCollectioniActive" checked/>&nbsp;<label for="chkSetLogCollectionActive"><spring:message code="logmanager.logapplication.active"/></label></td>
		    	<td colspan="2" class="bottomline">&nbsp;</td>
			</tr>
		</table>
	</div>
	<div class="btncontainer_right margin_top5">
		<span class="button tableout large">
	        <button type="button"  id="btnSetLogCollectionNew"><spring:message code="logmanager.logapplication.button.new"/></button>
	    </span>
	    <span class="button tableout large">
	        <button type="button"  id="btnSetLogCollectionSave"><spring:message code="logmanager.logapplication.button.save"/></button>
        </span>
        <span class="button tableout large">
	        <button type="button"  id="btnSetLogCollectionClose"><spring:message code="logmanager.logapplication.button.close"/></button>
	    </span>
	</div>
</div>

<div id="gui-edit-form" title="<spring:message code="logmanager.logapplication.policy.edit.title"/>">
	<div id="accordion"></div>
	<div class="btncontainer_right margin_top5">
	    <span class="button tableout small">
	        <button type="button"  id="btnAppenderAdd"><spring:message code="logmanager.logapplication.button.add.appender"/></button>
	    </span>
	    <span class="button tableout small">
	        <button type="button"  id="btnLoggerAdd"><spring:message code="logmanager.logapplication.button.add.logger"/></button>
	    </span>
	    <span class="button tableout large">
	        <button type="button"  id="btnGuiSave"><spring:message code="logmanager.logapplication.button.save"/></button>
	    </span>
	    <span class="button tableout large">
	        <button type="button"  id="btnGuiCancel"><spring:message code="logmanager.logapplication.button.cancel"/></button>
	    </span>
	</div>
</div>

<div id="text-edit-form" title="<spring:message code="logmanager.logapplication.policy.edit.title"/>" style="padding:2px;">
	<div id="text-edit-title" class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix" style="padding: 5px 13px;margin-bottom:4px;">
		<span class="ui-dialog-title" id="ui-dialog-title-editor-select-form"><spring:message code="logmanager.logapplication.policy.edit.title"/></span>
	</div>
	<div id="xmlEditor" style="position:relative;height:500px;width:798px;"></div>
	<div class="btncontainer_right margin_top5" style="padding : 5px 10px 0 0;">
	    <span class="button tableout large">
	        <button type="button"  id="btnTextSave"><spring:message code="logmanager.logapplication.button.save"/></button>
	    </span>
	    <span class="button tableout large">
	        <button type="button"  id="btnTextCancel"><spring:message code="logmanager.logapplication.button.cancel"/></button>
	    </span>
	</div> 
</div>

<div id="logger-create-form" title="<spring:message code="logmanager.logapplication.logger.create.title"/>">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.logapplication.logger.create.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <th class="topline"><spring:message code="logmanager.logapplication.name"/></th>
		        <td class="topline"><input type="text" id="txtLoggerName" style="width:100%;"/></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.logapplication.additivity"/></th>
		        <td><select id="selectAdditivity" style="width:100%;"><option value="false">false</option><option value="true">true</option></select></td>
		    </tr>
		    <tr>
		        <th class="bottomline"><spring:message code="logmanager.logapplication.level"/></th>
		        <td class="bottomline">
		        	<select id="selectLoggerLogLevel" style="width:100%;">
		        		<option value="FATAL">FATAL</option>
		        		<option value="ERROR">ERROR</option>
		        		<option value="WARN">WARN</option>
		        		<option value="INFO">INFO</option>
		        		<option value="DEBUG">DEBUG</option>
		        		<option value="OFF">OFF</option>
		        	</select>
		        </td>
		    </tr>
		</table>
	</div>
</div>

<div id="appender-create-form" title="<spring:message code="logmanager.logapplication.appender.create.title"/>">
	<div class="view poplayer margin_top10">
		<table id="tblAppenderAdd" summary="">
		    <caption><spring:message code="logmanager.logapplication.appender.create.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <th class="topline"><spring:message code="logmanager.logapplication.name"/></th>
		        <td class="topline"><input type="text" id="txtAppenderName" style="width:100%;"/></td>
		    </tr>
		    <tr>
		        <th class="bottomline"><spring:message code="logmanager.logapplication.class"/></th>
		        <td class="bottomline">
		        	<select id="selectAppenderClass" style="width:100%">
		        		<option value="org.apache.log4j.ConsoleAppender">ConsoleAppender</option>
		        		<option value="org.apache.log4j.DailyRollingFileAppender">DailyRollingFileAppender</option>
		        		<option value="org.apache.log4j.RollingFileAppender">RollingFileAppender</option>
		        		<option value="org.anyframe.logmanager.log4j.MongoDbAppender">MongoDbAppender</option>
		        		<option value="MI"><spring:message code="logmanager.logapplication.manually.input"/></option>
		        	</select>
		        	<input type="text" id="txtAppenderClass" style="width:90%;display:none;"/><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="<spring:message code="logmanager.logapplication.button.cancel"/>" title="<spring:message code="logmanager.logapplication.button.cancel"/>" id="appenderClassCancel" style="cursor:pointer;padding-left:10px;display:none;"/>
		        </td>
		    </tr>
		</table>
		<table id="tblLayoutAdd" summary="">
		    <caption><spring:message code="logmanager.logapplication.layout.create.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <td colspan="2" class="layout"><spring:message code="logmanager.logapplication.layout"/></td>
		    </tr>
		    <tr>
		        <th class="topline"><spring:message code="logmanager.logapplication.class"/></th>
		        <td class="topline">
		        	<select id="selectLayoutClass" style="width:100%">
		        		<option value="org.apache.log4j.PatternLayout"><spring:message code="logmanager.logapplication.patternlayout"/></option>
		        		<option value="MI"><spring:message code="logmanager.logapplication.manually.input"/></option>
		        	</select>
		        	<input type="text" id="txtLayoutClass" style="width:90%;display:none;"/><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="<spring:message code="logmanager.logapplication.button.cancel"/>" title="<spring:message code="logmanager.logapplication.button.cancel"/>" id="layoutClassCancel" style="cursor:pointer;padding-left:10px;display:none;"/>
		        </td>
		    </tr>
		    <tr>
		        <th class="bottomline">ConversionPattern</th>
		        <td class="bottomline"><input type="text" id="txtConversionPattern" style="width:100%;"/></td>
		    </tr>
		</table>
	</div>
</div>

<div id="editor-select-form" title="<spring:message code="logmanager.logapplication.editor.select.title"/>">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.logapplication.editor.select.title"/></caption>
		    <colgroup>
		        <col style="width:50%;" />
		        <col style="width:50%;" />
		    </colgroup>
		    <tr>
		        <td class="topline bottomline"><a href="#" id="gui"><spring:message code="logmanager.logapplication.gui"/></a></td>
		        <td class="topline bottomline"><a href="#" id="text"><spring:message code="logmanager.logapplication.text"/></a></td>
		    </tr>
		</table>
	</div>
</div>

<div id="result-message-form" title="<spring:message code="logmanager.logapplication.result.message.title"/>">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.logapplication.result.message.title"/></caption>
		    <colgroup>
		        <col style="width:50%;" />
		        <col style="width:50%;" />
		    </colgroup>
		    <tr>
		        <td class="topline bottomline" id="tdMessage"></td>
		    </tr>
		</table>
	</div>
</div>

</body>
</html>