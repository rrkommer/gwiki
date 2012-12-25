<%@ taglib uri="/WEB-INF/tlds/c-rt.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/tlds/fmt-rt.tld" prefix="fmt"%>
<html>
<head>
<script type="text/javascript"
	src="<c:url value="/gwiki/static/js/jquery-1.4.min.js"/>"></script>
<script type='text/javascript'
	src="<c:url value='/gwiki/static/js/jquery.bgiframe.min.js'/>"></script>
	<script src='<c:url value="/gwiki/static/js/jquery.contextmenu.js"/>' type="text/javascript"></script>
<script type="text/javascript"
	src="<c:url value="/gwiki/static/gwiki/gwiki-i18n-contextmenu-0.3.js"/>"></script>
<style type="text/css">
.gwiki18nk {
	background-color: #EEEEEE;
}
</style>
</head>
<body>
<div id="gwikii18nmenu" style="font-size:0.7em;display:none;width:650px;background-color:white;border:1px solid black;padding:5px;"></div>

<h1>GWiki I18N Example.</h1>
This is a <fmt:message key="gwiki.page.headmenu.Page" /> and a message: <fmt:message key="gwiki.edit.EditPage.label.noNotificationEmails" />.
<% de.micromata.genome.gwiki.page.impl.i18n.GWikiMessageTag.addI18NDomMap(pageContext, "gwiki.page.headmenu.Page", "pageButton"); %>
<input id="pageButton" type="button" value="<fmt:message key="gwiki.page.headmenu.Page" />"></input></span>
<%de.micromata.genome.gwiki.page.impl.i18n.GWikiMessageTag.renderPatchDom(pageContext);  %>
</body>
</html>
