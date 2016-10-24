<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
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

<!-- jqGrid -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/jqgrid/i18n/grid.locale-en.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery/jqgrid/jquery.jqGrid.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/jquery/jquery/jqgrid/plugins/grid.setcolumns.js'/>"></script>
<link href="<c:url value='/jquery/jquery/jqgrid/ui.jqgrid.css'/>" rel="stylesheet" type="text/css" />

<!-- quick pager -->
<script type="text/javascript" src="<c:url value='/jquery/jquery/quickpager/quickpager.mod.jquery.js'/>"></script>
<link href="<c:url value='/jquery/jquery/quickpager/pagination.css'/>" rel="stylesheet" type="text/css" />

<!-- logger -->
<script src="<c:url value='/jquery/jquery/logger.js'/>" type="text/javascript"></script>

<script type="text/javascript" src="<c:url value='/logmanager/javascript/InputCalendar.js'/>"></script>

<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery.highlight-3.js'/>"></script>

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


var gridId = 'tblAccountList';
var dialog_mode = "";
$(document).ready(function() {

	$('#divMessageGrid').html('<table id="' + gridId + '"></table><div class="boardNavigation"><div id="pagination" class="pagination"></div><button id="btnPagenationSearch"/></div>');
	$('#' + gridId).jqGrid({
		url : '<c:url value="/account.do?method=list"/>',
		mtype:'POST',
		postData : $('#searchForm').serialize(),
		datatype : 'json',
		colNames : ['<spring:message code="logmanager.account.userid"/>', '<spring:message code="logmanager.account.username"/>', '<spring:message code="logmanager.account.usertype"/>', '<spring:message code="logmanager.account.password"/>'],
		colModel : [{name:'userId', index:'userId', width:30, sortable:false},
					{name:'userName', index:'userName', width:60, sortable:false},
					{name:'userType', index:'userType', width:40, sortable:false},
					{name:'password', index:'password', width:30, sortable:false, hidden:true}],
		jsonReader: {
			repeatitems: false
		},
		viewrecoreds : true,
		autowidth : true,
		height : '100%',
		sortable : false,
		rowNum : $('#pageSize').val(), 
		gridComplete: function() { 
	    }, 
		loadError: function(xhr,st,err) {
			alert(err); 
		},
		loadComplete : function(xhr) {
			
			$('#spanListCount').html($('#' + gridId).getGridParam("records"));
			
			$('#pagination').quickPager({
	    		pageSize: $('#pageSize').val(), 
	    		pageUnit: 10,
	    		pageIndexId: 'pageIndex',
	    		searchButtonId: 'btnPagenationSearch', 
	    		currentPage: $('#' + gridId).getGridParam("page"),
	    		totalCount: $('#' + gridId).getGridParam("records"),
	    		searchUrl: "#"
	    	});
			$('.ui-jqgrid-bdiv').css('overflow-x', 'hidden');
		},
		onSelectRow : function(rowid, status, e) {
			var ret = $("#" + gridId).jqGrid('getRowData',rowid);
			document.accountForm.userId.value = ret.userId;
			document.accountForm.userName.value = ret.userName;
			document.accountForm.password.value = "";
			document.accountForm.confirmPassword.value = "";
			$('#userType').val(ret.userType);
			document.accountForm.userId.readOnly=true;
			
			dialog_mode = "update";
			$(".ui-dialog-buttonpane button:contains('Remove')").show();
			$('#account-detail-form').dialog({width:430,position:['center', 'center']});
			$('#account-detail-form').dialog('open');
			$('#account-detail-form').dialog({
				width:$('#account-detail-form table').width() + 70
			});
		},
		caption : 'Account List'
	});

	$("#btnPagenationSearch").click(function() {
		$('#' + gridId).setGridParam({
			page: $("#pageIndex").val(),
			postData: $('#searchForm').serialize()
		}).trigger("reloadGrid");
	});
	
	/**
	 * 'Search' button click event handler
	 * search form submit
	 */
	$('#btnSearch').click(function(e) {
		$("#pageIndex").val(1);
		$('#' + gridId).setGridParam({
			page: $("#pageIndex").val(),
			postData: $('#searchForm').serialize()
		}).trigger("reloadGrid");
	});
	/* auto click by enter key */
	$('#searchKeyword').keypress(function (e) {
		if (e.which == 13){
			$('#btnSearch').trigger("click");
			return false;
		}
	});

	$('#btnAdd').click(function(e) {
		dialog_mode = "add";
		document.accountForm.reset();
		document.accountForm.userId.readOnly=false;
		 $(".ui-dialog-buttonpane button:contains('Remove')").hide();
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
					alert('<spring:message code="logmanager.account.alert.filledout"/>');
					return false;
				}
				if(document.accountForm.password.value != document.accountForm.confirmPassword.value){
					alert('<spring:message code="logmanager.account.alert.pwdnotmatch"/>');
					return false;
				}
				var url="";
				if(dialog_mode == "add"){
					url = '<c:url value="/account.do?method=create"/>';
					$.get('<c:url value="/account.do?method=checkAccountExist"/>&userId=' + $.trim($('#userId').val()), function(data){
						if(data.isExist) {
							alert('<spring:message code="logmanager.dup.account.userId"/>');
							$('#userId').select();
							return false;
						}else{
							$.post(url, 
									{'userId' : document.accountForm.userId.value,
								     'userName' : document.accountForm.userName.value,
									'password' : document.accountForm.password.value, 
									'userType' : $("#userType").val()}, 
									function(data) {
										alert('save success');
										$( "#account-detail-form" ).dialog('close');
										$('#btnSearch').trigger("click");
									});
						}
					});
				}else if(dialog_mode == "update"){
					url = '<c:url value="/account.do?method=save"/>';
					$.post(url, 
							{'userId' : document.accountForm.userId.value,
						     'userName' : document.accountForm.userName.value,
							'password' : document.accountForm.password.value, 
							'userType' : $("#userType").val()}, 
							function(data) {
								alert('save success');
								$( "#account-detail-form" ).dialog('close');
								$('#btnSearch').trigger("click");
							});
				}
			},
			'Remove': function() {
				var msg = '<spring:message code="logmanager.config.delete"/>';
			    ans = confirm(msg);
			    if (ans) {
			    	var url='<c:url value="/account.do?method=remove"/>';
					$.post(url, 
							{'userId' : document.accountForm.userId.value}, 
							function(data) {
								$( "#account-detail-form" ).dialog('close');
								$('#btnSearch').trigger("click");
							});
			    } else {
			       return false;
			    }
			}}
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
				<h2><spring:message code="logmanager.account.title"/></h2>
                <div id="innercontents">
					<div id="divSearchCondition" class="search_area">
						<form:form name="searchForm" id="searchForm" method="post" target="_self" modelAttribute="search">
						<input type="hidden" id="pageIndex" name="pageIndex"/>
						<input type="hidden" id="pageSize" name="pageSize"/>
						<table summary="Account search table">
							<caption>Search Area</caption>
                            <colgroup>
                                <col style="width:18%;" />
                                <col style="width:32%;" />
                                <col style="width:18%;" />
                                <col style="width:32%;" />
                            </colgroup>
                            <tbody>
								<tr>
									<th class="topline"><spring:message code="logmanager.account.userid"/></th>
									<td class="topline">
										<form:input path="searchKeyword"/>
									</td>
									<th class="topline"><spring:message code="logmanager.account.usertype"/></th>
									<td class="topline">
										<form:select class="select_search" path="searchCondition" items="${searchUserTypeList}"/>
									</td>
								</tr>
                            </tbody>
						</table>
						</form:form>
						<div class="btncontainer_right">
	                        <span class="button tableout">
	                            <button id="btnSearch"><spring:message code="logmanager.button.search"/></button>
	                        </span>
	                    </div>
					</div>
					<!-- <div>[<span id="spanListCount"></span>&nbsp;rows]</div> -->
					<div id="divMessageGrid" class="margin_top0">
					</div>
					<div id="gridBtn"></div>
					<div class="btncontainer_right margin_top5">
						 <span class="button">
	                    	<a id="btnAdd"><spring:message code="logmanager.button.add"/></a>
						</span>
					</div>
				</div><!-- // innercontents E -->
            </div><!-- // contents E -->
        </div><!-- // container E -->
    </div><!-- // container_bgbox E -->

	<hr />
	<div id="footer_wrap">
		<div id="footer"><spring:message code="logmanager.footer.copyright"/></div>
	</div>
</div>
<div id="account-detail-form" title="<spring:message code="logmanager.account.detail.title"/>">
	<div class="view poplayer margin_top10">
		<form:form modelAttribute="account" id="accountForm" name="accountForm">
			<table summary="">
			    <caption><spring:message code="logmanager.account.detail.title"/></caption>
			    <colgroup>
			        <col style="width:30%;" />
			        <col style="width:60%;" />
			    </colgroup>
			    <tr>
			        <th class="topline"><spring:message code="logmanager.account.userid"/></th>
			        <td class="topline"><form:input id="userId" path="userId" cssClass="required"/></td>
			    </tr>
			    <tr>
			        <th><spring:message code="logmanager.account.username"/></th>
			        <td><form:input path="userName" cssClass="required"/></td>
			    </tr>
			    <tr>
			        <th><spring:message code="logmanager.account.password"/></th>
			        <td><form:password path="password" cssClass="required"/></td>
			    </tr>
			     <tr>
			        <th><spring:message code="logmanager.account.confirm.password"/></th>
			        <td><input type="password" name="confirmPassword" id="confirmPassword" class="required"/></td>
			    </tr>
			    <tr>
			        <th><spring:message code="logmanager.account.usertype"/></th>
			        <td>
			        	<form:select cssClass="select_search" path="userType" items="${userTypeList}"/>
			       </td>
			    </tr>
			</table>
		</form:form>
	</div>
</div>
</body>
</html>