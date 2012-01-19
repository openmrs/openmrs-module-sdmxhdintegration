<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<b class="boxHeader">
	<spring:message code="@MODULE_ID@.config.title" />
</b>
<form method="post" class="box">
	<table>
		<tr>
			<th><spring:message code="@MODULE_ID@.config.option" /></th>
			<th><spring:message code="@MODULE_ID@.config.description" /></th>
			<th><spring:message code="@MODULE_ID@.config.value" /></th>
		</tr>
		<tr>
			<td><spring:message code="@MODULE_ID@.config.messageuploaddir" /></td>
			<td><spring:message code="@MODULE_ID@.config.messageuploaddir.description" /></td>
			<td><input type="text" style="width: 300px" name="messageUploadDir" value="${messageUploadDir}" /></td>
		</tr>
		<tr>
			<td><input type="submit" value="<spring:message code="general.save" />" /></td>
		</tr>
	</table>
	
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
