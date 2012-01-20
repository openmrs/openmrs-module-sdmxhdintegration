<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messages.list" />

<springform:form commandName="sdmxhdMessage">

	<b class="boxHeader">
		<spring:message code="@MODULE_ID@.globalconfig.groupAttributes" />
	</b>
	<div class="box" style="margin-bottom: 20px">
		<table id="frequencyTable">
			<tr>
				<td><spring:message code="@MODULE_ID@.globalconfig.reportingfrequency" /></td>
				<td>
					<springform:select path="groupElementAttributes[${'FREQ'}]" items="${CL_FREQ}" itemLabel="description.defaultStr" itemValue="value" />
				</td>
			</tr>
		</table>
	</div>
	
	<b class="boxHeader">
		<spring:message code="@MODULE_ID@.globalconfig.datasetAttributes" />
	</b>
	<div class="box" style="margin-bottom: 20px">
		<table id="datasetTable">
			<!--
			<tr>
				<td>keyFamilyURI</td>
				<td>
					<springform:input path="datasetElementAttributes['keyFamilyURI']" />
				</td>
			</tr>
			-->
			<tr>
				<td>datasetID</td>
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