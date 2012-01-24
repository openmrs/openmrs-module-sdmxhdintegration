<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messages.list" />

<script type="text/javascript">
	var $j = jQuery.noConflict();

	var indMapUrl = "mappingIndDialog.form?sdmxhdMessageId=${messageId}&keyfamilyid=${keyFamilyId}";
	var dimMapUrl = "mappingDimDialog.form?sdmxhdMessageId=${messageId}&keyfamilyid=${keyFamilyId}";

	$j(function() {

		$j("#mapDimDialogiFrameDiv").dialog({
			autoOpen: false,
			bgiframe: false,
			width: 640,
			height: 480,
			modal: true,
			resizable: true,
			title: "<spring:message code="@MODULE_ID@.mapping.dimdialogtitle" />"
		});

		$j("#mapIndDialogiFrameDiv").dialog({
			autoOpen: false,
			bgiframe: false,
			width: 640,
			height: 160,
			modal: true,
			resizable: true,
			title: "<spring:message code="@MODULE_ID@.mapping.inddialogtitle" />"
		}); 
		
	});

	function indDialog(sdmxhdInd) {
		sdmxhdInd = encodeURIComponent(sdmxhdInd);
		// add request params
		$j("#mapIndDialogiFrame").attr("src", encodeURI(indMapUrl + "&sdmxhdIndicator=" + sdmxhdInd));
		src = $j("#mapIndDialogiFrame").attr("src");
		
		$j("#mapIndDialogiFrameDiv").dialog('open');
	}

	function dimDialog(sdmxhdDim) {
		sdmxhdDim = encodeURIComponent(sdmxhdDim);
		// add request params
		$j("#mapDimDialogiFrame").attr("src", encodeURI(dimMapUrl + "&sdmxhdDimension=" + sdmxhdDim));
		src = $j("#mapDimDialogiFrame").attr("src");
		
		$j("#mapDimDialogiFrameDiv").dialog('open');
	}
	
	-->
</script>
	
<form action="mapping.form" method="post">
	<input type="hidden" name="sdmxhdmessageid" value="${sdmxhdmessageid}">
	
	<table>
		<tr>
			<td><img width="25" height="25" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/glass_numbers_1.png" title="Step 1" /></td>
			<td class="description"><spring:message code="@MODULE_ID@.mapping.dimdescription" /></td>
		</tr>
	</table>
	
	<span class="boxHeader">
		<b><spring:message code="Dimensions" /></b>
		<a onclick="$j('#dimBox').slideToggle()"> <spring:message code="@MODULE_ID@.general.show-hide" /></a>
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
			<td class="description"><spring:message code="@MODULE_ID@.mapping.inddescription" /></td>
		</tr>
	</table>
	
	<span class="boxHeader">
		<b><spring:message code="Indicators" /></b>
		<a onclick="$j('#indBox').slideToggle()"> <spring:message code="@MODULE_ID@.general.show-hide" /></a>
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


