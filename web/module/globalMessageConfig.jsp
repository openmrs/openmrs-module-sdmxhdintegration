<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ taglib prefix="springform" uri="http://www.springframework.org/tags/form" %>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="include.jsp"%>

<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<a href="messageUpload.form"><spring:message code="@MODULE_ID@.general.uploadlink" /></a> | <a href="viewSDMXHDMessages.list"><spring:message code="@MODULE_ID@.general.viewlink" /></a>

<h2><spring:message code="@MODULE_ID@.globalconfig.title" /></h2>

<springform:form commandName="sdmxhdMessage">

	<span class="boxHeader">
		<b><spring:message code="@MODULE_ID@.globalconfig.frequency" /></b>
		<a onclick="$j('#frequencyTable').slideToggle()"> <spring:message code="@MODULE_ID@.general.show-hide" /></a>
	</span>
	<table id="frequencyTable" class="box">
		<tr>
			<td><spring:message code="@MODULE_ID@.globalconfig.reportingfrequency" /></td>
			<td>
				<springform:select path="groupElementAttributes[${'FREQ'}]" items="${CL_FREQ}" itemLabel="description.defaultStr" itemValue="value" />
			</td>
		</tr>
	</table>
	
	<br />
	
	<span class="boxHeader">
		<b><spring:message code="@MODULE_ID@.globalconfig.datasetattr" /></b>
		<a onclick="$j('#datasetTable').slideToggle()"> <spring:message code="@MODULE_ID@.general.show-hide" /></a>
	</span>
	<table id="datasetTable" class="box">
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
	
	<!--
	<br />
	
	<span class="boxHeader">
		<b><spring:message code="Message Header Attributes" /></b>
		<a onclick="$j('#headerTable').slideToggle()"> [show/hide]</a>
	</span>
	<table id="headerTable" class="box">
		<tr>
			<td></td>
			<td>
				<springform:input path="headerElementAttributes['']" />
			</td>
		</tr>
	</table>
	-->
	
	<br />
	
	<input type="submit" value="<spring:message code="@MODULE_ID@.general.save" />" />

</springform:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>