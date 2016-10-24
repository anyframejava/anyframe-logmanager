<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Anyframe Log M 1.0.0-SNAPSHOT</title>

<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/layout.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/common.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/logmanager.css'/>"/>

<!-- for jquery -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery-1.6.2.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery/validation/jquery.validate.js'/>"></script>

<!-- jquery ui -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery-ui/jquery-ui-1.8.16.custom.min.js'/>"></script>
<link id='uiTheme' href="<c:url value='/jquery/jquery/jquery-ui/themes/cape/jquery-ui-1.8.16.custom.css'/>" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="<c:url value='/logmanager/javascript/InputCalendar.js'/>"></script>

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
var MONITOR_LEVEL_ADMIN_ONLY = 0;
var MONITOR_LEVEL_DEV_VISIBLE = 1;

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
		$('#txtLog4jXmlPath').val('');
		$('#tblLogger tr').remove();
		$('#tblAppender tr').remove();
		hiddenId;
		
		$('#app-detail-form').dialog('open');
	});

	/**
	 * log application 'Delete' button click event handler
	 * log application delete
	 */
	$('.delete-button').click(function(e) {
		var appName = $(e.target).attr('appName');
		if(confirm('Do you want really to delete \"' + appName + '\"?')) {
			$.get('<c:url value="/logManager.do?method=deleteApplication"/>&appName=' + appName, function(data) {
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
		var status = $(e.target).attr('status');
		$.get('<c:url value="/logManager.do?method=reloadApplication"/>&status=' + status + '&appName=' + appName, function(data) {
			self.location.reload(true);
		});
	});

	/**
	 * list item click event handler
	 * log application info load and open log application edit form
	 */
	$('.app-name').click(function(e) {
		$.get('<c:url value="/logManager.do?method=viewAppDetail"/>&id=' + $(this).attr('id'), function(data){
			
			var app = data.app;
			var loggers = app.loggers;
			var appenders = app.appenders;
			var root = data.app.root;
			
			$('#txtAppName').val(app.appName);
			$('#txtAppName').attr('disabled', 'disabled');
			$('#txtLog4jXmlPath').val(app.log4jXmlPath);

			$('#tblLogger tr').remove();
			$('#tblAppender tr').remove();
			$('#hiddenId').val(app.id);
			
			var html = '<tr><th>';
            html += '<label for="root">root</label></th><td>';
            html += '<select id="root" class="select_search">';
            html += '<option value="DEBUG">DEBUG</option>';
            html += '<option value="INFO">INFO</option>';
            html += '<option value="WARN">WARN</option>';
            html += '<option value="ERROR">ERROR</option>';
            html += '<option value="FATAL">FATAL</option>';
            html += '<option value="OFF">OFF</option>';
            html += '</select>';
            html += '</td></tr>';
            $('#tblLogger').append(html);
            $('#root').val(root.level);
            
			for(var i=0;i<loggers.length;i++) {
				var id = (loggers[i].name).replace(/\./g, '_');
				var html = '<tr><th>';
	            html += '<label for="' + id + '">' + loggers[i].name + '</label></th><td>';
	            html += '<select id="' + id + '" class="select_search">';
	            html += '<option value="DEBUG">DEBUG</option>';
	            html += '<option value="INFO">INFO</option>';
	            html += '<option value="WARN">WARN</option>';
	            html += '<option value="ERROR">ERROR</option>';
	            html += '<option value="FATAL">FATAL</option>';
	            html += '<option value="OFF">OFF</option>';
	            html += '</select>';
	            html += '</td></tr>';
	            $('#tblLogger').append(html);
	            $('#' + id).val(loggers[i].level);
			}
			for(var i=0;i<appenders.length;i++) {
				var html = '<tr><th>';
				var id = (appenders[i].name).replace(/\./g, '_');
	            html += '<label for="' + id + '">' + appenders[i].name + '</label></th><td>';
	            html += '<select id="' + id + '" class="select_search">';
	            html += '<option value="' + MONITOR_LEVEL_ADMIN_ONLY + '">Admin Only</option>';
	            html += '<option value="' + MONITOR_LEVEL_DEV_VISIBLE + '">Developer Visible</option>';
	            html += '</select>';
	            html += '</td></tr>';
	            $('#tblAppender').append(html);
	            if(appenders[i].params.length > 0) {
		            var isMonitorLevel = false;
		            for(var j=0;j<appenders[i].params.length;j++) {
			            var param = appenders[i].params[j];
			            if(param.name == 'monitorLevel') {
			            	$('#' + id).val(param.value);
			            	isMonitorLevel = true;
			            	break;
			            }
		            }
		            if(!isMonitorLevel) {
		            	$('#' + id).val(MONITOR_LEVEL_ADMIN_ONLY);
		            }
	            }
			}
			$('#app-detail-form').dialog('open');
			$('#app-detail-form').dialog({
				width:$('#tblLogger').width() + 70,
				position:['center', 100]});
		});
	});

	/**
	 * 'Load' button click event handler
	 * log4j.xml load & parser
	 */
	$('#btnLog4jXmlLoad').click(function(e){
		if($('#txtLog4jXmlPath').val() == '') return false;
		
		$.get('<c:url value="/logManager.do?method=loadLog4jXml"/>&log4jXmlPath=' + $('#txtLog4jXmlPath').val(), function(data){
			//alert(data.app.appenders.length + '|' + data.app.loggers.length);
			var loggers = data.app.loggers;
			var appenders = data.app.appenders;
			var root = data.app.root;
			var html = '<tr><th>';
            html += '<label for="root">root</label></th><td>';
            html += '<select id="root" class="select_search">';
            html += '<option value="DEBUG">DEBUG</option>';
            html += '<option value="INFO">INFO</option>';
            html += '<option value="WARN">WARN</option>';
            html += '<option value="ERROR">ERROR</option>';
            html += '<option value="FATAL">FATAL</option>';
            html += '<option value="OFF">OFF</option>';
            html += '</select>';
            html += '</td></tr>';
            $('#tblLogger tr').remove();
            $('#tblLogger').append(html);
            $('#root').val(root.level);
			for(var i=0;i<loggers.length;i++) {
				var id = (loggers[i].name).replace(/\./g, '_');
				var html = '<tr><th>';
	            html += '<label for="' + id + '">' + loggers[i].name + '</label></th><td>';
	            html += '<select id="' + id + '" class="select_search">';
	            html += '<option value="DEBUG">DEBUG</option>';
	            html += '<option value="INFO">INFO</option>';
	            html += '<option value="WARN">WARN</option>';
	            html += '<option value="ERROR">ERROR</option>';
	            html += '<option value="FATAL">FATAL</option>';
	            html += '<option value="OFF">OFF</option>';
	            html += '</select>';
	            html += '</td></tr>';
	            $('#tblLogger').append(html);
	            $('#' + id).val(loggers[i].level);
			}
			$('#tblAppender tr').remove();
			for(var i=0;i<appenders.length;i++) {
				var html = '<tr><th>';
				var id = (appenders[i].name).replace(/\./g, '_');
	            html += '<label for="' + id + '">' + appenders[i].name + '</label></th><td>';
	            html += '<select id="' + id + '" class="select_search">';
	            html += '<option value="' + MONITOR_LEVEL_ADMIN_ONLY + '">Admin Only</option>';
	            html += '<option value="' + MONITOR_LEVEL_DEV_VISIBLE + '">Developer Visible</option>';
	            html += '</select>';
	            html += '</td></tr>';
	            $('#tblAppender').append(html);
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
			}
			$('#app-detail-form').dialog({
				width:$('#tblLogger').width() + 70,
				position:['center', 100]});
		});
	});

	/**
	 * 'Save' button click event handler
	 * db save and log4j.xml apply
	 */
	$('#btnSave').click(function(e) {

		if($('#hiddenId').val() == '') {
			$.get('<c:url value="/logManager.do?method=checkApplicationExist"/>&appName=' + $('#txtAppName').val().trim(), function(data){
				if(data.isExist) {
					alert('<spring:message code="logmanager.dup.logapplication.name"/>');
					$('#txtAppName').select();
					return false;
				}else{
					save();
				}
			});
		}else{
			save();
		}
		
	});

	/**
	 * log application edit form dialog define
	 */
	$( "#app-detail-form" ).dialog({
		autoOpen: false,
		width: 430,
		height:"auto",
		modal: true,
		resizable:true
	});
});

function save() {

	var loggersArray = new Array($('#tblLogger select').length);
	var appendersArray = new Array($('#tblAppender select').length);
	
	$('#tblLogger select').each(function(i, element) {
		//loggersParam += '&logger=' + $(element).attr('id').replace(/_/g, '.') + ',' + $(element).val();
		loggersArray[i] = $(element).attr('id').replace(/_/g, '.') + '|' + $(element).val();
		if(i == $('#tblLogger select').length - 1) {
			//alert(loggersArray);
			if($('#tblAppender select').length > 0) {
				$('#tblAppender select').each(function(j, element2) {
					appendersArray[j] = $(element2).attr('id').replace(/_/g, '.') + '|' + $(element2).val();
					if(j == $('#tblAppender select').length - 1) {
						//alert(appendersArray);
						$.post('<c:url value="/logManager.do?method=saveLogApplication"/>', 
								{'id' : $('#hiddenId').val(),
								'loggers[]' : loggersArray, 
								'appenders[]' : appendersArray, 
								'appName' : $('#txtAppName').val().trim(), 
								'log4jXmlPath' : $('#txtLog4jXmlPath').val().trim()}, 
								function(data) {
									alert('save success');
									$( "#app-detail-form" ).dialog('close');
									self.location.reload(true);
								});
					}
				});
			}else{
				$.post('<c:url value="/logManager.do?method=saveLogApplication"/>', 
						{'id' : $('#hiddenId').val(),
						'loggers' : loggersArray, 
						'appenders' : appendersArray, 
						'appName' : $('#txtAppName').val().trim(), 
						'log4jXmlPath' : $('#txtLog4jXmlPath').val().trim()}, 
						function(data) {
							alert('save success');
							$( "#app-detail-form" ).dialog('close');
							self.location.reload(true);
						});
			}
		}
	});
}
//-->
</script>
</head>
<body class="logging_leftmenu">
<!--[if IE 6]><div id="wrapper" class="ie6fix"><![endif]-->
<!--[if IE 7]><div id="wrapper" class="ie7fix"><![endif]-->
<!--[if gt IE 7]><div id="wrapper" class="iefix"><![endif]-->
<!--[if !IE]><div id="wrapper"><![endif]-->
<div id="wrap">
	<div class="skipnavigation">
		<a href="#contents">Jump up to the contents</a>
	</div>
	<hr />
	   
	<div id="header">
		<h1><a href="#"><img src="<c:url value="/logmanager/images/toplogo.jpg"/>" alt="Log Manager"/></a></h1>
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
                    <li><a href="<c:url value="/logManager.do?method=appList"/>">Log Application Management</a></li>
                    <li><a href="<c:url value="/account.do?method=view"/>">Account Management</a></li>
                </ul>
			</div>
			<hr/>
			
			<div id="contents">
				<h2>Log Application Management</h2>
                <div id="innercontents">
                	<div class="list">
                        <table summary="App Name, log4j.xml, file_config, Status List">
                            <caption>App Name, log4j.xml, file-config, Status List</caption>
                            <colgroup>
                                <col style="width:20%;" />
                                <col style="width:47%;" />
                                <col style="width:10%;" />
                                <col style="width:23%;" />
                            </colgroup>
                            <thead>
                                <tr>
                                    <th>App Name</th>
                                    <th>log4j.xml</th>
                                    <th>Status</th>
                                    <th>&nbsp;</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${list}">
                                <tr>
                                    <td><a href="#" id="${app.id}" class="app-name">${app.appName}</a></td>
                                    <td>${app.log4jXmlPath}</td>
                                    <td class="align_center">${app.statusMessage}</td>
                                    <td class="align_center">
	                                    <span class="button tablein small">
	                                        <button class="reload-button" appName="${app.appName}" status="${app.status}">
	                                        <c:if test="${app.status == 0}">Inactive</c:if>
	                                        <c:if test="${app.status == 1}">Active</c:if>
	                                        <c:if test="${app.status == 2}">Reload</c:if>
	                                        </button>
	                                    </span>
	                                    <span class="button tablein small">
	                                        <button class="delete-button" appName="${app.appName}">Delete</button>
	                                    </span>
                                    </td>
                                </tr>
                            </c:forEach>                
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="btncontainer_center margin_top5">
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
		<div id="footer">
			Copyright ⓒ <span class="sitename">Anyframe Log Manager</span> All rights reserved.
		</div>
	</div>
</div>
<!--[if IE]></div><![endif]-->
<!--[if !IE]></div><![endif]--><!--// wrapper E -->

<div id="app-detail-form" title="Log Application Edit Form">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption>Log Application Edit Form</caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:60%;" />
		    </colgroup>
		    <tr>
		        <th class="topline">App. Name</th>
		        <td class="topline"><input type="text" id="txtAppName" /><input type="hidden" id="hiddenId" /></td>
		    </tr>
		    <tr>
		        <th>log4j.xml Path</th>
		        <td>
		        	<label for="txtLog4jXmlPath"><input type="text" id="txtLog4jXmlPath" /></label>&nbsp;<span class="button tablein small">
	                    <button id="btnLog4jXmlLoad">Load</button>
	                </span>
	            </td>
		    </tr>
		    <tr>
		        <td colspan="2" class="bottomline">
		            <div class="view_innerdiv">
		                <p>Logger Management</p>
		                <table summary="Logger List Table" id="tblLogger">
	                        <caption>Logger Management</caption>
	                        <colgroup>
	                            <col style="" />
	                            <col style="width:150px;" />
	                        </colgroup>
	                    </table>
		            </div><!-- // view_innerdiv E -->
		            <div class="view_innerdiv">
		                <p>Appender Management</p>
		                <table summary="Logger List Table" id="tblAppender">
	                        <caption>Logger Management</caption>
	                        <colgroup>
	                            <col style="" />
	                            <col style="width:150px;" />
	                        </colgroup>
	                    </table>
		            </div><!-- // view_innerdiv E -->
		        </td>
		    </tr>
		</table>
		<div class="btncontainer_right margin_top5">
		    <span class="button tableout large">
		        <button id="btnSave">Save</button>
		    </span>
		</div><!-- // list_underbtn_right E -->
	</div>
</div>
</body>
</html>