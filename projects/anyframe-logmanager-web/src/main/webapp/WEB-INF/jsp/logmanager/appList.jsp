<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ page import="org.anyframe.logmanager.LogManagerConstant" %>

<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Anyframe Log Manager Web</title>

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

<style>
	div.divEditor {
		position:absolute;
		display:inline; 
		top:100px;
		left:100px;
		width:600px; 
		height:400px; 
		background-color:#ffffff;
	}
</style>

<script type="text/javascript" src="<c:url value='/logmanager/javascript/InputCalendar.js'/>"></script>
<script type="text/javascript" src="<c:url value='/logmanager/javascript/logmanager.js'/>"></script>
<script type="text/javascript">
<!--
function fncLogout(){
	var msg = "Are you sure you want to logout ?";
    ans = confirm(msg);
    if (ans) {
    	document.location.href="<c:url value='/logout.do'/>";
    } else {
        return false;
    }
}

var editor = null;

var MONITOR_LEVEL_ADMIN_ONLY = <%=LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY%>;
var MONITOR_LEVEL_DEV_VISIBLE = <%=LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE%>;

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
//var MONGODB_CONVERSIONPATTERN = 'level:%p,thread:%t,message:%m,className:%C,methodName:%M,lineNumber:%L,userId:%X{userId},userName:%X{userName},clientIp:%X{clientIp},serverId:%X{serverId},appName:%X{appName},category:%c';
var LOGMANGER_CONVERSIONPATTERN = '[%-5p] [%d] [%X{clientIp}/%X{serverId}:%X{appName}] [%X{userId}/%X{userName}] [%c.%M()(:%L;%t)]: %n%m%n';
var JSON_CONVERSIONPATTERN = 'level:%1,clientIp:%3,serverId:%4,appName:%5,userId:%6,userName:%7,className:%8,methodName:%9,lineNumber:%.10,thread:%.11,message:%.12';

var PATTERN_LAYOUT_CLASS = 'org.apache.log4j.PatternLayout';
//var MONGODB_PATTERN_LAYOUT_CLASS = 'org.anyframe.logmanager.log4mongo.MongoDbPatternLayout';
var LOGMANGER_PATTERN_LAYOUT_CLASS = 'org.anyframe.logmanager.log4j.PatternLayout'

REQUEST_CONTEXT = '<%=request.getContextPath()%>';

/**
 * 
 */
function save() {
	var size = $('#tblAppender input:checkbox:checked.harvest').length;
	var appendersArray = new Array(size);
	var pollingTimesArray = new Array(size);
	var monitorLevelsArray = new Array(size);
	var fileAppenderArray = new Array(size);
	var collectionNameArray = new Array(size);
	var statusArray = new Array(size);
	var agentId = $('#selectAgentId').val();
	
	if(size > 0) {
		$('#tblAppender input:checkbox:checked.harvest').each(function(j, el) {
			
			var id = $(el).val();
			appendersArray[j] = id.replace(/_/g, '.');
			pollingTimesArray[j] = $('#txtPollingTime_' + id).val() + $('#selectUnit_' + id).val();
			monitorLevelsArray[j] = $('#selectMonitorLevel_' + id).val();
			fileAppenderArray[j] = !$('#checkbox_' + id).attr('disabled');
			statusArray[j] = $('#checkbox_' + id).attr('checked') == 'checked' ? 1 : 0;
			collectionNameArray[j] = $('#txtCollectionName_' + id).val();
			
			if(j == size - 1) {
				loading($('#app-detail-form'));
				$.post('<c:url value="/logManager.do?method=saveLogApplication"/>', 
						{'id' : $('#hiddenId').val(),
						'appenders[]' : appendersArray, 
						'agentId' : agentId,
						'appName' : $.trim($('#txtAppName').val()), 
						'pollingTimes' : pollingTimesArray,
						'monitorLevels' : monitorLevelsArray,
						'fileAppenders' : fileAppenderArray,
						'collectionNames' : collectionNameArray,
						'status' : statusArray,
						'loggingFramework' : $('#selectLoggingFramework').val(),
						'loggingPolicyFilePath' : $.trim($('#txtLoggingPolicyFilePath').val())}, 
						function(data) {
							alert('save is successful');
							loadingClose($('#app-detail-form'));
							$( "#app-detail-form" ).dialog('close');
							self.location.reload(true);
						}).error(function(jqXHR, textStatus, errorThrown) {
					        alert("Error: " + textStatus + " " + errorThrown);
					        loadingClose($('#app-detail-form'));
						});
				
			}
		});
	}else{ 
		loading($('#app-detail-form'));
		$.post('<c:url value="/logManager.do?method=saveLogApplication"/>',
				{'id' : $('#hiddenId').val(),
				'appenders[]' : appendersArray, 
				'agentId' : agentId,
				'appName' : $.trim($('#txtAppName').val()), 
				'pollingTimes' : pollingTimesArray,
				'monitorLevels' : monitorLevelsArray,
				'fileAppenders' : fileAppenderArray,
				'collectionNames' : collectionNameArray,
				'status' : statusArray,
				'loggingFramework' : $('#selectLoggingFramework').val(),
				'loggingPolicyFilePath' : $.trim($('#txtLoggingPolicyFilePath').val())}, 
			function(data) {
					alert('save is successful');
					loadingClose($('#app-detail-form'));
					$( "#app-detail-form" ).dialog('close');
					self.location.reload(true);
				}).error(function(jqXHR, textStatus, errorThrown) {
			        alert("Error: " + textStatus + " " + errorThrown);
			        loadingClose($('#app-detail-form'));
				});
	}

	currentAppName = null;
	currentAgentid = null;
}

/**
 * 
 */
function accordionRefresh(acc) {
	
	$('#' + acc).accordion('destroy');
	$('#' + acc).accordion({
		autoHeight : false,
		header: "> div > h3"
	});
}

/**
 * 
 */
function generateRoot(root) {

	ROOT = root;
	var html = '<div id="root" class="view poplayer margin_top10 root">\n';
	html += '<h3><a href="#">Root Logger</a></h3>\n';
	html += '<div>\n';
	html += '<table id="tbl_Root">\n';
	html += '<colgroup>\n';
	html += '<col style="width:25%;" />\n';
	html += '<col style="width:69%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '</colgroup>\n';
	html += '<tr type="level" parentType="Root" parentId="Root"><th class="topline">Level</th><td class="topline">' + root.level + '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
	for(var i=0;i<root.appenderRefs.length;i++) {
		var appRef = root.appenderRefs[i];
		html += '<tr type="appender-ref" parentType="Root" parentId="Root"><th>Appender-Ref</th><td>' + appRef + '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
	}
	html += '</table>\n';
	html += '<div class="btncontainer_right margin_top5">\n';
	html += '<span class="button tablein small">\n';
	html += '<button id="btnAppenderRefAdd_Root">Add Appender-Ref</button>\n';
	html += '</span>\n';
	html += '</div>\n';
	html += '</div>\n';
	html += '</div>';
	//alert(html);
	return html;
}

/**
 * 
 */
function generateLogger(logger, id) {

	LOGGERs.put(id, logger);
	
	var html = '<div id="' + id + '" class="view poplayer margin_top10 logger">\n';
	html += '<h3><a href="#">Logger : ' + logger.name + '</a></h3>\n';
	html += '<div>\n';
	html += '<table id="tbl_' + id + '">\n';
	html += '<colgroup>\n';
	html += '<col style="width:25%;" />\n';
	html += '<col style="width:69%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '<col style="width:3%;" />\n';
	html += '</colgroup>\n';
	html += '<tr type="additivity" parentType="Logger" parentId="' + id + '"><th class="topline">Additivity</th><td class="topline">' + logger.additivity + '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
	html += '<tr type="level" parentType="Logger" parentId="' + id + '"><th>Level</th><td>' + logger.level + '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon">&nbsp;</td></tr>\n';
	for(var i=0;i<logger.appenderRefs.length;i++) {
		var appRef = logger.appenderRefs[i];
		html += '<tr type="appender-ref" parentType="Logger" parentId="' + id + '"><th>Appender-Ref</th><td>' + appRef + '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
	}
	html += '</table>\n';
	html += '<div class="btncontainer_right margin_top5">\n';
	html += '<span class="button tablein small">\n';
	html += '<button id="btnLoggerDelete_' + id + '" type="Logger" parentId="' + id + '">Delete</button>\n';
	html += '</span>\n';
	html += '<span class="button tablein small">\n';
	html += '<button id="btnAppenderRefAdd_' + id + '" parentId="' + id + '">Add Appender-Ref</button>\n';
	html += '</span>\n';
	html += '</div>\n';
	html += '</div>\n';
	html += '</div>\n';
	//alert(html);
	return html;
}

/**
 * 
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
	html += '<tr type="class" parentType="Appender" parentId="' + id + '"><th class="topline">Class</th><td class="topline class">' + appender.appenderClass + '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
	if(appender.appenderRefs) {
		for(var i=0;i<appender.appenderRefs.length;i++) {
			var appRef = appender.appenderRefs[i];
			html += '<tr type="appender-ref" parentType="Appender" parentId="' + id + '"><th>Appender-Ref</th><td>' + appRef + '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
		}
	}
	
	for(var i=0;i<appender.params.length;i++) {
		var param = appender.params[i];
		html += '<tr type="param" parentType="Appender" parentId="' + id + '"><th>' + param.name + '</th><td class="value">' + param.value + '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
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
		html += '<tr type="Layout" parentType="Appender" parentId="' + id + '"><td colspan="4" class="layout">Layout</td></tr>\n';
		html += '<tr type="class" parentType="Layout" parentId="' + id + '"><th class="topline">Class</th><td class="topline class">' + appender.layout.layoutClass + '</td><td class="topline icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="topline icon">&nbsp;</td></tr>\n';
		if(appender.layout.params && typeof(appender.layout.params) != "undefined") {
			for(var i=0;i<appender.layout.params.length;i++) {
				var param = appender.layout.params[i];
				html += '<tr type="param" parentType="Layout" parentId="' + id + '"><th>' + param.name + '</th><td class="value">' + param.value + '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>\n';
			}
		}
		html += '</table>\n';
	}
	html += '<div class="btncontainer_right margin_top5">\n';
	html += '<span class="button tablein small">\n';
	html += '<button id="btnAppenderDelete_' + id + '" type="Appender" parentId="' + id + '">Delete</button>\n';
	html += '</span>\n';
	html += '<span class="button tablein small">\n';
	html += '<button id="btnParamAdd_' + id + '" parentId="' + id + '">Add Param</button>\n';
	html += '</span>\n';
	
	if(appender.appenderClass != MONGODB_APPENDER_CLASS) {
		html += '<span class="button tablein small">\n';
		html += '<button id="btnLayoutParamAdd_' + id + '" parentId="' + id + '">Add Layout Param</button>\n';
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
 * 
 */
function editFormClose() {
	$('#gui-edit-form').dialog('close');
	$('#text-edit-form').editorDialog('close');
}

/**
 * 
 */
function addAppenderRef(type, parentId, value) {
	if(type == 'Root') {
		ROOT.appenderRefs.push(value);
	}else if(type == 'Logger'){
		LOGGERs.get(parentId).appenderRefs.push(value);
	}
	var t = $('#tbl_' + parentId);
	var html = '<tr type="appender-ref" parentType="' + type + '" parentId="' + parentId + '"><th>Appender-Ref</th><td>' + value + '</td><td class="icon">&nbsp;</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>';
	t.append(html);
	$('.removeBtn').click(function(e) {
		removeEventHandler(e);
	});
	$('#tbl_' + parentId + ' .bottomline').removeClass('bottomline');
	$('#tbl_' + parentId + ' tr:last > th').addClass('bottomline');
	$('#tbl_' + parentId + ' tr:last > td').addClass('bottomline');
}

/**
 * 
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
	var html = '<div id="appender-ref-form" title="Appender-Ref">';
	html += '<div class="view poplayer margin_top10">';
	html += '<table><colgroup><col style="width:30%"/><col style="width:70%"/></colgroup><tr><th>Appender-Ref</th><td>';
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
			'Cancel' : function(){
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
 * 
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
	
	var html = '<tr type="param" parentType="' + type + '" parentId="' + parentId + '"><th>' + name + '</th><td>' + value + '</td><td class="icon"><img src="<c:url value="/logmanager/images/btn_edit_i.gif"/>" alt="Edit" title="Edit" class="editBtn"></td><td class="icon"><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Remove" title="Remove" class="removeBtn"></td></tr>';
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
 * 
 */
function paramAddForm(type, id) {
	var a = APPENDERs.get(id);
	var b = null;
	if(type == 'Appender') {
		b = a.params;
	}else if(type == 'Layout'){
		b = a.layout.params;
	}
	var html = '<div id="param-edit-form" title="Param Edit Form">';
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
			'Add' : function(){
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
				if(isDup) {
					alert(name + ' is aleady exist.');
					return;
				}
				addParam(type, id, name, value);
				$(this).dialog('close');
			},
			'Cancel' : function(){
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
	self.attr('alt', 'Save');
	self.attr('title', 'Save');
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
 * 
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
 * 
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
		 * btnAppenderRefAdd_ParamXX click handler
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
		alert('Logger aleady exist.');
		return false;
	}
}

/**
 * 
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

function getActiveAgentList(defalutAgentId, callback) {
	
	var url = '<c:url value="/logManager.do?method=getActiveAgentList"/>';
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
				html += '<option value="' + agentList[i].agentId + '" ' + selected + '>' + agentList[i].agentId + '</option>\n';
			}
		}else{
			html += '<option value="' + defalutAgentId + '" selected>' + defalutAgentId + '</option>\n';
		}
		$('#selectAgentId').html(html);

		if(typeof callback == 'function'){
			callback();
		}
	});
}

$(document).ready(function() {

	/**
	 * 'Add' button click event handler
	 * open log application edit form
	 */
	$('#btnAdd').click(function(e) {
		$('#app-detail-form').dialog({
			position:['center', 100]
		});
		$('#txtAppName').removeAttr('disabled');
		$('#txtAppName').val('');
		$('#txtLoggingPolicyFilePath').val('');
		$('#tblAppender tbody tr').remove();
		$('#hiddenId').val('');

		$('#btnSave').show();
		$('#txtLoggingPolicyFilePath').removeAttr('disabled');
		$('#btnLoggingPolicyFileLoad').removeAttr('disabled');
		$('#selectLoggingFramework').removeAttr('disabled');
		$('#spanOffline').hide();
		
		getActiveAgentList('', function() {
			$('#selectAgentId').removeAttr('disabled');
			$('#app-detail-form').dialog('open');
		});
	});

	/**
	 * log application 'Delete' button click event handler
	 * log application delete
	 */
	$('.delete-button').click(function(e) {
		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		if(confirm('Do you want really to delete \"' + appName + '\"?')) {
			$.get('<c:url value="/logManager.do?method=deleteApplication"/>&agentId=' + agentId + '&appName=' + appName, function(data) {
				self.location.reload(true);
			});
		}
	});

	/**
	 * log application 'Active/Inactive/Realod' button click event handler 
	 * log application active/inactive/reload
	 */
	$('.reload-button').click(function(e) {
		var appName = $(e.target).attr('appName');
		var agentId = $(e.target).attr('agentId');
		var status = $(e.target).attr('status');
		loading($('body'));
		$.get('<c:url value="/logManager.do?method=reloadApplication"/>&agentId=' + agentId + '&status=' + status + '&appName=' + appName, function(data) {
			loadingClose($('body'));
			self.location.reload(true);
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + textStatus + " " + errorThrown);
	        loadingClose($('body'));
		});
	});

	/**
	 * editor select popup display
	 */
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
	 * 'Text' button click event handler
	 * open text type policy(logging policy file) edit form
	 */
	$('#text').click(function(e){
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
			 
			/* $('#checkboxUseWrapMode').attr('checked', 'checked');
			
			$('#checkboxUseWrapMode').click(function(event){
				if($('#checkboxUseWrapMode').attr('checked')) {
					editor.getSession().setUseWrapMode(true);
				}else{
					editor.getSession().setUseWrapMode(false);
				}
			}); */
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + textStatus + " " + errorThrown);
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
			var loggers = data.logApplication.loggers;
			var appenders = data.logApplication.appenders;
			var root = data.logApplication.root;
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
			html += '<h3><a href="#">Logger</a></h3>\n';
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
	        alert("Error: " + textStatus + " " + errorThrown);
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
			var appenders = app.appenders;
			var currAppenders = data.currAppenders;
			var isConnect = Boolean(data.isConnect);

			currentAppName = app.appName;
			currentAgentid = app.agentId;
			
			$('#txtAppName').val(app.appName);
			$('#txtAppName').attr('disabled', 'disabled');
			$('#txtLoggingPolicyFilePath').val(app.loggingPolicyFilePath);
			$('#selectLoggingFramework').val(app.loggingFramework);
			$('#selectLoggingFramework').attr('disabled', 'disabled');
			$('#hiddenId').val(app.id);

			$('#tblAppender tbody tr').remove();
			
			if(isConnect) {
				for(var i=0;i<appenders.length;i++) {
					var html = '<tr><td>';
					var id = (appenders[i].name).replace(/\./g, '_');
					
					if($('#selectLoggingFramework').val() == '<%=LogManagerConstant.LOGGING_FRAMEWORKS[0]%>' 
						|| appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOGBACK%>') {
						html += appenders[i].name + '</td>';
			            html += '<td>' + appenders[i].appenderClass + '</td>';
			            html += '<td class="align_center"><input type="checkbox" id="checkbox_' + id + '" class="harvest" value="' + id + '"/></td>';
			            html += '<td><nobr><input type="text" id="txtPollingTime_' + id + '" size="4" value="1"/>&nbsp;';
			            html += '<select class="unit" id="selectUnit_' + id + '"><option value="s">Sec</option><option value="m">Min</option><option value="h" selected>Hr</option><option value="d">Day</option></select></nobr></td>';
			            html += '<td><select class="monitor-level" id="selectMonitorLevel_' + id + '">';
			            html += '<option value="' + MONITOR_LEVEL_ADMIN_ONLY + '">Admin Only</option>';
			            html += '<option value="' + MONITOR_LEVEL_DEV_VISIBLE + '">Developer Visible</option>';
			            html += '</select></td>';
			            html += '<td><input type="text" id="txtCollectionName_' + id + '" value="' + appenders[i].name + '"/>'
			            html += '</td></tr>';

			            $('#tblAppender tbody').append(html);

			            // mongodb appender case
			            if(appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOG4J_PATTERNLAYOUT%>'
							|| appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOG4J%>'
							|| appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOGBACK%>') {
				            $('#checkbox_' + id).attr('checked', 'checked');
				            $('#checkbox_' + id).attr('disabled', 'disabled');
				            $('#txt_' + id).val(0);
				            $('#txt_' + id).attr('disabled', 'disabled');
				            $('#select_' + id).attr('disabled', 'disabled');
				            $('#txtPollingTime_' + id).attr('disabled', 'disabled');
				            $('#selectUnit_' + id).attr('disabled', 'disabled');
				            $('#txtCollectionName_' + id).attr('disabled', 'disabled');
				            /* for(var j=0;j<appenders[i].params.length;j++) {
					            if(appenders[i].params[j].name == 'collectionName') {
				            		$('#txtCollectionName_' + id).val(appenders[i].params[j].value);
					            }    
				            } */
						}
						
			            if(appenders[i].params.length > 0) {
				            var isMonitorLevel = false;
				            for(var j=0;j<appenders[i].params.length;j++) {
					            var param = appenders[i].params[j];
					            if(param.name == 'monitorLevel') {
					            	$('#' + id).val(param.name);
					            	isMonitorLevel = true;
					            	break;
					            }
				            }
				            if(!isMonitorLevel) {
				            	$('#' + id).val(MONITOR_LEVEL_ADMIN_ONLY);
				            }
			            }

			            /**
			             * integer check event handler
			             **/
			            $('#txt_' + id).keyup(function(e) {
				            try{
					            var n = parseInt($(e.target).val().replace(/[^0-9]/g, ''));
					            if(isNaN(n)) n = 1;
			            		$(e.target).val(n);
				            }catch(e) {
				            	$(e.target).val(1);
				            }
				        });
					}
				}

				for(var i=0;i<currAppenders.length;i++) {
					var id = currAppenders[i].appenderName.replace(/\./g, '_');
					if(currAppenders[i].status == 1) {
						$('#checkbox_' + id).attr('checked', 'checked');
					}
					var pollingTime = currAppenders[i].pollingTime;
					var unit = pollingTime.substring(pollingTime.length -1);
					var time = pollingTime.substring(0, pollingTime.length -1);
					$('#txtPollingTime_' + id).val(time);
					$('#selectUnit_' + id).val(unit);
					$('#selectMonitorLevel_' + id).val(currAppenders[i].monitorLevel);
					$('#txtCollectionName_' + id).val(currAppenders[i].collectionName);
				}
				$('#btnSave').show();
				$('#txtLoggingPolicyFilePath').removeAttr('disabled');
				$('#btnLoggingPolicyFileLoad').removeAttr('disabled');
				$('#selectAgentId').removeAttr('disabled');
				$('#spanOffline').hide();
				getActiveAgentList(currentAgentid);
				
			}else{
				
				for(var i=0;i<currAppenders.length;i++) {
					var html = '<tr><td>';
					var id = (currAppenders[i].appenderName).replace(/\./g, '_');
					
		            html += currAppenders[i].appenderName + '</td>';
		            html += '<td class="align_center">&nbsp;-&nbsp;</td>';
		            var checked = '';
		            if(currAppenders[i].fileAppender) {
			            checked = 'checked';
		            }
		            html += '<td class="align_center"><input type="checkbox" id="checkbox_' + id + '" class="harvest" value="' + id + '" disabled ' + checked + ' /></td>';
		            html += '<td><nobr><input type="text" id="txtPollingTime_' + id + '" size="4" value="1"/>&nbsp;';
		            html += '<select class="unit" id="selectUnit_' + id + '" disabled><option value="s">Sec</option><option value="m">Min</option><option value="h" selected>Hr</option><option value="d">Day</option></select></nobr></td>';
		            html += '<td><select class="monitor-level" id="selectMonitorLevel_' + id + '" disabled>';
		            html += '<option value="' + MONITOR_LEVEL_ADMIN_ONLY + '">Admin Only</option>';
		            html += '<option value="' + MONITOR_LEVEL_DEV_VISIBLE + '">Developer Visible</option>';
		            html += '</select></td>';
		            html += '<td><input type="text" id="txtCollectionName_' + id + '" value="' + currAppenders[i].name + '"/>'
		            html += '</td></tr>';

		            $('#tblAppender tbody').append(html);
				}

				for(var i=0;i<currAppenders.length;i++) {
					var id = currAppenders[i].appenderName.replace(/\./g, '_');
					var pollingTime = currAppenders[i].pollingTime;
					var unit = pollingTime.substring(pollingTime.length -1);
					var time = pollingTime.substring(0, pollingTime.length -1);
					$('#txtPollingTime_' + id).val(time);
					$('#selectUnit_' + id).val(unit);
					$('#selectMonitorLevel_' + id).val(currAppenders[i].monitorLevel);
				}
				
				$('#btnSave').hide();
				$('#txtLoggingPolicyFilePath').attr('disabled', 'disabled');
				$('#btnLoggingPolicyFileLoad').attr('disabled', 'disabled');
				$('#selectAgentId').attr('disabled', 'disabled');
				$('#spanOffline').show();
				$('#selectAgentId').html('<option value="' + currentAgentid + '">' + currentAgentid + '</option>');
			}
			
			$('#app-detail-form').dialog('open');
			$('#app-detail-form').dialog({width:$('#tblAppender').width() + 70});
			$('#app-detail-form').dialog({position:['center', 100]});
		}).error(function(jqXHR, textStatus, errorThrown) {
	        alert("Error: " + textStatus + " " + errorThrown);
	        loadingClose($('body'));
		});
	});

	/**
	 * 'Load' button click event handler
	 * logging policy file load & parser
	 */
	$('#btnLoggingPolicyFileLoad').click(function(e){
		if($('#txtLoggingPolicyFilePath').val() == '') return false;
		loading($('#app-detail-form'));
		$.get('<c:url value="/logManager.do?method=loadLoggingPolicyFile"/>&agentId=' + $('#selectAgentId').val() + '&loggingPolicyFilePath=' + $('#txtLoggingPolicyFilePath').val() + '&loggingFramework=' + $('#selectLoggingFramework').val(), function(data){

			if(checkError(data)) {
				loadingClose($('#app-detail-form'));
				return;
			}
			loadingClose($('#app-detail-form'));
			//alert(data.app.appenders.length + '|' + data.app.loggers.length);
			var loggers = data.logApplication.loggers;
			var appenders = data.logApplication.appenders;

			$('#tblAppender tbody tr').remove();
			
			for(var i=0;i<appenders.length;i++) {
				var html = '<tr><td>';
				var id = (appenders[i].name).replace(/\./g, '_');
				
				if($('#selectLoggingFramework').val() == '<%=LogManagerConstant.LOGGING_FRAMEWORKS[0]%>' 
						|| appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOGBACK%>') {
					html += appenders[i].name + '</td>';
		            html += '<td>' + appenders[i].appenderClass + '</td>';
		            html += '<td class="align_center"><input type="checkbox" id="checkbox_' + id + '" class="harvest" value="' + id + '"/></td>';
		            html += '<td><nobr><input type="text" id="txtPollingTime_' + id + '" size="4" value="1"/>&nbsp;';
		            html += '<select class="unit" id="selectUnit_' + id + '"><option value="s">Sec</option><option value="m">Min</option><option value="h" selected>Hr</option><option value="d">Day</option></select></nobr></td>';
		            html += '<td><select class="monitor-level" id="selectMonitorLevel_' + id + '">';
		            html += '<option value="' + MONITOR_LEVEL_ADMIN_ONLY + '">Admin Only</option>';
		            html += '<option value="' + MONITOR_LEVEL_DEV_VISIBLE + '">Developer Visible</option>';
		            html += '</select></td>';
		            html += '<td><input type="text" id="txtCollectionName_' + id + '" value="' + appenders[i].name + '"/>'
		            html += '</td></tr>';

		            $('#tblAppender tbody').append(html);

		            // mongodb appender case
		            if(appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOG4J_PATTERNLAYOUT%>' 
		            		|| appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOG4J%>'
		            		|| appenders[i].appenderClass == '<%=LogManagerConstant.APPENDER_CLASS_LOGBACK%>') {
			            $('#checkbox_' + id).attr('checked', 'checked');
			            $('#checkbox_' + id).attr('disabled', 'true');
			            $('#txt_' + id).val(0);
			            $('#txt_' + id).attr('disabled', 'true');
			            $('#select_' + id).attr('disabled', 'true');
			            $('#txtPollingTime_' + id).attr('disabled', 'disabled');
			            $('#selectUnit_' + id).attr('disabled', 'disabled');
			            $('#txtCollectionName_' + id).attr('disabled', 'disabled');
			            for(var j=0;j<appenders[i].params.length;j++) {
				            if(appenders[i].params[j].name == 'collectionName') {
			            		$('#txtCollectionName_' + id).val(appenders[i].params[j].value);
				            }    
			            }
					}
					
		            if(appenders[i].params.length > 0) {
			            var isMonitorLevel = false;
			            for(var j=0;j<appenders[i].params.length;j++) {
				            var param = appenders[i].params[j];
				            if(param.name == 'monitorLevel') {
				            	$('#' + id).val(param.name);
				            	isMonitorLevel = true;
				            	break;
				            }
			            }
			            if(!isMonitorLevel) {
			            	$('#' + id).val(MONITOR_LEVEL_ADMIN_ONLY);
			            }
		            }

		            /**
		             * integer check event handler
		             **/
		            $('#txt_' + id).keyup(function(e) {
			            try{
				            var n = parseInt($(e.target).val().replace(/[^0-9]/g, ''));
				            if(isNaN(n)) n = 1;
		            		$(e.target).val(n);
			            }catch(e) {
			            	$(e.target).val(1);
			            }
			        });
				}
			}
			$('#btnSave').show();
			$('#app-detail-form').dialog({width:$('#tblAppender').width() + 70});
			$('#app-detail-form').dialog({position:['center', 100]});
		});
	});

	/**
	 * 'Save' button click event handler
	 * db save and logging policy file apply
	 */
	$('#btnSave').click(function(e) {

		if($('#txtAppName').val() == '') {
			alert('check log application name.');
			$('#txtAppName').select();
			return;
		}
		
		if($('#selectAgentId option').length == 0) {
			alert('any agent is not active.');
			return;
		}

		if($('#txtLoggingPolicyFilePath').val() == '') {
			alert('check value of logging policy file path');
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
		        alert("Error: " + textStatus + " " + errorThrown);
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
		var url = '<c:url value="/logManager.do?method=saveLoggingPolicyFile"/>';
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
				alert('save is successful.');
				loadingClose($('#gui-edit-form'));
				editFormClose();
			}
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
		$.post(url, { 'agentId': currentAgentId, 'appName': currentAppName, 'loggingPolicyFileText': loggingPolicyFileText}, function(data){
			if(checkError(data)) {
				loadingClose($('#text-edit-form'));
				return;
			}else{
				alert('save is successful.');
				loadingClose($('#text-edit-form'));
				editFormClose();
			}
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
	 *
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
			html = '<option value="org.apache.log4j.PatternLayout">PatternLayout</option>';
			html += '<option value="org.anyframe.logmanager.log4mongo.MongoDbPatternLayout">MongoDbPatternLayout</option>';
			html += '<option value="org.anyframe.logmanager.log4j.PatternLayout">PatternLayout for Logmanager</option>';
			html += '<option value="MI">-- To manually input --</option>';
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
			html = '<option value="org.apache.log4j.PatternLayout">PatternLayout</option>';
			html += '<option value="MI">-- To manually input --</option>';
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
			html = '<option value="org.apache.log4j.PatternLayout">PatternLayout</option>';
			html += '<option value="org.anyframe.logmanager.log4j.PatternLayout">PatternLayout for Logmanager</option>';
			html += '<option value="MI">-- To manually input --</option>';
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
			html = '<option value="org.apache.log4j.PatternLayout">PatternLayout</option>';
			html += '<option value="org.anyframe.logmanager.log4j.PatternLayout">PatternLayout for Logmanager</option>';
			html += '<option value="MI">-- To manually input --</option>';
			$('#selectLayoutClass').append(html);
			$('#selectLayoutClass').val(PATTERN_LAYOUT_CLASS).trigger('change');
			$('#tblLayoutAdd').show();
		}else if(self.val() == MONGODB_APPENDER_CLASS) {
			$('#tblLayoutAdd').hide();
			
			//$('#txtConversionPattern').val(MONGODB_CONVERSIONPATTERN);

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

			//$('#selectLayoutClass > option').remove();
			//html = '<option value="org.anyframe.logmanager.log4mongo.MongoDbPatternLayout">MongoDbPatternLayout</option>';
			//$('#selectLayoutClass').append(html);
			//$('#selectLayoutClass').val(MONGODB_PATTERN_LAYOUT_CLASS).trigger('change');
		}

	});

	/**
	 *
	 **/
	$('#appenderClassCancel').click(function(e) {
		$('#selectAppenderClass').val(CONSOLE_APPENDER_CLASS).trigger('change');
		$('#selectAppenderClass').css('display', 'inline');
		$('#txtAppenderClass').css('display', 'none');
		$(e.target).css('display', 'none');
		$('#txtAppenderClass').val('');
	});

	/**
	 *
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
//		}else if(self.val() == MONGODB_PATTERN_LAYOUT_CLASS) {
			
//			$('#txtConversionPattern').val(MONGODB_CONVERSIONPATTERN);
//			$('#tblLayoutAdd tr.logmanager').remove();
			
//			$('#tblLayoutAdd tr:last > th').addClass('bottomline');
//			$('#tblLayoutAdd tr:last > td').addClass('bottomline');
			
		}else if(self.val() == LOGMANGER_PATTERN_LAYOUT_CLASS) {

			$('#txtConversionPattern').val(LOGMANGER_CONVERSIONPATTERN);
			
			html = '<tr class="logmanager"><th>ColumnDelimiter</th><td><input type="text" id="ColumnDelimiter" value="29" style="width:100%;"/></td></tr>\n';			
			html += '<tr class="logmanager"><th>RowDelimiter</th><td><input type="text" id="RowDelimiter" value="31" style="width:100%;"/></td></tr>\n';
			html += '<tr class="logmanager"><th>JsonPattern</th><td><input type="text" id="JsonPattern" value="' + JSON_CONVERSIONPATTERN + '" style="width:100%;"/></td></tr>\n';
			html += '<tr class="logmanager"><th>TimestampIndex</th><td><input type="text" id="TimestampIndex" value="2" style="width:100%;"/></td></tr>\n';
			html += '<tr class="logmanager"><th>TimestampDatePattern</th><td><input type="text" id="TimestampDatePattern" value="yyyy-MM-dd HH:mm:ss,SSS" style="width:100%;"/></td></tr>\n';

			$('#tblLayoutAdd th.bottomline').removeClass('bottomline');
			$('#tblLayoutAdd td.bottomline').removeClass('bottomline');
			$('#tblLayoutAdd').append(html);
			$('#tblLayoutAdd tr:last > th').addClass('bottomline');
			$('#tblLayoutAdd tr:last > td').addClass('bottomline');
			
		}
	});

	/**
	 *
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
	

	// ########################################################################

	/**
	 * log application edit form dialog define
	 */
	$( "#app-detail-form" ).dialog({
		autoOpen: false,
		width: 430,
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
	 */
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
	 */
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
	
	$("#appender-create-form").dialog({
		autoOpen: false,
		width: 600,
		height:"auto",
		modal: true,
		resizable:true,
		buttons : {
			'Add' : function(){

				if(addAppender()) {
					$(this).dialog('close');
				}else{
					alert('appender aleady exist.');
					return;
				}
				$(this).dialog('close');
			},
			'Cancel' : function(){
				$(this).dialog('close');
			}
		},
		close : function(e, ui) {
		}
	});

	$("#logger-create-form").dialog({
		autoOpen: false,
		width: 430,
		height:"auto",
		modal: true,
		resizable:true,
		buttons : {
			'Add' : function(){
				if(addLogger()) {
					$(this).dialog('close');
				}else{
					alert('logger aleady exist.');
					return;
				}
			},
			'Cancel' : function(){
				$(this).dialog('close');
			}
		},
		close : function(e, ui) {
		}
	});

	/**
	 * editor select form dialog define
	 */
	$("#editor-select-form").dialog({
		autoOpen: false,
		width: 430,
		height:100,
		modal: true,
		resizable:false
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
            		<li class="end"><a href="javascript:fncLogout();">
            		<c:out value="${sessionScope.loginAccount.userId}"/>님 Logout</a></li>
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
                    <li><a href="<c:url value="/logManager.do?method=appList"/>">Log Application Management</a></li>
                    <li><a href="<c:url value="/account.do?method=view"/>">Account Management</a></li>
                </c:if>
                </ul>
			</div>
			<hr/>
			
			<div id="contents">
				<h2>Log Application Management</h2>
                <div id="innercontents">
                	<div class="list">
                        <table summary="App Name, logging policy file, file_config, Status List">
                            
                            <caption>App Name, logging policy file, file-config, Status List</caption><colgroup>
                                <col style="width:20%;"/>
                                <col style="width:13%;"/>
                                <col style="width:32%;"/>
                                <col style="width:5%;"/>
                                <col style="width:8%;"/>
                                <col style="width:22%;"/>
                            </colgroup>
                            <thead>
                                <tr>
                                    <th>App Name</th>
                                    <th>Agent ID</th>
                                    <th colspan="2">Logging Policy File Path</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${list}">
                                <tr>
                                    <td><a href="#" id="${app.id}" class="app-name">${app.appName}</a></td>
                                    <td>${app.agentId}</td>
                                    <td style="overflow:hidden; text-overflow:ellipsis; white-space:nowrap;max-width:300px;" title="${app.loggingPolicyFilePath}">${app.loggingPolicyFilePath}</td>
                                    <td class="align_center">
                                    	<img src="<c:url value="/logmanager/images/btn_provisioning_i.gif"/>" title="Edit" alt="Edit" class="editor-select" agentId="${app.agentId}" loggingPolicyFilePath="${app.loggingPolicyFilePath}" appName="${app.appName}" loggingFramework="${app.loggingFramework}"/>
                                    </td>
                                    <td class="align_center">${app.statusMessage}</td>
                                    <td class="align_center" style="white-space:nowrap;">
	                                    <span class="button tablein small">
	                                        <button class="reload-button" agentId="${app.agentId}" appName="${app.appName}" status="${app.status}">
	                                        <c:if test="${app.status == 0}">Inactive</c:if>
	                                        <c:if test="${app.status == 1}">Active</c:if>
	                                        </button>
	                                    </span>
	                                    <span class="button tablein small">
	                                        <button class="delete-button" agentId="${app.agentId}" appName="${app.appName}">Delete</button>
	                                    </span>
	                                    
                                    </td>
                                </tr>
                            </c:forEach>                
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="btncontainer_right margin_top5">
                    	<span class="button">
                            <a href="#" id="btnAdd">Add</a>
                        </span>
                    </div>
                    
                    
                    
				</div>
			</div>
		</div>
	</div>

	<hr />
	<div id="footer_wrap">
		<div id="footer">Copyright 2012 www.anyframejava.org</div>
	</div>
</div>
<!--[if IE]></div><![endif]-->
<!--[if !IE]></div><![endif]--><!--// wrapper E -->

<div id="app-detail-form" title="Log Application Edit Form">
	<div class="view poplayer margin_top10">
		<form:form name="appDetailForm" id="formAppDetail" modelAttribute="logApplication" onsubmit="return false;">
		<table summary="">
		    <caption>Log Application Edit Form</caption>
		    <colgroup>
		        <col style="width:28%;" />
		        <col style="width:62%;" />
		    </colgroup>
		    <tr>
		        <th class="topline">App. Name</th>
		        <td class="topline"><input type="text" id="txtAppName" /><input type="hidden" id="hiddenId" /></td>
		    </tr>
		    <tr>
		        <th>Log Agent<span id="spanOffline" style="color:gray;">&nbsp;(Offline)</span></th>
		        <td>
		        	<select id="selectAgentId" name="agentId" class="select-search"/>
		        </td>
		    </tr>
		    <tr>
		    	<th>Framework</th>
		    	<td><form:select id="selectLoggingFramework" path="loggingFramework" items="${loggingFrameworks}"/></td>
		    </tr>
		    <tr>
		        <th>Policy Path</th>
		        <td>
		        	<label for="txtLoggingPolicyFilePath"></label><input type="text" id="txtLoggingPolicyFilePath" />&nbsp;
		        	<span class="button tablein small"><button id="btnLoggingPolicyFileLoad">Load</button></span>
	            </td>
		    </tr>
		</table>
		</form:form>
	</div>
	<div class="list">
	    <table id="tblAppender" summary="App Name, logging policy file, file_config, Status List">
	        <caption>Appender Name, Appender Class, Harvest, Polling Time, View Level</caption>
	        <colgroup>
	            <col style="width:17%;" />
	            <col style="width:28%;" />
	            <col style="width:10%;" />
	            <col style="width:20%;" />
	            <col style="width:15%;" />
	            <col style="width:10%;" />
	        </colgroup>
	        <thead>
	            <tr>
	                <th>Appender Name</th>
	                <th>Appender Class</th>
	                <th>Harvest</th>
	                <th>Polling Time</th>
	                <th>Monitor Level</th>
	                <th>Collection Name</th>
	            </tr>
	        </thead>
	        <tbody></tbody>
	    </table>
	</div>
	<div class="btncontainer_right margin_top5">
	    <span class="button tableout large">
	        <button id="btnSave">Save</button>
        </span>
        <span class="button tableout large">
	        <button id="btnCancel">Cancel</button>
	    </span>
	</div>
</div>

<div id="gui-edit-form" title="Logging Policy Edit Form">
	<div id="accordion"></div>
	<div class="btncontainer_right margin_top5">
	    <span class="button tableout small">
	        <button id="btnAppenderAdd">Add Appender</button>
	    </span>
	    <span class="button tableout small">
	        <button id="btnLoggerAdd">Add Logger</button>
	    </span>
	    <span class="button tableout large">
	        <button id="btnGuiSave">Save</button>
	    </span>
	    <span class="button tableout large">
	        <button id="btnGuiCancel">Cancel</button>
	    </span>
	</div>
</div>

<div id="text-edit-form" title="Logging Policy Edit Form" style="padding:2px;">
	<div id="text-edit-title" class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix" style="padding: 5px 13px;margin-bottom:4px;">
		<span class="ui-dialog-title" id="ui-dialog-title-editor-select-form">Logging Policy Edit Form</span>
	</div>
	<div id="xmlEditor" style="position:relative;height:500px;width:798px;"></div>
	<div class="btncontainer_right margin_top5" style="padding : 5px 10px 0 0;">
		<!--
		<span><input type="checkbox" id="checkboxUseWrapMode" checked="checked"/><label for="checkboxUseWrapMode">Use Wrap Mode</label></span> 
		 -->
	    <span class="button tableout large">
	        <button id="btnTextSave">Save</button>
	    </span>
	    <span class="button tableout large">
	        <button id="btnTextCancel">Cancel</button>
	    </span>
	</div> 
</div>


<div id="logger-create-form" title="Logger Create Form">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption>Logger Create Form</caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <th class="topline">Name</th>
		        <td class="topline"><input type="text" id="txtLoggerName" style="width:100%;"/></td>
		    </tr>
		    <tr>
		        <th>Additivity</th>
		        <td><select id="selectAdditivity" style="width:100%;"><option value="false">false</option><option value="true">true</option></select></td>
		    </tr>
		    <tr>
		        <th class="bottomline">Level</th>
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

<div id="appender-create-form" title="Appender Create Form">
	<div class="view poplayer margin_top10">
		<table id="tblAppenderAdd" summary="">
		    <caption>Appender Create Form</caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <th class="topline">Name</th>
		        <td class="topline"><input type="text" id="txtAppenderName" style="width:100%;"/></td>
		    </tr>
		    <tr>
		        <th class="bottomline">Class</th>
		        <td class="bottomline">
		        	<select id="selectAppenderClass" style="width:100%">
		        		<option value="org.apache.log4j.ConsoleAppender">ConsoleAppender</option>
		        		<option value="org.apache.log4j.DailyRollingFileAppender">DailyRollingFileAppender</option>
		        		<option value="org.apache.log4j.RollingFileAppender">RollingFileAppender</option>
		        		<option value="org.anyframe.logmanager.log4j.MongoDbAppender">MongoDbAppender</option>
		        		<option value="MI">-- To manually input --</option>
		        	</select>
		        	<input type="text" id="txtAppenderClass" style="width:90%;display:none;"/><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Cancel" title="Cancel" id="appenderClassCancel" style="cursor:pointer;padding-left:10px;display:none;"/>
		        </td>
		    </tr>
		</table>
		<table id="tblLayoutAdd" summary="">
		    <caption>Layout Create Form</caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		    <tr>
		        <td colspan="2" class="layout">Layout</td>
		    </tr>
		    <tr>
		        <th class="topline">Class</th>
		        <td class="topline">
		        	<select id="selectLayoutClass" style="width:100%">
		        		<option value="org.apache.log4j.PatternLayout">PatternLayout</option>
		        		<option value="MI">-- To manually input --</option>
		        	</select>
		        	<input type="text" id="txtLayoutClass" style="width:90%;display:none;"/><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Cancel" title="Cancel" id="layoutClassCancel" style="cursor:pointer;padding-left:10px;display:none;"/>
		        </td>
		    </tr>
		    <tr>
		        <th class="bottomline">ConversionPattern</th>
		        <td class="bottomline"><input type="text" id="txtConversionPattern" style="width:100%;"/></td>
		    </tr>
		</table>
	</div>
</div>

<div id="editor-select-form" title="Editor Select Form">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption>Editor Select Form"</caption>
		    <colgroup>
		        <col style="width:50%;" />
		        <col style="width:50%;" />
		    </colgroup>
		    <tr>
		        <td class="topline bottomline"><a href="#" id="gui">GUI</a></td>
		        <td class="topline bottomline"><a href="#" id="text">Text</a></td>
		    </tr>
		</table>
	</div>
</div>
</body>
</html>