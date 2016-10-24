<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
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

/**
 *  check result error
 */
function checkError(data) {
	if(data.isFail) {
		alert('check agent status.');
		return true;
	}else{
		return false;
	}
}

$(document).ready(function() {
	
	REQUEST_CONTEXT = '<c:url value="/"/>';
	
	/**
	 * log agent 'Restart' button click event handler 
	 * log agent restart
	 */
	$('.restart-button').click(function(e) {
		var agentId = $(e.target).attr('agentId');
		loading($('body'));
		$.get('<c:url value="/logManager.do?method=restartAgent"/>&agentId=' + agentId, function(data) {
			if(checkError(data)) {
				return;
			}
			alert("agent restart is complete.");
			loadingClose($('body'));
			self.location.reload(true);
		});
	});

	/**
	 * log agent 'Delete' button click event handler
	 * log agent meta info delete
	 */
	$('.delete-button').click(function(e) {
		var agentId = $(e.target).attr('agentId');
		if(confirm('Do you want really to delete \"' + agentId + '\"?')) {
			loading($('body'));
			$.get('<c:url value="/logManager.do?method=deleteAgent"/>&agentId=' + agentId, function(data) {
				loadingClose($('body'));
				self.location.reload(true);
			});
		}
	});

	$('#btnRefresh').click(function(e) {
		self.location.href = '<c:url value="/logManager.do?method=agentList&refresh=true"/>';
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
				<h2>Log Agent Management</h2>
                <div id="innercontents">
                	<div class="list">
                        <table summary="App Name, log4j.xml, file_config, Status List">
                            <caption>App Name, log4j.xml, file-config, Status List</caption>
                            <colgroup>
                                <col style="width:40%;" />
                                <col style="width:10%;" />
                                <col style="width:20%;" />
                                <col style="width:25%;" />
                            </colgroup>
                            <thead>
                                <tr>
                                    <th>Agent ID</th>
                                    <th>Status</th>
                                    <th>Last Update</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="agent" items="${list}">
                                <tr>
                                    <td>${agent.agentId}</td>
                                    <td class="align_center">${agent.statusMessage}</td>
                                    <td><fmt:formatDate value="${agent.lastUpdate}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                    <td class="align_center">
                                    <c:if test="${agent.status == 1}">
	                                    <span class="button tablein small">
	                                        <button class="restart-button" agentId="${agent.agentId}">Restart</button>
	                                    </span>
	                                </c:if>
	                                	<span class="button tablein small">
	                                        <button class="delete-button" agentId="${agent.agentId}">Delete</button>
	                                    </span>
                                    </td>
                                </tr>
                            </c:forEach>                
                            </tbody>
                        </table>
                    </div>
                    
                    <div class="btncontainer_right margin_top5">
                    	<span class="button">
                            <a href="#" id="btnRefresh">Refresh</a>
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
</body>
</html>