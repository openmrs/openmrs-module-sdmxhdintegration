<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:require privilege="Upload SDMX-HD Messages" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messageUpload.form" />

<span>Upload New SDMX-HD Template</span> | <a href="viewSDMXHDMessages.list">View All SDMX-HD Message Templates</a>

<h2><spring:message code="Upload SDMX-HD Message" /></h2>

<b class="boxHeader">
	<spring:message code="Upload SDMX-HD Message" />
</b>
<form:form modelAttribute="sdmxhdMessage" enctype="multipart/form-data">
	<table class="box">
		<tr>
			<input type="hidden" value="${sdmxhdMessage.id}"/>
			
			<td>Name:</td>
			<td>
				<form:input path="name"/>
				<form:errors path="name" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td>Description:</td>
			<td>
				<form:input path="description"/>
				<form:errors path="description" cssClass="error"/>
			</td>
		</tr>
		<tr>
	    	<td>SDMX-HD DataSetDefinition:</td>
	    	<td>
				<c:choose>
				    <c:when test="${empty sdmxhdMessage.sdmxhdZipFileName}">
				    	<input type="file" name="sdmxhdMessage" id="sdmxhdMessage" size="40" />
				    </c:when>
				    <c:otherwise>
				    	Current message: <i>${sdmxhdMessage.sdmxhdZipFileName}</i>
				    	<input type="file" name="sdmxhdMessage" id="sdmxhdMessage" size="40" />
				    	<br />
				    	<br />
				    	<span class="tooltip">Warning: Uploading a new SDMX-HD DataSetDefinition will delete all previous mappings</span>
				    </c:otherwise>
				</c:choose>
				
				<input type="hidden" value="${sdmxhdMessage.sdmxhdZipFileName}"/>
				<br />
				<form:errors path="sdmxhdZipFileName" cssClass="error"/>
	    	</td>
		</tr>
		<tr></tr>
		<tr>
			<td><input type="submit" value="Save" /></td>
		</tr>
	</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
