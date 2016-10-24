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

<script type="text/javascript" src="<c:url value='/jquery/jquery/jquery.highlight-3.js'/>"></script>

<script type="text/javascript"><!--
 
function fncLogout(){
	var msg = '<spring:message code="logmanager.confirm.logout"/>';
    ans = confirm(msg);
    if (ans) {
    	document.location.href='<c:url value="/logout.do"/>';
    } else {
        return false;
    }
}
var isTailing = false;
var pollingTerm = '<c:out value="${pollingTerm}"/>';
var initAppName = '<c:out value="${param.appName}"/>';
var DATE_FORMAT = 'yyyy-MM-dd HH:mm:ss,SSS';

/**
 * log data draw(1row)
 */
function drawLogData(i, log) {
	if($('#tbodyMessageText tr').length >= parseInt($('#txtLineLimit').val())) {
		$('#tbodyMessageText tr:first').remove();
	} 
	var html = '<tr class="log-row"><td class="number-cell">' + (1 + i) + '</td><td class="log-cell" id="' + log._id + '">';
	html += '<pre>';
	html += '<span class="level ' + log.level + ' NEW">[' + log.level + ']&nbsp;</span>';
	html += '<span class="timestamp ' + log.level + ' NEW">['	+ log.timestamp.dateFormat(DATE_FORMAT) + ']&nbsp;</span>'; 
	if(log.mdc) {
		html += '<span class="client-ip ' + log.level + ' NEW">' + (log.clientIp ? '[' + log.clientIp + ']&nbsp;' : '') + '</span>';
		html += '<span class="user-id ' + log.level + ' NEW">' + (log.userId?'[' + log.userId + ']&nbsp;':'') + '</span>';	
	}
	if(log.className != null) {
		html += '<span class="class-name ' + log.level + ' NEW">[' + log.className + '</span>';
		if(log.methodName != null) {
			html += '<span class="method-name ' + log.level + ' NEW">.' + log.methodName + '()]&nbsp;</span>';	
		}else{
			html += '<span class="method-name ' + log.level + ' NEW">]&nbsp;</span>';
		}
	}else if(log.methodName != null) {
		html += '<span class="method-name ' + log.level + ' NEW">[' + log.methodName + '()]&nbsp;</span>';	
	}
	
	if(log.lineNumber != null) {
		html += '<span class="line-number ' + log.level + ' NEW">[line ' + log.lineNumber + ']&nbsp;</span>';	
	}
	if(log.thread != null) {
		html += '<span class="thread ' + log.level + ' NEW">[' + log.thread + ']&nbsp;</span>' ;	
	}
	
	for(var key in log) {
		if(log[key] != null) {
			if(key != '_id' && key != 'level' && key != 'timestamp' && key != 'className' 
					&& key != 'methodName' && key != 'lineNumber' && key != 'thread' && key != 'message') {
				html += '<span class="else ' + log.level + ' NEW">[' + log[key] + ']&nbsp;</span>';	
			}
		}
	}
	
	html += '<span class="message ' + log.level + ' NEW">' + log.message.escapeHTML() + '</span>'; 
	html += '</pre>';
	html += '</td></tr>';
	
	$('#tbodyMessageText').append(html);
}

/**
 * highlight search result
 */
function highlightResult() {

	// search result highlight effect : log level
	if($('#selectLogLevel').val() != '') {
		$('td.log-cell span.level.NEW').highlight($('#selectLogLevel').val());
	}
	
	// search result highlight effect : client ip
	if($('#inputClientIp').val() != '') {
		$('td.log-cell span.client-ip.NEW').highlight($('#inputClientIp').val());
	}

	// search result highlight effect : user id
	if($('#inputUserId').val() != '') {
		$('td.log-cell span.user-id.NEW').highlight($('#inputUserId').val());
	}

	// search result highlight effect : class name
	if($('#inputClassName').val() != '') {
		$('td.log-cell span.class-name.NEW').highlight($('#inputClassName').val());
	}
	
	// search result highlight effect : method name
	if($('#inputMethodName').val() != '') {
		$('td.log-cell span.method-name.NEW').highlight($('#inputMethodName').val());
	}
	
	// search result highlight effect : message text
	if($('#inputMessageText').val() != '') {
		var a = $('#inputMessageText').val().split(' ');
		for(var i=0;i<a.length;i++) {
			$('td.log-cell span.message.NEW').highlight(a[i]);
		}
	}
}

/**
 * set search result count
 */
function setListCount(length) {
	$('span.spanListCount').html(length);
}

function getListCount() {
	var c = 0;
	c = parseInt($('span.spanListCount').html());
	if(isNaN(c)) return 0;
	else return c;
}

/**
 * on search
 */
function onSearch(){
	$.post('<c:url value="/logManager.do?method=analysis"/>', $('#searchForm').serialize(), function(data){
		var logs = data.list;
		var html = '';

		if(logs.length > 5000) {
			alert('<spring:message code="logmanager.too.many.result.01"/>(5000 <spring:message code="logmanager.too.many.result.02"/>)! \n<spring:message code="logmanager.too.many.result.03"/>');
		}

		$('#tbodyMessageText tr').remove();
		
		for(var i=0;i<logs.length;i++) {
			drawLogData(i, logs[i]);
		}
		
		setListCount(logs.length);
		
		if($('#checkboxLogTailingMode').is(':checked') == false) {
			highlightResult();
		}
	});
}

/**
 * on tail search
 */
function onTailSearch() {
	if(isTailing) {
		var pos = null;
		var p0 = $('#tbodyMessageText tr:last span.timestamp');
		var p = null;
		if( p0.html() != null) {
			p0 = p0.html();
			p = p0.substring(p0.indexOf('[')+1).substring(0, p0.indexOf(']'));				
		}else{
			p = getTimestamp(pollingTerm) + ', 000';
		}
			
		$('#previousTimestamp').val(p);
		
		$.post('<c:url value="/logManager.do?method=analysis"/>', $('#searchForm').serialize(), function(data){
			var logs = data.list;
			var html = '';

			if(logs.length > 5000) {
				alert('<spring:message code="logmanager.too.many.result.01"/>(5000 <spring:message code="logmanager.too.many.result.02"/>)! \n<spring:message code="logmanager.too.many.result.03"/>');
			}

			$('td.log-cell span.NEW').removeClass('NEW');
			
			var l = getListCount();
			for(var i=0;i<logs.length;i++) {
				drawLogData(l + i, logs[i]);
			}

			setListCount(l + logs.length);
			pos = $('#tbodyMessageText tr:last').offset();
			if(pos != null && logs.length > 0) {
				$('html, body').animate({'scrollTop' : pos.top}, 500);
			}
			
			highlightResult();
			
			var t = setTimeout(function() {
					onTailSearch();
				}, pollingTerm*1000);
		});
	}
}

$(document).ready(function() {

	/**
	 * 'Search' button click event handler
	 * search form submit
	 */
	$('#btnSearch').click(function(e) {
		if($('#selectAppName').val() != null) {
			isTailing = false;
			onSearch();
		}
	});

	/**
	 * 'checkboxLogTailingMode' check box click event handler
	 */
	$('#checkboxLogTailingMode').click(function(e){
		if($('#selectAppName').val() != null) {
			if($('#checkboxLogTailingMode').is(':checked')) {
				isTailing = true;
				$('#previousTimestamp').val('');
				$('#checkboxLogTailingModeBottom').attr('checked', true);
				$('#checkboxLogTailingModeBottom').css('display', 'inline');
				$('#checkboxLogTailingModeBottom + label').css('display', 'inline');
				onTailSearch();
			}else{
				isTailing = false;
				$('#checkboxLogTailingModeBottom').attr('checked', false);
				$('#checkboxLogTailingModeBottom').css('display', 'none');
				$('#checkboxLogTailingModeBottom + label').css('display', 'none');
			}
		}
	});

	/**
	 * 'checkboxLogTailingModeBottom' check box click event handler
	 */
	$('#checkboxLogTailingModeBottom').click(function(e){
		if($('#selectAppName').val() != null) {
			if($('#checkboxLogTailingModeBottom').is(':checked')) {
				isTailing = true;
				$('#previousTimestamp').val('');
				$('#checkboxLogTailingMode').attr('checked', true);
				onTailSearch();
			}else{
				isTailing = false;
				$('#checkboxLogTailingMode').attr('checked', false);
			}
		}
	});

	/**
	 * 'AdvancedOptions' check box click event handler
	 */
	$('#checkboxAdvancedOptions').click(function(e) {
		$('.advanced-form').val('');
		$('tr.advanced-option,td.advanced-option').toggle();
	});

	/**
	 * 'Clear' button click event handler
	 */
	$('a.btn-clear').click(function(e) {
		$('span.spanListCount').html('0');
		$('#divMessageText tbody').html('');
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
		$('#selectFromHour').val(aDate[1].length == 1 ? '0' + aDate[1] : aDate[1]);
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
		$('#selectFromHour').val(aDate[1].length == 1 ? '0' + aDate[1] : aDate[1]);
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
		if($('#checkboxUseFromDate').attr('checked')) {
			$('.from').attr('disabled', false);
		}else{
			$('.from').attr('disabled', true);
		}
	});

	/**
	 * 'checkboxUseToDate' check box click event handler
	 */
	$('#checkboxUseToDate').click(function(e) {
		if($('#checkboxUseToDate').attr('checked')) {
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
	 * switch to grid style search
	 **/
	$('#gridStyle').click(function(e){
		$('#searchForm').attr('target', '_self');
		$('#searchForm').attr('action', '<c:url value="/logManager.do?method=analysis4gridForm"/>');
		$('#searchForm').submit();
	});


	if($('#checkboxAdvancedOptions').is(':checked')) {
		$('tr.advanced-option,td.advanced-option').toggle();
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
                    <li><a href="<c:url value="/logManager.do?method=analysis4gridForm"/>"><spring:message code="logmanager.analysis.title"/></a></li>
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
				<h2><spring:message code="logmanager.analysis.title"/></h2>
                <div id="innercontents">
                	<div id="tabs">
	                	<ul>
	                		<li><a href="#gridStyle" id="gridStyle"><spring:message code="logmanager.analysis.grid.style"/></a></li>
	                		<li class="selected"><a href="#textStyle" id="textStyle"><spring:message code="logmanager.analysis.text.style"/></a></li>
	                	</ul>
                	</div>
					<div id="divSearchCondition" class="search_area">
						<form:form name="searchForm" id="searchForm" method="post" target="_self" commandName="searchCondition">
						<input type="hidden" name="previousTimestamp" id="previousTimestamp"/>
						<table summary="App, Search Type, Search Result, Logger, C/L IP, User ID, Duration, loglevel, this level only, Program Info, Method Name, Message Text">
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
										<form:checkbox id="checkboxAdvancedOptions" path="advancedOptions" value="true" label="Advanced Options"/>
										<form:checkbox id="checkboxMatchedLogOnly" path="matchedLogOnly" value="true" label="Matched Log Only"/>
										<form:checkbox id="checkboxLogTailingMode" path="logTailingMode" value="true" label="Log Tailing"/>										
									</td>
								</tr>
								<tr>
									<th class="topline"><spring:message code="logmanager.analysis.app.name"/></th>
									<td class="topline">
										<form:select id="selectAppName" class="select_search" path="appName" items="${appNameList}"/>
									</td>
									<th class="topline"><spring:message code="logmanager.analysis.repository"/></th>
									<td class="topline">
										<form:select id="selectRepository" path="repositoryName" class="select_search" items="${repositoryList}"/>
									</td>
								</tr>
								<tr class="advanced-option">
									<th><spring:message code="logmanager.analysis.client_ip"/></th>
									<td>
										<form:input id="inputClientIp" class="advanced-form" path="clientIp"/>
									</td>
									<th><spring:message code="logmanager.analysis.user.id"/></th>
									<td>
										<form:input id="inputUserId" class="advanced-form" path="userId"/>
									</td>
								</tr>
								<tr>
									<th rowspan="2"><spring:message code="logmanager.analysis.duration"/></th>
									<td id="tdDurTempl" colspan="3">
										<form:radiobutton path="durationTemplate" id="rdo1Hour" value="1" label="1 Hour"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo3Hour" value="2" label="3 Hours"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo1Day" value="3" label="1 Day"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo1Week" value="4" label="1 Week"/>&nbsp;
										<form:radiobutton path="durationTemplate" id="rdo1Month" value="5" label="1 Month"/>
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
									<th><spring:message code="logmanager.analysis.loglevel"/></th>
									<td colspan="3">
										<form:select id="selectLogLevel" path="level" items="${levels}"/>
										<form:select id="selectLogDirectionSupport" path="logLevelDirection">
											<form:option value="1">△&nbsp;(<spring:message code="logmanager.analysis.loglevel.high"/>)</form:option>
											<form:option value="0">〓&nbsp;(<spring:message code="logmanager.analysis.loglevel.this"/>)</form:option>
											<form:option value="2">▽&nbsp;(<spring:message code="logmanager.analysis.loglevel.sub"/>)</form:option>
										</form:select>
									</td>
								</tr>
								<tr class="advanced-option">
									<th><spring:message code="logmanager.analysis.program.info" text="Program Info."/></th>
									<td colspan="3">
										<label for="inputClassName"><spring:message code="logmanager.analysis.class.name" text="Class Name"/></label>&nbsp;<form:input id="inputClassName" class="advanced-form" path="className"/>
										&nbsp;&nbsp;<label for="inputMethodName"><spring:message code="logmanager.analysis.method.name" text="Method Name"/></label>&nbsp;<form:input id="inputMethodName" class="advanced-form" path="methodName"/>
									</td>
								</tr>
								<tr>
									<th class="bottomline"><spring:message code="logmanager.analysis.message.text" text="Message Text"/></th>
									<td colspan="3" class="bottomline">
										<form:input id="inputMessageText" path="messageText"/>(<spring:message code="logmanager.analysis.blank.info" text="&quot;blank&quot; to be more complex search"/>)
									</td>
								</tr>
                            </tbody>
						</table>
						</form:form>
						<table width="100%" class="btn">
							<tr>
								<td width="50%">
									<div class="btncontainer_left">
										<span>
				                            <label for="txtLineLimit"><spring:message code="logmanager.analysis.line.limit"/> : </label>&nbsp;<input type="text" size="3" id="txtLineLimit" value="500" style="text-align:right;"/>
				                        </span>
				                    </div>
								</td>
								<td width="50%">
									<div class="btncontainer_right">
				                        <span class="button tableout">
				                            <button id="btnSearch"><spring:message code="logmanager.button.search"/></button>
				                        </span>
				                    </div>
								</td>
							</tr>
						</table>
						
					</div>
					<table width="100%">
						<tr><td style="text-align:left;">[<span class="spanListCount"></span>&nbsp;<spring:message code="logmanager.analysis.rows"/>]</td><td style="text-align:right;"><a href="#" class="btn-clear"><spring:message code="logmanager.button.clear"/></a>&nbsp;|&nbsp;<a href="#" class="btn-export"><spring:message code="logmanager.button.export"/></a></td></tr>
					</table>
					<div id="divMessageText" class="margin_top0">
						<table width="100%" cellspacing="2" cellpadding="2" border="0">
							<tbody id="tbodyMessageText"></tbody>
						</table>
					</div>
					<table width="100%">
						<tr><td style="text-align:right;"><input type="checkbox" id="checkboxLogTailingModeBottom"/><label for="checkboxLogTailingModeBottom">&nbsp;<spring:message code="logmanager.analysis.logtailing"/></label></td></tr>
					</table>
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
<form id="exportForm" style="display:none;"/>
</body>
</html>