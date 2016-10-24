<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Anyframe Log Manager</title>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/layout.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/common.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/logmanager.css'/>"/>

<!-- for jquery -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery-1.6.2.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery/validation/jquery.validate.js'/>"></script>

<!-- jquery ui -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery-ui/jquery-ui-1.8.16.custom.min.js'/>"></script>
<link id='uiTheme' href="<c:url value='/jquery/jquery/jquery-ui/themes/cape/jquery-ui-1.8.16.custom.css'/>" rel="stylesheet" type="text/css" />

<script type="text/javascript">
function fncLogin() {
	if(document.loginForm.userId.value =="" || document.loginForm.password.value==""){
		alert("All fields must be filled out.");
	}
	else{
		document.loginForm.action="<c:url value='/login.do'/>";
	    document.loginForm.submit();
	}
}

function fncKeyPress(event){
	if(event.keyCode==13)
		fncLogin();
	else
		return true;
}

$(document).ready(function() {
	$('#btnAdd').click(function(e) {
		document.accountForm.reset();
		$('#account-detail-form').dialog({width:430,position:['center', 'center']});
		$('#account-detail-form').dialog('open');
		$('#account-detail-form').dialog({
			width:$('#account-detail-form table').width() + 70
		});
	});
	$('#account-detail-form').dialog({
		autoOpen: false,
		width: 430,
		height:'auto',
		modal: true,
		resizable:false,
		buttons: {
			'Save': function() {
				$('#accountForm').validate();
				if(document.accountForm.userId.value =="" || document.accountForm.userName.value =="" ||
							document.accountForm.password.value=="" || document.accountForm.confirmPassword.value==""){
					alert("All fields must be filled out.");
					return false;
				}
				if(document.accountForm.password.value != document.accountForm.confirmPassword.value){
					alert("Your passwords do not match. Please type more carefully.");
					return false;
				}
				$.post('<c:url value="/welcome.do?method=save"/>', 
						{'userId' : document.accountForm.userId.value,
					     'userName' : document.accountForm.userName.value,
						'password' : document.accountForm.password.value, 
						'userType' : $("#userType").val()}, 
						function(data) {
							alert('save success');
							$("#account-detail-form").dialog('close');
							window.location.reload();
						});
			},
			'Cancel': function() {
				$( "#account-detail-form" ).dialog('close');
			}}
	});
});
</script>

</head>

<body class="logging_leftmenu">
<!--[if IE 6]><div id="wrapper" class="ie6fix"><![endif]-->
<!--[if IE 7]><div id="wrapper" class="ie7fix"><![endif]-->
<!--[if gt IE 7]><div id="wrapper" class="iefix"><![endif]-->
<!--[if !IE]><div id="wrapper"><![endif]-->
<div id="wrap">

	<div class="skipnavigation">
  		<a href="#container">Jump up to the contents</a>
	</div><!-- // skipnavigation E -->
	<hr />
	<div id="container_login">
		<div class="logintitleimg">Anyframe Log Manager</div>
	    <hr />
	    <div id="introcontainer_bgbox">
			<div id="introcontainer" class="login">
				<div class="inputbox">
		        	<fieldset>
		            	<legend>Login Input</legend>
		            	<form id="loginForm" name="loginForm" method="post" action="<c:url value='/login.do'/>">
			                <p><label for="userId">User ID :</label><input type="text" id="userId" name="userId"/></p>
			                <p><label for="password">Password :</label><input type="password" id="password" name="password" onkeypress="fncKeyPress(event);"/></p>
			                <p class="inputbox_describe"><c:out value="${rejectMessage}"/></p>
		                </form>
		            </fieldset>
		       </div>
	            <div class="introbtn">
	                <span class="button listout loginbtn">
	                    <a id="btnLogin" href="javascript:fncLogin();">Login</a>
	                </span>
	            </div> 
	            
	            <div class="clear"></div>
	        </div><!-- // introcontainer E -->
	        
	    </div><!-- // introcontainer_bgbox E -->
	    <hr />
	    <div class="copyright">
	    	<c:if test="${isAdminExist==false}">
		       		 <span class="button">
		       			<a id="btnAdd">Admininstrator account does not exist. Register Now!</a>
		       			</span>
		       </c:if>
	    </div>
	    <div class="copyright">Copyright ⓒ <span class="sitename">Anyframe Log Manager</span> All rights reserved.</div>
    </div>
</div><!-- // wrap E -->
<div id="account-detail-form" title="Register Administrator">
	<div class="view poplayer margin_top10">
		<form id="accountForm" name="accountForm">
			<table summary="">
			    <caption>Account Detail Info.</caption>
			    <colgroup>
			        <col style="width:30%;" />
			        <col style="width:60%;" />
			    </colgroup>
			    <tr>
			        <th class="topline">User ID</th>
			        <td class="topline"><input type="text" id="userId" name="userId" class="required"/></td>
			    </tr>
			    <tr>
			        <th>User Name</th>
			        <td><input type="text" name="userName" class="required"/></td>
			    </tr>
			    <tr>
			        <th>Password</th>
			        <td><input type="password" id="password" name="password" class="required"/></td>
			    </tr>
			     <tr>
			        <th>Confirmation Password</th>
			        <td><input type="password" name="confirmPassword" id="confirmPassword" class="required"/></td>
			    </tr>
			    <tr>
			        <th>User Type</th>
			        <td>
			        	<select class="select_search" name="userType" id="userType">
			        		<option value="Administrator" label="Administrator"/>
			        	</select>
			       </td>
			    </tr>
			</table>
		</form>
	</div>
</div>
<!--[if IE]></div><![endif]-->
<!--[if !IE]></div><![endif]--><!--// wrapper E -->
</body>
</html>