<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="include.jsp"%>

<a href="messageUpload.form">Upload New SDMX-HD Template</a> | <a href="viewSDMXHDMessages.list">View All SDMX-HD Message Templates</a>

<h2><spring:message code="Global Message Configuration" /></h2>

<form:form commandName="sdmxhdMessage">

	<span class="boxHeader">
		<b><spring:message code="Frequency" /></b>
		<a onclick="$('#frequencyTable').slideToggle()"> [show/hide]</a>
	</span>
	<table id="frequencyTable" class="box">
		<tr>
			<td>Reporting Frequency</td>
			<td>
				<form:select path="groupElementAttributes[${'FREQ'}]" items="${CL_FREQ}" itemLabel="description.defaultStr" itemValue="value" />
			</td>
		</tr>
	</table>
	
	<br />
	
	<span class="boxHeader">
		<b><spring:message code="DataSet Attributes" /></b>
		<a onclick="$('#datasetTable').slideToggle()"> [show/hide]</a>
	</span>
	<table id="datasetTable" class="box">
		<!--
		<tr>
			<td>keyFamilyURI</td>
			<td>
				<form:input path="datasetElementAttributes['keyFamilyURI']" />
			</td>
		</tr>
		-->
		<tr>
			<td>datasetID</td>
			<td>
				<form:input path="datasetElementAttributes['datasetID']" />
			</td>
		</tr>
		<tr>
			<td>dataProviderSchemeAgencyId</td>
			<td>
				<form:input path="datasetElementAttributes['dataProviderSchemeAgencyId']" />
			</td>
		</tr>
		<tr>
			<td>dataProviderSchemeId</td>
			<td>
				<form:input path="datasetElementAttributes['dataProviderSchemeId']" />
			</td>
		</tr>
		<tr>
			<td>dataProviderID</td>
			<td>
				<form:input path="datasetElementAttributes['dataProviderID']" />
			</td>
		</tr>
		<!--
		<tr>
			<td>dataflowAgencyID</td>
			<td>
				<form:input path="datasetElementAttributes['dataflowAgencyID']" />
			</td>
		</tr>
		<tr>
			<td>action</td>
			<td>
				<form:input path="datasetElementAttributes['action']" />
			</td>
		</tr>
		<tr>
			<td>reportingBeginDate</td>
			<td>
				<form:input path="datasetElementAttributes['reportingBeginDate']" />
			</td>
		</tr>
		<tr>
			<td>reportingEndDate</td>
			<td>
				<form:input path="datasetElementAttributes['reportingEndDate']" />
			</td>
		</tr>
		<tr>
			<td>validFromDate</td>
			<td>
				<form:input path="datasetElementAttributes['validFromDate']" />
			</td>
		</tr>
		<tr>
			<td>validToDate</td>
			<td>
				<form:input path="datasetElementAttributes['validToDate']" />
			</td>
		</tr>
		<tr>
			<td>publicationYear</td>
			<td>
				<form:input path="datasetElementAttributes['publicationYear']" />
			</td>
		</tr>
		<tr>
			<td>publicationPeriod</td>
			<td>
				<form:input path="datasetElementAttributes['publicationPeriod']" />
			</td>
		</tr>
		-->
	</table>
	
	<!--
	<br />
	
	<span class="boxHeader">
		<b><spring:message code="Message Header Attributes" /></b>
		<a onclick="$('#headerTable').slideToggle()"> [show/hide]</a>
	</span>
	<table id="headerTable" class="box">
		<tr>
			<td></td>
			<td>
				<form:input path="headerElementAttributes['']" />
			</td>
		</tr>
	</table>
	-->
	
	<br />
	
	<input type="submit" value="Save" />

</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>