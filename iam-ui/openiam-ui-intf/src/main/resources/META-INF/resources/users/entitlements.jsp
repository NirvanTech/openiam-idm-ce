<%
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.openiam.com/tags/openiam" prefix="openiam" %>
<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title>${titleOrganizatioName} -
			<c:choose>
				<c:when test="${requestScope.type eq 'roles'}">
					<fmt:message key="openiam.ui.user.entitlement.role.title"/>
				</c:when>
				<c:when test="${requestScope.type eq 'groups'}">
                    <fmt:message key="openiam.ui.user.entitlement.group.title"/>
				</c:when>
				<c:when test="${requestScope.type eq 'resources'}">
                    <fmt:message key="openiam.ui.user.entitlement.resource.title"/>
				</c:when>
				<c:when test="${requestScope.type eq 'organizations'}">
                    <fmt:message key="openiam.ui.user.entitlement.organization.title"/>
				</c:when>
				<c:otherwise>
					
				</c:otherwise>
			</c:choose>
		</title>
		<link href="/openiam-ui-static/css/common/style.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="/openiam-ui-static/css/common/style.client.css" rel="stylesheet" type="text/css" />
		<link href="/openiam-ui-static/plugins/tablesorter/themes/yui/style.css" rel="stylesheet" type="text/css" />
		<link href="/openiam-ui-static/js/common/jquery/css/smoothness/jquery-ui-1.9.1.custom.css" rel="stylesheet" type="text/css" />
		<link href="/openiam-ui-static/css/common/entitlements.css" rel="stylesheet" type="text/css" />
		<link href="/openiam-ui-static/js/common/plugins/entitlementstable/entitlements.table.css" rel="stylesheet" type="text/css" />
		<link href="/openiam-ui-static/js/common/plugins/modalsearch/modal.search.css" rel="stylesheet" type="text/css" />
		<link href="/openiam-ui-static/js/webconsole/plugins/usersearch/user.search.css" rel="stylesheet" type="text/css" />
		<openiam:overrideCSS />
		
        <script type="text/javascript" src="/openiam-ui-static/_dynamic/openiamResourceBundle.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/jquery/jquery-1.8.2.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/jquery/jquery-ui-1.9.1.custom.min.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/json/json.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/openiam.common.js"></script>

		<script type="text/javascript" src="/openiam-ui-static/plugins/tablesorter/js/jquery.tablesorter-2.0.3.js"></script>
	    <script type="text/javascript" src="/openiam-ui-static/plugins/tablesorter/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/plugins/tablesorter/js/jquery.tablesorter.filer.js"></script>
        <script type="text/javascript" src="/openiam-ui-static/plugins/tablesorter/js/jquery.tablesorter.pager.js"></script>

        <script type="text/javascript" src="/openiam-ui-static/js/common/menutree.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/plugins/modalEdit/modalEdit.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/user/user.entitlements.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/plugins/modalsearch/modal.search.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/search/resource.search.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/search/group.search.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/search/organization.search.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/search/role.search.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/plugins/entitlementstable/entitlements.table.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/common/entitlements/entitlement.entity.view.js"></script>
		<script type="text/javascript" src="/openiam-ui-static/js/webconsole/plugins/usersearch/user.search.form.js"></script>
        <script type="text/javascript" src="/openiam-ui-static/js/webconsole/plugins/usersearch/user.search.results.js"></script>
        <script type="text/javascript" src="/openiam-ui-static/plugins/placeholder/jquery.placeholder.js"></script>
		
		<script type="text/javascript">
			OPENIAM = window.OPENIAM || {};
			OPENIAM.ENV = window.OPENIAM.ENV || {};
			OPENIAM.ENV.ContextPath = "${pageContext.request.contextPath}";
			OPENIAM.ENV.MenuTree = <c:choose><c:when test="${! empty requestScope.menuTree}">${requestScope.menuTree}</c:when><c:otherwise>null</c:otherwise></c:choose>;
            OPENIAM.ENV.initialMenu = <c:choose><c:when test="${! empty requestScope.initialMenu}">${requestScope.initialMenu}</c:when><c:otherwise>null</c:otherwise></c:choose>;
            OPENIAM.ENV.buttonsMenu = <c:choose><c:when test="${! empty requestScope.buttonsMenu}">${requestScope.buttonsMenu}</c:when><c:otherwise>null</c:otherwise></c:choose>;
            OPENIAM.ENV.UserId = "${requestScope.user.id}";
			OPENIAM.ENV.MenuTreeAppendURL = "id=${requestScope.user.id}";
			OPENIAM.ENV.EntitlementType = "${requestScope.type}";
			OPENIAM.ENV.PreventOnClick = ${not fn:contains(pageContext.request.contextPath, 'webconsole')};

		</script>
	</head>
	<body>
		<div id="title" class="title">
			<c:choose>
				<c:when test="${requestScope.type eq 'Roles'}">
					<fmt:message key="openiam.ui.user.role"/>: ${requestScope.user.displayName}
				</c:when>
				<c:when test="${requestScope.type eq 'Groups'}">
                    <fmt:message key="openiam.ui.user.group"/>: ${requestScope.user.displayName}
				</c:when>
				<c:when test="${requestScope.type eq 'Resources'}">
                    <fmt:message key="openiam.ui.user.resource"/>: ${requestScope.user.displayName}
				</c:when>
				<c:when test="${requestScope.type eq 'Organizations'}">
                    <fmt:message key="openiam.ui.user.organization"/>: ${requestScope.user.displayName}
				</c:when>
				<c:otherwise>
					
				</c:otherwise>
			</c:choose>
			<div id="usermenu" style="min-width: 800px"></div>
		</div>
		<div class="frameContentDivider">
			<div id="entitlementsContainer"></div>
		</div>
		<div id="searchResultsContainer" style="display:none"></div>
		<div id="editDialog"></div>
	</body>
</html>