<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messages.list" />

<script type="text/javascript">
	var $j = jQuery.noConflict();
	var scr = "keyFamilyAttributesDialog.form?messageId=${message.id}&keyFamilyId=${keyFamilyId}";

	$j(function() {

		$j("#setAttributesDialogiFrameDiv").dialog({
			autoOpen: false,
			bgiframe: false,
			width: 800,
			height: 600,
			modal: true,
			resizable: true,
			title: "Add Attributes"
		});
		
	});

	function openDialogForColumn(attachmentLevel, columnName) {
		attachmentLevel = encodeURIComponent(attachmentLevel);
		columnName = encodeURIComponent(columnName);
		// add request params
		$j("#setAttributesDialogiFrame").attr("src", encodeURI(scr + "&attachmentLevel=" + attachmentLevel + "&columnName=" + columnName));
		//src = $j("#setAttributesDialogiFrame").attr("src");
		
		$j("#setAttributesDialogiFrameDiv").dialog('open');
	}

	function openDialog(attachmentLevel) {
		attachmentLevel = encodeURIComponent(attachmentLevel);
		// add request params
		$j("#setAttributesDialogiFrame").attr("src", encodeURI(scr + "&attachmentLevel=" + attachmentLevel));
		//src = $j("#setAttributesDialogiFrame").attr("src");
		
		$j("#setAttributesDialogiFrameDiv").dialog('open');
	}
</script>

<b class="boxHeader">
	<spring:message code="sdmxhdintegration.general.keyFamily" />
</b>
<div class="box" style="margin-bottom: 20px">
	<table>
		<tr>
			<th align="left" width="300"><spring:message code="sdmxhdintegration.general.message" /></th>
			<td>${message.name}</td>
		</tr>
		<tr>
			<th align="left"><spring:message code="general.name" /></th>
			<td>${keyFamilyId}</td>
		</tr>
	</table>
</div>

<b class="boxHeader">
	<spring:message code="sdmxhdintegration.general.datasetAttributes" />
</b>
<div class="box" style="margin-bottom: 20px">
	<table style="margin-bottom: 10px" cellspacing="0">
		<tr>
			<th width="300"><spring:message code="general.name" /></th>
			<th><spring:message code="general.value" /></th>
		</tr>
		<c:forEach var="attribute" items="${attachedDatasetAttrs}" varStatus="row">
			<tr class="<c:choose><c:when test="${row.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
				<td>${attribute.key}</td>
				<td>${attribute.value}</td>
			</tr>
		</c:forEach>	
	</table>
	
	<input type="button" value="Add/edit" onclick="openDialog('Dataset')" />
	
	<spring:message code="sdmxhdintegration.attributes.containsAllMandatoryAttributes" />:
	<c:choose>
		<c:when test="${hasAllMandatoryDatasetAttrs}">
			<span style="color:#080; font-weight: bold"><spring:message code="general.yes" /></span>
		</c:when>
		<c:otherwise>
			<span style="color:#800; font-weight: bold"><spring:message code="general.no" /></span>
		</c:otherwise>
	</c:choose>
</div>

<b class="boxHeader">
	<spring:message code="sdmxhdintegration.general.reportColumns" />
</b>
<div class="box">
	<p class="description">Set the attributes that can be attached at the Series and Obs level for this SDMX-HD message. A tick will be show if all mandatory attributes have been filled in.</p>
	
	<table>
		<tr>
			<th>Column Name</th>
			<th>Column Label</th>
		</tr>
		<c:forEach var="column" items="${columns}">
			<tr>
				<td>${column.name}</td>
				<td>${column.label}</td>
				<td>
					<a href="#" onclick="openDialogForColumn('Series', '${column.name}')">Add Series level Attributes</a>
					<c:choose>
						<c:when test="${hasAllMandatorySeriesAttrs[column.name] == true}">
							<img src="${pageContext.request.contextPath}/images/checkmark.png" title="Mandatory Attributes Set" />
						</c:when>
						<c:otherwise>
							<img src="${pageContext.request.contextPath}/images/delete.gif" title="Mandatory Attributes NOT Set" />
						</c:otherwise>
					</c:choose>
				</td>
					
				<td>
					<a href="#" onclick="openDialogForColumn('Observation', '${column.name}')">Add Obs level Attributes</a>
					<c:choose>
						<c:when test="${hasAllMandatoryObsAttrs[column.name] == true}">
							<img src="${pageContext.request.contextPath}/images/checkmark.png" title="Mandatory Attributes Set" />
						</c:when>
						<c:otherwise>
							<img src="${pageContext.request.contextPath}/images/delete.gif" title="Mandatory Attributes NOT Set" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>

<div style="display: none">
	<div id="setAttributesDialogiFrameDiv">
		<iframe id="setAttributesDialogiFrame" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>