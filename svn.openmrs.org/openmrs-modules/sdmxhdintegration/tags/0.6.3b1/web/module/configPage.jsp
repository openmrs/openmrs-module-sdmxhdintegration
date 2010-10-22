<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<h2><spring:message code="SDMX-HD Messages" /></h2>

<form method="POST">

	<b class="boxHeader">
		<spring:message code="Configuration Options" />
	</b>
	<table class="box">
		<tr>
			<th>Option</th>
			<th>Description</th>
			<th>Value</th>
		</tr>
		<tr>
			<td>Message Upload Directory</td>
			<td>The directory where SDMX-HD messages are uploaded to.</td>
			<td><input type="text" name="messageUploadDir" value="${messageUploadDir}" /></td>
		</tr>
		<tr>
			<td><input type="submit" value="Save" /></td>
		</tr>
	</table>
	
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
