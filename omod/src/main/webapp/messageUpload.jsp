<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messageUpload.form" />

<b class="boxHeader">
	<spring:message code="Upload SDMX-HD Message" />
</b>
<springform:form modelAttribute="message" cssClass="box" enctype="multipart/form-data">
	<input type="hidden" value="${message.id}"/>
	<table>
		<tr>		
			<td width="300"><spring:message code="general.name" /></td>
			<td>
				<springform:input path="name"/>
				<springform:errors path="name" cssClass="error"/>
			</td>
		</tr>
		<tr>
			<td><spring:message code="general.description" /></td>
			<td>
				<springform:input path="description"/>
				<springform:errors path="description" cssClass="error"/>
			</td>
		</tr>
		<tr>
	    	<td><spring:message code="@MODULE_ID@.upload.sdmxhdZipFileName" /></td>
	    	<td>
	    		<input type="file" name="sdmxhdMessage" id="sdmxhdMessage" size="40" />
	    		
				<c:if test="${not empty message.sdmxhdZipFileName}">
				    	<spring:message code="@MODULE_ID@.upload.currentMessage" />: <i>${message.sdmxhdZipFileName}</i>
				    	<br />
				    	<br />
				    	<span class="tooltip"><spring:message code="@MODULE_ID@.upload.overwriteWarning" /></span>
				</c:if>
				
				<input type="hidden" value="${message.sdmxhdZipFileName}"/>
				<br />
				<springform:errors path="sdmxhdZipFileName" cssClass="error"/>
	    	</td>
		</tr>
		<tr>
			<td><input type="submit" value="<spring:message code="@MODULE_ID@.general.upload" />" /></td>
		</tr>
	</table>
</springform:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>
