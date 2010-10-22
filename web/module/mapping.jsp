<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="include.jsp"%>

<script type="text/javascript">
	<!--

	var indMapUrl = "mappingIndDialog.form?sdmxhdMessageId=${sdmxhdmessageid}&keyfamilyid=${keyfamilyid}";
	var dimMapUrl = "mappingDimDialog.form?sdmxhdMessageId=${sdmxhdmessageid}&keyfamilyid=${keyfamilyid}";

	$(function() {

		$("#mapDimDialogiFrameDiv").dialog({
			autoOpen: false,
			bgiframe: false,
			width: 640,
			height: 480,
			modal: true,
			resizable: true,
			title: "Dimension Mapping"
		});

		$("#mapIndDialogiFrameDiv").dialog({
			autoOpen: false,
			bgiframe: false,
			width: 640,
			height: 160,
			modal: true,
			resizable: true,
			title: "Indicator Mapping"
		}); 
		
	});

	function indDialog(sdmxhdInd) {
		sdmxhdInd = encodeURIComponent(sdmxhdInd);
		// add request params
		$("#mapIndDialogiFrame").attr("src", encodeURI(indMapUrl + "&sdmxhdIndicator=" + sdmxhdInd));
		src = $("#mapIndDialogiFrame").attr("src");
		
		$("#mapIndDialogiFrameDiv").dialog('open');
	}

	function dimDialog(sdmxhdDim) {
		sdmxhdDim = encodeURIComponent(sdmxhdDim);
		// add request params
		$("#mapDimDialogiFrame").attr("src", encodeURI(dimMapUrl + "&sdmxhdDimension=" + sdmxhdDim));
		src = $("#mapDimDialogiFrame").attr("src");
		
		$("#mapDimDialogiFrameDiv").dialog('open');
	}
	
	-->
</script>
	
	<a href="messageUpload.form">Upload New SDMX-HD Template</a> | <a href="viewSDMXHDMessages.list">View All SDMX-HD Message Templates</a>

	<h2><spring:message code="SDMX-HD Message Mapping" /></h2>

	<form action="mapping.form" method="POST">
		<input type="hidden" name="sdmxhdmessageid" value="${sdmxhdmessageid}">
		
		<table>
			<tr>
				<td><img width="25" height="25" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/glass_numbers_1.png" title="Step 1" /></td>
				<td class="description">Map the dimensions from this SDMX-HD DataSet Definition to dimensions that exist in OpenMRS. If an appropriate dimension doesn't exist, it can be created using the reporting framework.</td>
			</tr>
		</table>
		
		<span class="boxHeader">
			<b><spring:message code="Dimensions" /></b>
			<a onclick="$('#dimBox').slideToggle()"> [show/hide]</a>
		</span>
		<table id="dimBox" class="box">
		
			<c:forEach var="dim" items="${sdmxhdDimensions}">
				<tr>
					<td>
						${dim.name} 
						<c:choose>
							<c:when test="${(mappedDimensions[dim.name] != null) || (fixedDimensionValues[dim.name] != null)}">
								<i><b>Mapped</b></i> <a href="#" onclick="dimDialog('${dim.name}')">[edit]</a>
							</c:when>
							<c:otherwise>
								<a href="#" onclick="dimDialog('${dim.name}')">[map]</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		
		</table>
		
		<br>
		
		<table>
			<tr>
				<td><img width="25" height="25" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/glass_numbers_2.png" title="Step 1" /></td>
				<td class="description">Map the indicators from this SDMX-HD DataSet Definition to indicators that exist in OpenMRS. If an appropriate indicator doesn't exist, it can be created using the reporting framework. Indicators often relate to the dimensions listed above so, it is best to map all the dimensions first otherwise it may not be possible to map some of the indicators.</td>
			</tr>
		</table>
		
		<span class="boxHeader">
			<b><spring:message code="Indicators" /></b>
			<a onclick="$('#indBox').slideToggle()"> [show/hide]</a>
		</span>
		<table id="indBox" class="box">
		
			<c:forEach var="ind" items="${sdmxhdIndicators}">
				<tr>
					<td>
						${ind} 
						<c:choose>
							<c:when test="${mappedIndicators[ind] != null}">
								<i><b>Mapped</b></i> <a href="#" onclick="indDialog('${ind}')">[edit]</a>
							</c:when>
							<c:otherwise>
								<a href="#" onclick="indDialog('${ind}')">[map]</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</c:forEach>
		
		</table>
		
	</form>
	
	<div style="display: none">
		<div id="mapDimDialogiFrameDiv">
			<iframe id="mapDimDialogiFrame" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
		</div>

		<div id="mapIndDialogiFrameDiv">
			<iframe id="mapIndDialogiFrame" width="100%" height="100%" marginWidth="0" marginHeight="0" frameBorder="0" scrolling="auto"></iframe>
		</div>
	</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>


