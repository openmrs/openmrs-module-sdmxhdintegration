<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/config.form" />

<b class="boxHeader">
	<spring:message code="sdmxhdintegration.config.title" />
</b>
<form method="post" class="box">
	<table>
		<tr>
			<th><spring:message code="sdmxhdintegration.config.option" /></th>
			<th><spring:message code="sdmxhdintegration.config.description" /></th>
			<th><spring:message code="sdmxhdintegration.config.value" /></th>
		</tr>
		<tr>
			<td><spring:message code="sdmxhdintegration.config.messageuploaddir" /></td>
			<td><spring:message code="sdmxhdintegration.config.messageuploaddir.description" /></td>
			<td><input type="text" style="width: 300px" name="messageUploadDir" value="${messageUploadDir}" /></td>
		</tr>
		<tr>
			<td><input type="submit" value="<spring:message code="general.save" />" /></td>
		</tr>
	</table>
	
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
