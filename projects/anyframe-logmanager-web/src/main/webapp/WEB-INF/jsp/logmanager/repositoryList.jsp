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

/**
 *  check result error
 */
function checkError(data) {
	if(data.isFail) {
		alert('<spring:message code="logmanager.repository.alert.status"/>');
		return true;
	}else{
		return false;
	}
}

// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

var MONITOR_LEVEL_ADMIN_ONLY = <%=LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY%>;
var MONITOR_LEVEL_DEV_VISIBLE = <%=LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE%>;

var regExp = /[^a-zA-Z0-9_]/;

/**
 * display log repository info
 */
function displayLogRepository(repository){
	$('#hiddenId').val(repository.id);
	$('#txtRepositoryName').val(repository.repositoryName);
	$('#selectMonitorLevel').val(repository.monitorLevel);
	if(repository.active == true) {
		$('#chkActive').attr('checked', 'checked');	
	}else{
		$('#chkActive').removeAttr('checked');
	}
}

/**
 * delete event handler
 */
function deleteEventHandler(id, callback) {
	var url = '<c:url value="/logManager.do?method=deleteLogRepository"/>';
	
	loading($('body'));
	$.get(url, {
		'id' : id
	}, function(data){
		if(checkError(data)) {
			loadingClose($('body'));
			return;
		}else{
			alert('delete is successful.');
			loadingClose($('body'));
			callback();	
		}
	}).error(function(jqXHR, textStatus, errorThrown) {
        alert("Error: " + errorThrown);
        loadingClose($('body'));
	});
}

/**
 * save event handler
 */
function saveEventHandler(callback) {
	var url = '<c:url value="/logManager.do?method=saveLogRepository"/>';
	
	if($('#txtRepositoryName').val() == '') {
		alert('<spring:message code="logmanager.repository.alert.appname"/>');
		$('#txtRepositoryName').select();
		return;
	}
	
	if(regExp.test($('#txtRepositoryName').val())) {
		alert('<spring:message code="logmanager.repository.alert.alphanumeric"/>');
		$('#txtRepositoryName').select();
		return;
	}
	
	loading($('body'));
	$.post(url, {
		'id' : $('#hiddenId').val(),
		'repositoryName' : $('#txtRepositoryName').val(),
		'monitorLevel' : $('#selectMonitorLevel').val(),
		'active' : $('#chkActive').attr('checked') ? true : false
	}, function(data){
		if(checkError(data)) {
			loadingClose($('body'));
			return;
		}else{
			alert('save is successful.');
			loadingClose($('body'));
			callback();	
		}
	}).error(function(jqXHR, textStatus, errorThrown) {
        alert("Error: " + errorThrown);
        loadingClose($('body'));
	});
}

/**
 * form initialize
 */
function initEditForm() {
	$('#txtRepositoryName').val('');
	$('#selectMonitorLevel').val(MONITOR_LEVEL_ADMIN_ONLY);
	$('#chkActive').attr('checked', 'checked');
}


$(document).ready(function() {
	
	REQUEST_CONTEXT = '<%=request.getContextPath()%>';

	/**
	 * log repository 'Create' button click event handler
	 */
	$('#btnCreate').click(function(e) {
		initEditForm();
		$("#repo-edit-form").dialog('open');
	});
	
	/**
	 * log repository 'Delete' button click event handler
	 * log repository meta info delete
	 */
	$('.delete-button').click(function(e){
		var id = $(e.target).attr('repoId');
		if(confirm('<spring:message code="logmanager.config.delete"/>')) {
			deleteEventHandler(id, function(){
				self.location.reload(true);
			});
		}
	});
	
	/**
	 * #tblSetLogCollection row select event handler
	 **/
	$('.list td:not(.td-btn)').click(function(e){
		$('.list td.selected').removeClass('selected');
		$(this).parent('tr').children('td').addClass('selected');
		var repoId = $(this).parent('tr').attr('repoId');
		var url = '<c:url value="logManager.do?method=getLogRepository"/>&id=' + repoId;
		$.get(url, function(data){
			displayLogRepository(data.repository);
			$('#repo-edit-form').dialog('open');
		});
	});
	
	/**
	 * gui type edit form dialog define
	 **/
	$("#repo-edit-form").dialog({
		autoOpen: false,
		position : ['center', 120],
		width: 400,
		height: "auto",
		modal: true,
		resizable:true,
		buttons : {
			'Save' : function(){
				saveEventHandler(function(){
					$('#repo-edit-form').dialog('close');
					self.location.reload(true);
				});
			},
			'Cancel' : function(){
				$(this).dialog('close');
			}
		}
	});	
	
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
				<h2><spring:message code="logmanager.repository.title"/></h2>
                <div id="innercontents">
                	<div class="list">
                        <table summary="Repository Name, Monitor Level, Active">
                            <caption><spring:message code="logmanager.repository.name"/>, <spring:message code="logmanager.repository.monitor.level"/>, <spring:message code="logmanager.repository.active"/></caption>
                            <colgroup>
                                <col style="width:50%;" />
                                <col style="width:20%;" />
                                <col style="width:15%;" />
                                <col style="width:15%;" />
                            </colgroup>
                            <thead>
                                <tr>
                                    <th><spring:message code="logmanager.repository.name"/></th>
                                    <th><spring:message code="logmanager.repository.monitor.level"/></th>
                                    <th><spring:message code="logmanager.repository.active"/></th>
                                    <th>&nbsp;</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="repo" items="${repositoryList}">
                                <tr repoId="${repo.id}">
                                    <td>${repo.repositoryName}</td>
                                    <td class="align_center">
<c:if test="${repo.monitorLevel == 0}">
   										<spring:message code="logmanager.repository.admin"/>
</c:if>
<c:if test="${repo.monitorLevel == 1}">
   										<spring:message code="logmanager.repository.dev"/>
</c:if>
                                    </td>
                                    <td class="align_center">${repo.active}</td>
                                    <td class="align_center td-btn">
	                                	<span class="button tablein small">
	                                        <button type="button"  class="delete-button" repoId="${repo.id}"><spring:message code="logmanager.logapplication.button.delete"/></button>
	                                    </span>
                                    </td>
                                </tr>
                            </c:forEach>                
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="btncontainer_right margin_top5">
                    	<span class="button">
                            <a href="#" id="btnCreate"><spring:message code="logmanager.logapplication.button.create"/></a>
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

<div id="repo-edit-form" title="<spring:message code="logmanager.repository.edit.title"/>">
	<div class="view poplayer margin_top10">
		<table id="tblRepoEdit" summary="">
		    <caption><spring:message code="logmanager.repository.edit.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:70%;" />
		    </colgroup>
		     <tr>
		        <th class="topline"><spring:message code="logmanager.repository.type"/></th>
		        <td class="topline"><select disabled><option value="mongodb">MongoDB</option></select></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.repository.name"/></th>
		        <td><input type="text" id="txtRepositoryName"/><input type="hidden" id="hiddenId"/></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.repository.monitor.level"/></th>
		        <td>
		        	<select id="selectMonitorLevel" class="monitor-level">
			            <option value="<%=LogManagerConstant.MONITOR_LEVEL_ADMIN_ONLY%>"><spring:message code="logmanager.repository.admin"/></option>
			            <option value="<%=LogManagerConstant.MONITOR_LEVEL_DEV_VISIBLE%>"><spring:message code="logmanager.repository.dev"/></option>
		        	</select>
		        	<input type="text" id="txtAppenderClass" style="width:90%;display:none;"/><img src="<c:url value="/logmanager/images/btn_x_i.gif"/>" alt="Cancel" title="Cancel" id="appenderClassCancel" style="cursor:pointer;padding-left:10px;display:none;"/>
		        </td>
		    </tr>
		    <tr>
		        <th class="bottomline">&nbsp;</th>
		        <td class="bottomline">
		        	<input type="checkbox" id="chkActive" name="active" value="true" checked/>&nbsp;<label for="chkActive"><spring:message code="logmanager.repository.active"/></label>
		        </td>
		    </tr>
		</table>
	</div>
</div>

<!--[if IE]></div><![endif]-->
<!--[if !IE]></div><![endif]--><!--// wrapper E -->
</body>
</html>