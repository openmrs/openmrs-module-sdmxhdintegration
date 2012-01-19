<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>

<h2><spring:message code="SDMX-HD Messages" /></h2>

<form method="POST">

	<b class="boxHeader">
		<spring:message code="@MODULE_ID@.configpage.title" />
	</b>
	<table class="box">
		<tr>
			<th><spring:message code="@MODULE_ID@.configpage.option" /></th>
			<th><spring:message code="@MODULE_ID@.configpage.description" /></th>
			<th><spring:message code="@MODULE_ID@.configpage.value" /></th>
		</tr>
		<tr>
			<td><spring:message code="@MODULE_ID@.configpage.messageuploaddir" /></td>
			<td><spring:message code="@MODULE_ID@.configpage.description.text" /></td>
			<td><input type="text" name="messageUploadDir" value="${messageUploadDir}" /></td>
		</tr>
		<tr>
			<td><input type="submit" value="<spring:message code="@MODULE_ID@.general.save" />" /></td>
		</tr>
	</table>
	
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
