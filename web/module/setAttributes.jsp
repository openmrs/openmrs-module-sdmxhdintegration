<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="include.jsp"%>

<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<script type="text/javascript">
	<!--

	var scr = "setAttributesDialog.form?sdmxhdMessageId=${sdmxhdMessageId}&keyfamilyid=${keyfamilyid}";

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

	-->
</script>

<a href="messageUpload.form">Upload New SDMX-HD Template</a> | <a href="viewSDMXHDMessages.list">View All SDMX-HD Message Templates</a>

<h2><spring:message code="Attributes" /></h2>

<p class="description">Set the attributes that can be attached at the DataSet level for this SDMX-HD message. A tick will be show if all mandatory attributes have been filled in.</p>

<span class="boxHeader">
	<b><spring:message code="DataSet Attributes" /></b>
	<a onclick="$j('#dataSetAttrBox').slideToggle()"> [show/hide]</a>
</span>
<table id="dataSetAttrBox" class="box">
	<tr>
		<td>
			<a href="#" onclick="openDialog('DataSet')">Add DataSet level Attributes</a>
			<c:choose>
				<c:when test="${datasetMandAttrSet == true}">
					<img src="${pageContext.request.contextPath}/images/checkmark.png" title="Mandatory Attributes Set" />
				</c:when>
				<c:otherwise>
					<img src="${pageContext.request.contextPath}/images/delete.gif" title="Mandatory Attributes NOT Set" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
		
</table>

<br />

<p class="description">Set the attributes that can be attached at the Series and Obs level for this SDMX-HD message. A tick will be show if all mandatory attributes have been filled in.</p>

<span class="boxHeader">
	<b><spring:message code="Report Columns" /></b>
	<a onclick="$j('#columnsAttrBox').slideToggle()"> [show/hide]</a>
</span>
<table id="columnsAttrBox" class="box">
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
					<c:when test="${seriesMandAttrSet[column.name] == true}">
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
					<c:when test="${obsMandAttrSet[column.name] == true}">
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

<div style="display: none">
	<div id="setAttributesDialogiFrameDiv">
		<iframe id="setAttributesDialogiFrame" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
	</div>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>