<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.openiam.com/tags/openiam" prefix="openiam" %>
<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>${titleOrganizatioName} - <fmt:message key="openiam.ui.debug.authorization" /></title>
		<link href="/openiam-ui-static/css/common/style.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="/openiam-ui-static/css/common/style.client.css" rel="stylesheet" type="text/css" />
		<openiam:overrideCSS />
        <script type="text/javascript" src="/openiam-ui-static/_dynamic/openiamResourceBundle.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/jquery/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/jquery/jquery-ui-1.9.1.custom.min.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/json/json.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/openiam.common.js"></script>
	    
		<script type="text/javascript">
			OPENIAM = window.OPENIAM || {};
			OPENIAM.ENV = window.OPENIAM.ENV || {};
		</script>
	</head>
	<body>
		<div id="title" class="title">
            <fmt:message key="openiam.ui.debug.authorization" />
		</div>
		<div class="frameContentDivider">
			<form method="POST" action="authorizationDebug.html">
				<div>
					<label><fmt:message key="contentprovider.provider.debug.user.id" /></label>
					<input id="userId" name="userId"  type="text" class="full rounded" autocomplete="off" value="${requestScope.userId}" />
				</div>
				<div>
					<label><fmt:message key="openiam.ui.shared.resource.name" /></label>
					<input id="resourceName" name="resourceName"  type="text" class="full rounded" autocomplete="off" value="${requestScope.resourceName}" />
				</div>
				<input type="submit" value="<fmt:message key='openiam.ui.debug.check.authorization' />" class="redBtn" autocomplete="off">
			</form>
		</div>
		<c:if test="${! empty requestScope.authorizationResponse}">
			<div id="title" class="title">
                <fmt:message key="openiam.ui.debug.authorization.response" />
			</div>
			<div class="frameContentDivider">
                <fmt:message key="openiam.ui.debug.is.user.entitled" />:  ${requestScope.authorizationResponse}
			</div>
		</c:if>
	</body>
</html>