<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ page import="org.anyframe.logmanager.LogManagerConstant" %>

<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><spring:message code="logmanager.application.title"/></title>

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

<script type="text/javascript" src="<c:url value='/logmanager/javascript/CommonScript.js'/>"></script>
<script type="text/javascript" src="<c:url value='/logmanager/javascript/InputCalendar.js'/>"></script>
<script type="text/javascript" src="<c:url value='/logmanager/javascript/logmanager.js'/>"></script>

<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery.highlight-3.js'/>"></script>

<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/layout.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/common.css'/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value='/logmanager/css/logmanager.css'/>"/>

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
var gridId = 'tblLogList';
var initAppName = '<c:out value="${param.appName}"/>';
var DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss,SSS';

/**
 * set log level style
 */
function setLogLevelStyle(rowId, tv, rawObject, cm, rdata) {
   	return 'class="' + cm.classes + ' ' + rawObject.level + '"';
}

$(document).ready(function() {

	$('#divMessageGrid').html('<table id="' + gridId + '"></table><div class="boardNavigation"><div id="pagination" class="pagination"></div><button id="btnPagenationSearch"/></div>');
	$('#' + gridId).jqGrid({
		url : '<c:url value="/logManager.do?method=analysis4grid"/>',
		mtype : 'POST',
		postData : $('#searchForm').serialize(),
		datatype : 'json',
		colNames : ['<spring:message code="logmanager.analysis.level"/>', '<spring:message code="logmanager.analysis.timestamp"/>', 
		            '<spring:message code="logmanager.analysis.clientip"/>', '<spring:message code="logmanager.analysis.userid"/>', 
		            '<spring:message code="logmanager.analysis.classname"/>', '<spring:message code="logmanager.analysis.methodname"/>', 
		            '<spring:message code="logmanager.analysis.line"/>', '<spring:message code="logmanager.analysis.thread"/>', 
		            '<spring:message code="logmanager.analysis.message"/>', '<spring:message code="logmanager.analysis.id"/>'],
		colModel : [{name:'level', index:'level', width:30, align:'center', sortable:false, classes:'level',cellattr: setLogLevelStyle},
					{name:'timestamp', index:'timestamp', width:80, sortable:false, classes:'timestamp',cellattr: setLogLevelStyle},
					{name:'clientIp', index:'clientIp', width:40, sortable:false, classes:'client-ip',cellattr: setLogLevelStyle, hidden:true},
					{name:'userId', index:'userId', width:30, sortable:false, classes:'user-id',cellattr: setLogLevelStyle, hidden:true},
					{name:'className', index:'className', width:40, sortable:false, classes:'class-name',cellattr: setLogLevelStyle, hidden:true},
					{name:'methodName', index:'methodName', width:40, sortable:false, classes:'method-name',cellattr: setLogLevelStyle, hidden:true},
					{name:'lineNumber', index:'lineNumber', width:15, align:'center', sortable:false, classes:'line-number',cellattr: setLogLevelStyle, hidden:true},
					{name:'thread', index:'thread', width:30, sortable:false, classes:'thread',cellattr: setLogLevelStyle, hidden:true},
					{name:'message', index:'message', width:300, sortable:false, classes:'message',cellattr: setLogLevelStyle},
					{name:'_id', index:'_id', width:300, sortable:false, classes:'_id',cellattr: setLogLevelStyle, hidden:true}],
		jsonReader: {
			repeatitems: false
		},
		viewrecoreds : true,
		autowidth : true,
		//autoencode:true,
		height : '100%',
		sortable : false,
		rowNum : $('#pageSize').val(), 
		gridComplete: function() { 
			var ids = $("#" + gridId).jqGrid('getDataIDs');
			for(var i=0;i < ids.length;i++){
				var ret = $("#" + gridId).jqGrid('getRowData',ids[i]);
				$("#" + gridId).jqGrid('setRowData',ids[i],{'message': '<pre>' + ret.message.escapeHTML() + '</pre>', 'timestamp': ret.timestamp.dateFormat(DATE_FORMAT)});
			}
	    }, 
		loadError: function(xhr,st,err) {
			//alert(err);
		},
		loadComplete : function(xhr) {
			// search result highlight effect : app name
			$('#tblLogList .app-name').highlight($('#selectAppName').val());

			// search result highlight effect : log level
			if($('#selectLogLevel').val() != '') {
				$('#tblLogList .level').highlight($('#selectLogLevel').val());
			}
			
			// search result highlight effect : client ip
			if($('#inputClientIp').val() != '') {
				$('#tblLogList .client-ip').highlight($('#inputClientIp').val());
			}

			// search result highlight effect : user id
			if($('#inputUserId').val() != '') {
				$('#tblLogList .user-id').highlight($('#inputUserId').val());
			}

			// search result highlight effect : class name
			if($('#inputClassName').val() != '') {
				$('#tblLogList .class-name').highlight($('#inputClassName').val());
			}
			
			// search result highlight effect : method name
			if($('#inputMethodName').val() != '') {
				$('#tblLogList .method-name').highlight($('#inputMethodName').val());
			}
			
			// search result highlight effect : message text
			if($('#inputMessageText').val() != '') {
				var a = $('#inputMessageText').val().split(' ');
				for(var i=0;i<a.length;i++) {
					$('#tblLogList .message').highlight(a[i]);
				}
			}
			
			$('span.spanListCount').html($('#' + gridId).getGridParam("records"));
			
			$('#pagination').quickPager({
	    		pageSize: $('#pageSize').val(), //$(gridId).getGridParam("total")
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
			var id = ret._id;
			var url = '<c:url value="/logManager.do?method=getLogData"/>&id=' + id + '&repositoryName=' + $('#selectRepository').val();
			$.get(url, function(data){
				
				$('#log-detail-form tbody tr').hide();
				
				$('#span-level').html(data.log.level);
				$('#span-level').parent().parent('tr').show();
				
				$('#span-timestamp').html(data.log.timestamp.dateFormat(DATE_FORMAT));
				$('#span-timestamp').parent().parent('tr').show();
				
				if(data.log.clientIp != null) {
					$('#span-clientIp').html(data.log.clientIp);
					$('#span-clientIp').parent().parent('tr').show();
				}
				
				if(data.log.userId != null) {
					$('#span-userId').html(data.log.userId);
					$('#span-userId').parent().parent('tr').show();
				}
				
				if(data.log.className != null) {
					$('#span-className').html(data.log.className);
					$('#span-className').parent().parent('tr').show();
				}
				
				if(data.log.methodName != null) {
					$('#span-methodName').html(data.log.methodName);
					$('#span-methodName').parent().parent('tr').show();
				}
				
				if(data.log.lineNumber != null) {
					$('#span-lineNumber').html(data.log.lineNumber);
					$('#span-lineNumber').parent().parent('tr').show();
				}
				
				if(data.log.thread != null) {
					$('#span-thread').html(data.log.thread);
					$('#span-thread').parent().parent('tr').show();
				}
				
				for(var key in data.log) {
					if(key != '_id' && key != 'level' && key != 'timestamp' && key != 'className' 
							&& key != 'methodName' && key != 'lineNumber' && key != 'thread' && key != 'message') {
						if(data.log[key] != null) {
							if($('#span-' + key).length > 0) {
								$('#span-' + key).html(data.log[key]);
								$('#span-' + key).parent().parent('tr').show();
							}else{
								$('#span-thread').parent().parent('tr').after('<tr><th>' + key + '</th><td><span id="span-' + key + '">' + data.log[key] + '</span></td></tr>');	
							}
						}
					}
				}
				
				$('#span-message').html('<pre>' + data.log.message.escapeHTML() + '</pre>');
				$('#span-message').parent().parent('tr').show();
				
				$('#log-detail-form').dialog({width:430,position:['center', 'center']});
				$('#log-detail-form').dialog('open');
				$('#log-detail-form').dialog({
					width:$('#log-detail-form table').width() + 70
				});	
			});
			
		},
		caption : '<spring:message code="logmanager.analysis.grid.title"/>'
	});

	$("#btnPagenationSearch").click(function() {
		if($('#selectAppName').val() != null) {
			$('#' + gridId).setGridParam({
				page: $("#pageIndex").val(),
				postData: $('#searchForm').serialize()
			}).trigger("reloadGrid");
		}
	});
	
	/**
	 * 'Search' button click event handler
	 * search form submit
	 */
	$('#btnSearch').click(function(e) {
		if($('#selectAppName').val() != null) {
			$("#pageIndex").val(1);
			$('#' + gridId).setGridParam({
				page: $("#pageIndex").val(),
				postData: $('#searchForm').serialize()
			}).trigger("reloadGrid");
		}
	});

	/**
	 * 'AdvancedOptions' check box click event handler
	 */
	$('#checkboxAdvancedOptions').click(function(e) {
		$('.advanced-form').val('');
		if($('#checkboxAdvancedOptions').is(':checked')) {
			$('tr.advanced-option,td.advanced-option').show();	
		}else{
			$('tr.advanced-option,td.advanced-option').hide();
		}
		
	});

	/**
	 * 'Export' button click event handler
	 */
	$('a.btn-export').click(function(e) {
		var pos = $(e.target).offset();
		$('#export-format-form').dialog({position:[pos.left-200, pos.top]});
		$("#export-format-form").dialog('open');
	});

	/**
	 * 'Text Format' item click event handler
	 */
	$('#txtFormat').click(function(e) {
		if($('#selectAppName').val() != null) {
			$('#searchForm').attr('target', 'exportForm');
			$('#searchForm').attr('action', '<c:url value="/logManager.do?method=txtExport"/>');
			$('#searchForm').submit();
			$("#export-format-form").dialog('close');
		}
	});

	/**
	 * 'Excel Format' item click event handler
	 */
	$('#xlsFormat').click(function(e) {
		if($('#selectAppName').val() != null) {
			$('#searchForm').attr('target', 'exportForm');
			$('#searchForm').attr('action', '<c:url value="/logManager.do?method=xlsExport"/>');
			$('#searchForm').submit();
			$("#export-format-form").dialog('close');
		}
	});

	/**
	 * 'rdo1Hour' radio button click event handler
	 */
	$('#rdo1Hour').click(function(e) {
		$('.from').attr('disabled', false);
		$('#checkboxUseFromDate').attr('checked', true);
		$('#checkboxUseToDate').attr('checked', false);
		var aDate = getTime(1).split(','); 
		$('#textFromDate').val(aDate[0]);
		$('#selectFromHour').val(aDate[1].zf(2));
	});

	/**
	 * 'rdo3Hour' radio button click event handler
	 */
	$('#rdo3Hour').click(function(e) {
		$('.from').attr('disabled', false);
		$('#checkboxUseFromDate').attr('checked', true);
		$('#checkboxUseToDate').attr('checked', false);
		var aDate = getTime(3).split(','); 
		$('#textFromDate').val(aDate[0]);
		$('#selectFromHour').val(aDate[1].zf(2));
	});

	/**
	 * 'rdo1Day' radio button click event handler
	 */
	$('#rdo1Day').click(function(e) {
		$('.from').attr('disabled', false);
		$('#checkboxUseFromDate').attr('checked', true);
		$('#checkboxUseToDate').attr('checked', false);
		$('#selectFromHour').val('01');
		$('#textFromDate').val(getDate(1));
	});


	/**
	 * 'rdo1Week' radio button click event handler
	 */
	$('#rdo1Week').click(function(e) {
		$('.from').attr('disabled', false);
		$('#checkboxUseFromDate').attr('checked', true);
		$('#checkboxUseToDate').attr('checked', false);
		$('#selectFromHour').val('01');
		$('#textFromDate').val(getDate(7));
	});

	/**
	 * 'rdo1Month' radio button click event handler
	 */
	$('#rdo1Month').click(function(e) {
		$('.from').attr('disabled', false);
		$('#checkboxUseFromDate').attr('checked', true);
		$('#checkboxUseToDate').attr('checked', false);
		$('#selectFromHour').val('01');
		$('#textFromDate').val(getDate(30));
	});

	/**
	 * 'checkboxUseFromDate' check box click event handler
	 */
	$('#checkboxUseFromDate').click(function(e) {
		if($('#checkboxUseFromDate').is(':checked')) {
			$('.from').attr('disabled', false);
		}else{
			$('.from').attr('disabled', true);
		}
	});

	/**
	 * 'checkboxUseToDate' check box click event handler
	 */
	$('#checkboxUseToDate').click(function(e) {
		if($('#checkboxUseToDate').is(':checked')) {
			$('.to').attr('disabled', false);
		}else{
			$('.to').attr('disabled', true);
		}
	});

	/**
	 * calendar icon click event handler
	 */
	$('a.calendar-icon').click(function(e){
		var t = document.getElementById($(e.currentTarget).attr('target'));
		if($(t).attr('disabled')) return false;
		else popUpCalendar(t, 'yyyy-mm-dd');	
	});

	/**
	 * switch to text style search
	 **/
	$('#textStyle').click(function(e){
		$('#searchForm').attr('target', '_self');
		$('#searchForm').attr('action', '<c:url value="/logManager.do?method=analysisForm"/>');
		$('#searchForm').submit();
	});

	// -----------------------------------------------------------------------------------
	//  init value setting
	// -----------------------------------------------------------------------------------
	
	if($('#checkboxAdvancedOptions').is(':checked')) {
		$('tr.advanced-option,td.advanced-option').show();
	}else{
		$('tr.advanced-option,td.advanced-option').hide();
	}

	// duration form init state setting
	if($('#checkboxUseFromDate').is(':checked')) {
		$('.from').attr('disabled', false);
	}else{
		$('.from').attr('disabled', true);
	}

	if($('#checkboxUseToDate').is(':checked')) {
		$('.to').attr('disabled', false);
	}else{
		$('.to').attr('disabled', true);
	}
	
	/**
	 * export format select form dialog define
	 */
	$("#export-format-form").dialog({
		autoOpen: false,
		width: 430,
		height:100,
		modal: true,
		resizable:false
	});

	/**
	 * log daetail form dialog define
	 */
	$('#log-detail-form').dialog({
		autoOpen: false,
		width: 430,
		height:'auto',
		modal: true,
		resizable:false,
		buttons: {
			'Close': function() {
				$('#log-detail-form').dialog('close');
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
<div id="wrap">
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
                     <li><a href="<c:url value="/logManager.do?method=analysis4gridForm"/>"><spring:message code="logmanager.analysis.title"/></a></li>
				<c:if test="${sessionScope.loginAccount.userType=='Administrator'}">
					<li><a href="<c:url value="/logManager.do?method=agentList"/>"><spring:message code="logmanager.agent.title"/></a></li>
					<li><a href="<c:url value="/logManager.do?method=repositoryListForm"/>"><spring:message code="logmanager.repository.title"/></a></li>
					<li><a href="<c:url value="/logManager.do?method=appList"/>"><spring:message code="logmanager.logapplication.title"/></a></li>
					<li><a href="<c:url value="/account.do?method=view"/>"><spring:message code="logmanager.account.title"/></a></li>
                </c:if>
                </ul>
			</div>
			<hr/>
			
			<div id="contents">
				<h2><spring:message code="logmanager.analysis.title"/></h2>
                <div id="innercontents">
                	<div id="tabs">
	                	<ul>
	                		<li class="selected"><a href="#gridStyle" id="gridStyle"><spring:message code="logmanager.analysis.grid.style"/></a></li>
	                		<li><a href="#textStyle" id="textStyle"><spring:message code="logmanager.analysis.text.style"/></a></li>
	                	</ul>
                	</div>
					<div id="divSearchCondition" class="search_area">
						<form:form name="searchForm" id="searchForm" method="post" target="_self" commandName="searchCondition">
							<form:hidden path="pageIndex"/>
							<form:hidden path="pageSize"/>
						<table summary="App, Search Type, Serch Result, Logger, C/L IP, User ID, Duration, loglevel, this level only, Program Info, Method Name">
							<caption>Search Area</caption>
                            <colgroup>
                                <col style="width:18%;" />
                                <col style="width:32%;" />
                                <col style="width:18%;" />
                                <col style="width:32%;" />
                            </colgroup>
                            <tbody>
                            	<tr>
									<td colspan="4" class="align_right nonebg">
										<form:checkbox id="checkboxAdvancedOptions" path="advancedOptions" class="checkbox_search" value="true" /><spring:message code="logmanager.analysis.advanced.options"/>
										<form:checkbox id="checkboxMatchedLogOnly" path="matchedLogOnly" class="checkbox_search" value="true" /><spring:message code="logmanager.analysis.matched.only"/>
									</td>
								</tr>
								<tr id="trApp">
									<th class="topline"><spring:message code="logmanager.analysis.app.name"/></th>
									<td class="topline">
										<form:select id="selectAppName" class="select_search" path="appName" items="${appNameList}"/>
									</td>
									<th class="topline appender-title"><spring:message code="logmanager.analysis.repository"/></th>
									<td class="topline">
										<form:select id="selectRepository" path="repositoryName" class="select_search" items="${repositoryList}"/>
									</td>
								</tr>
								<tr class="advanced-option">
									<th><spring:message code="logmanager.analysis.clientip"/></th>
									<td>
										<form:input id="inputClientIp" class="advanced-form" path="clientIp"/>
									</td>
									<th><spring:message code="logmanager.analysis.userid"/></th>
									<td>
										<form:input id="inputUserId" class="advanced-form" path="userId"/>
									</td>
								</tr>
								<tr>
									<th rowspan="2"><spring:message code="logmanager.analysis.duration"/></th>
									<td id="tdDurTempl" colspan="3">
										<form:radiobutton path="durationTemplate" id="rdo1Hour" value="1"/><spring:message code="logmanager.analysis.duration.1time"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo3Hour" value="2"/><spring:message code="logmanager.analysis.duration.3time"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo1Day" value="3"/><spring:message code="logmanager.analysis.duration.1day"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo1Week" value="4"/><spring:message code="logmanager.analysis.duration.1week"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo1Month" value="5"/><spring:message code="logmanager.analysis.duration.1month"/>
									</td>
									
								</tr>
								<tr>
									<td colspan="3" class="advanced-option">
										<form:input id="textFromDate" path="fromDate" class="from"/>
										<a class="underline_none calendar-icon" target="textFromDate">
					                    	<img id="calendar" src="<c:url value='/logmanager/images/btn_calendar_i.gif'/>" alt="Calendar" />
					                    </a>
										<form:select id="selectFromHour" path="fromHour" items="${hours}" class="from"/>
										&nbsp;:&nbsp;
										<form:select id="selectFromMinute" path="fromMinute" items="${minutes}" class="from"/>
										<form:checkbox id="checkboxUseFromDate" path="useFromDate" value="true"/>
										&nbsp;~&nbsp;
										<form:input id="textToDate" path="toDate" class="to"/>
										<a class="underline_none calendar-icon" target="textToDate">
					                    	<img id="calendar" src="<c:url value='/logmanager/images/btn_calendar_i.gif'/>" alt="Calendar" />
					                    </a>
										<form:select id="selectToHour" path="toHour" items="${hours}" class="to"/>
										&nbsp;:&nbsp;
										<form:select id="selectToMinute" path="toMinute" items="${minutes}" class="to"/>
										<form:checkbox id="checkboxUseToDate" path="useToDate" value="true"/>
									</td>
								</tr>
								<tr>
									<th><spring:message code="logmanager.analysis.log.level"/></th>
									<td colspan="3">
										<form:select id="selectLogLevel" path="level" items="${levels}"/>
										<form:select id="selectLogDirectionSupport" path="logLevelDirection">
											<form:option value="1">△&nbsp;(<spring:message code="logmanager.analysis.high"/>)</form:option>
											<form:option value="0">〓&nbsp;(<spring:message code="logmanager.analysis.only"/>)</form:option>
											<form:option value="2">▽&nbsp;(<spring:message code="logmanager.analysis.sub"/>)</form:option>
										</form:select>
									</td>
								</tr>
								<tr class="advanced-option">
									<th><spring:message code="logmanager.analysis.program"/></th>
									<td colspan="3">
										<label for="inputClassName"><spring:message code="logmanager.analysis.classname"/></label>&nbsp;<form:input id="inputClassName" class="advanced-form" path="className"/>
										&nbsp;&nbsp;<label for="inputMethodName"><spring:message code="logmanager.analysis.methodname"/></label>&nbsp;<form:input id="inputMethodName" class="advanced-form" path="methodName"/>
									</td>
								</tr>
								<tr>
									<th class="bottomline"><spring:message code="logmanager.analysis.message.text"/></th>
									<td colspan="3" class="bottomline">
										<form:input id="inputMessageText" path="messageText"/>(<spring:message code="logmanager.analysis.blank"/>)
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
					<table width="100%">
						<tr><td style="text-align:left;">[<span class="spanListCount"></span>&nbsp;<spring:message code="logmanager.analysis.rows"/>]</td><td style="text-align:right;"><a href="#" class="btn-export"><spring:message code="logmanager.button.export"/></a></td></tr>
					</table>
					<div id="divMessageGrid" class="margin_top0">
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

<div id="export-format-form" title="<spring:message code="logmanager.analysis.export.title"/>">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.analysis.export.title"/></caption>
		    <colgroup>
		        <col style="width:50%;" />
		        <col style="width:50%;" />
		    </colgroup>
		    <tr>
		        <td class="topline bottomline"><a href="#" id="txtFormat"><spring:message code="logmanager.analysis.export.text"/></a></td>
		        <td class="topline bottomline"><a href="#" id="xlsFormat"><spring:message code="logmanager.analysis.export.excel"/></a></td>
		    </tr>
		</table>
	</div>
</div>
<div id="log-detail-form" title="<spring:message code="logmanager.analysis.detail.title"/>">
	<div class="view poplayer margin_top10">
		<table summary="">
		    <caption><spring:message code="logmanager.analysis.detail.title"/></caption>
		    <colgroup>
		        <col style="width:30%;" />
		        <col style="width:60%;" />
		    </colgroup>
		    <tr>
		        <th class="topline"><spring:message code="logmanager.analysis.level"/></th>
		        <td class="topline"><span id="span-level"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.timestamp"/></th>
		        <td><span id="span-timestamp"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.clientip"/></th>
		        <td><span id="span-clientIp"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.userid"/></th>
		        <td><span id="span-userId"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.classname"/></th>
		        <td><span id="span-className"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.methodname"/></th>
		        <td><span id="span-methodName"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.linenumber"/></th>
		        <td><span id="span-lineNumber"></span></td>
		    </tr>
		    <tr>
		        <th><spring:message code="logmanager.analysis.thread"/></th>
		        <td><span id="span-thread"></span></td>
		    </tr>
		    <tr>
		        <th class="bottomline"><spring:message code="logmanager.analysis.message"/></th>
		        <td class="bottomline"><span id="span-message"></span></td>
		    </tr>
		</table>
	</div>
</div>
<form id="exportForm" style="display:none;"/>
</body>
</html>