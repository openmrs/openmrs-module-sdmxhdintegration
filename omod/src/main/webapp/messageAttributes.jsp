<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messages.list" />

<springform:form commandName="message">

	<b class="boxHeader">
		<spring:message code="sdmxhdintegration.general.message" />
	</b>
	<div class="box" style="margin-bottom: 20px">
		<table>
			<tr>
				<td width="300"><spring:message code="general.name" /></td>
				<td>${message.name}</td>
			</tr>
		</table>
	</div>

	<b class="boxHeader">
		<spring:message code="sdmxhdintegration.globalconfig.groupAttributes" />
	</b>
	<div class="box" style="margin-bottom: 20px">
		<table>
			<tr>
				<td width="300"><spring:message code="sdmxhdintegration.globalconfig.reportingfrequency" /></td>
				<td>
					<c:choose>
						<c:when test="${frequencyCodes != null}">
							<springform:select path="groupElementAttributes[${'FREQ'}]" items="${frequencyCodes}" itemLabel="description.defaultStr" itemValue="value" />
						</c:when>
						<c:otherwise>
							<i><spring:message code="sdmxhdintegration.globalconfig.noFrequencies" /></i>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</table>
	</div>
	
	<b class="boxHeader">
		<spring:message code="sdmxhdintegration.general.datasetAttributes" />
	</b>
	<div class="box" style="margin-bottom: 20px">
		<table>
			<tr>
				<td width="300">datasetID</td>
				<td>
					<springform:input path="datasetElementAttributes['datasetID']" />
				</td>
			</tr>
			<tr>
				<td>dataProviderSchemeAgencyId</td>
				<td>
					<springform:input path="datasetElementAttributes['dataProviderSchemeAgencyId']" />
				</td>
			</tr>
			<tr>
				<td>dataProviderSchemeId</td>
				<td>
					<springform:input path="datasetElementAttributes['dataProviderSchemeId']" />
				</td>
			</tr>
			<tr>
				<td>dataProviderID</td>
				<td>
					<springform:input path="datasetElementAttributes['dataProviderID']" />
				</td>
			</tr>
			<!--
			<tr>
				<td>dataflowAgencyID</td>
				<td>
					<springform:input path="datasetElementAttributes['dataflowAgencyID']" />
				</td>
			</tr>
			<tr>
				<td>action</td>
				<td>
					<springform:input path="datasetElementAttributes['action']" />
				</td>
			</tr>
			<tr>
				<td>reportingBeginDate</td>
				<td>
					<springform:input path="datasetElementAttributes['reportingBeginDate']" />
				</td>
			</tr>
			<tr>
				<td>reportingEndDate</td>
				<td>
					<springform:input path="datasetElementAttributes['reportingEndDate']" />
				</td>
			</tr>
			<tr>
				<td>validFromDate</td>
				<td>
					<springform:input path="datasetElementAttributes['validFromDate']" />
				</td>
			</tr>
			<tr>
				<td>validToDate</td>
				<td>
					<springform:input path="datasetElementAttributes['validToDate']" />
				</td>
			</tr>
			<tr>
				<td>publicationYear</td>
				<td>
					<springform:input path="datasetElementAttributes['publicationYear']" />
				</td>
			</tr>
			<tr>
				<td>publicationPeriod</td>
				<td>
					<springform:input path="datasetElementAttributes['publicationPeriod']" />
				</td>
			</tr>
			-->
		</table>
	</div>
	
	<input type="submit" value="<spring:message code="general.save" />" />
	<input type="button" value="<spring:message code="general.cancel" />" onclick="location.href='messages.list'" />
	
</springform:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>