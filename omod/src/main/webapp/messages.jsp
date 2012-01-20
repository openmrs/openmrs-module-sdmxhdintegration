<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localInclude.jsp" %>
<%@ include file="localHeader.jsp" %>

<openmrs:require privilege="Manage SDMX-HD Integration" otherwise="/login.htm" redirect="/module/sdmxhdintegration/messages.list" />

<script type="text/javascript" charset="utf-8">
	var $j = jQuery.noConflict();
	
	$j(document).ready(function() {
		$j(".reporting-data-table").dataTable( {
			"bPaginate": true,
			"iDisplayLength": 25,
			"bLengthChange": false,
			"bFilter": true,
			"bSort": true,
			"bInfo": true,
			"bAutoWidth": false
		} );

	} );

	var currentKeyFamily = "Help";

	function toggleKF(sdmxhdMsgID) {
		if (currentKeyFamily != null) {
			$j("#keyFamilyTable" + currentKeyFamily).hide();
			$j("#selectedIcon" + currentKeyFamily).hide();
		}
		$j("#keyFamilyTable" + sdmxhdMsgID).toggle();
		$j("#selectedIcon" + sdmxhdMsgID).toggle();
		currentKeyFamily = sdmxhdMsgID;
	}
</script>

<div id="page">

	<table width="100%">
		<tr>
			<td valign="top" width="50%">

				<div class="boxHeader">
					<spring:message code="@MODULE_ID@.general.messages" />
				</div>
				<div class="box">
					<table width="100%">
						<tr>
							<th><spring:message code="general.name" /></th>
							<th><spring:message code="general.description" /></th>
							<th><spring:message code="general.creator" /></th>
							<th><spring:message code="@MODULE_ID@.general.attributes" /></th>
							<th><spring:message code="general.delete" /></th>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
						</tr>
						<c:forEach var="sdmxhdMessage" items="${sdmxhdMessages}" varStatus="index1">
							<tr class="<c:choose><c:when test="${index1.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
								<td>				
									<a href="messageUpload.form?messageId=${sdmxhdMessage.id}">${sdmxhdMessage.name}</a>
								</td>
								<td>${sdmxhdMessage.description}</td>
								<td>${sdmxhdMessage.creator}</td>
								<td align="center">
									<a href="messageAttributes.form?messageId=${sdmxhdMessage.id}">
										<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/preferences_system.png" align="absmiddle" border="0"/>	
									</a>
								</td>
								<td align="center">
									<a href="messages.list?deletemsgid=${sdmxhdMessage.id}" onclick="return confirm('Are you sure you want to delete this SDMX-HD Message?')">
										<img src='<c:url value="/images/trash.gif"/>' align="absmiddle" border="0"/>							
									</a>
								</td>
								<td align="right">
									<button onclick="toggleKF('${sdmxhdMessage.id}')">Show KeyFamilies...</button>
								</td>
								<td id="selectedIcon${sdmxhdMessage.id}" style="display:none" align="center">
									<img width="25" height="25" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/next.png" />
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>			
			</td>
			
			<td valign="top" width="50%">
				<div class="boxHeader">
					<spring:message code="@MODULE_ID@.general.keyFamilies" />
				</div>
				<div class="box">
					<span id="keyFamilyTableHelp"><i><spring:message code="@MODULE_ID@.messages.keyFamilyTableHelp" /></i></span>
					
					<c:forEach var="sdmxhdMessage" items="${sdmxhdMessages}">
						<div id="keyFamilyTable${sdmxhdMessage.id}" style="display:none">
							<table width="100%">
								<thead>
									<tr>
										<th>&nbsp;</th>
										<th>KeyFamily Name</th>
										<th>Map Indicators</th>
										<th>Add Attributes</th>
									</tr>
								</thead>
								<tbody>
									<c:set var="index2" value="0" />
									<c:forEach var="keyFamilyMapping" items="${keyFamilyMappings}">
										<c:if test="${keyFamilyMapping.sdmxhdMessage.id == sdmxhdMessage.id}">
											<tr class="<c:choose><c:when test="${index2 % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
												<td align="center">
													<c:choose>
														<c:when test="${keyFamilyMapping.reportDefinitionId != null}">
															<a href="${pageContext.request.contextPath}/module/reporting/run/runReport.form?reportId=${reportUuidMapping[keyFamilyMapping.keyFamilyId]}">
																<img src='<c:url value="/images/play.gif"/>' align="absmiddle" border="0"/>
															</a>
														</c:when>
														<c:otherwise>
															<img src='<c:url value="/images/alert.gif"/>' title="This report has no Indicator and Dimension mappings" align="absmiddle" border="0"/>
														</c:otherwise>
													</c:choose>
												</td>
												<td>
													<span>${keyFamilyNamesMap[keyFamilyMapping.keyFamilyId]}</span>
												</td>
												<td align="center">
													<a href="mapping.form?sdmxhdmessageid=${sdmxhdMessage.id}&keyfamilyid=${keyFamilyMapping.keyFamilyId}">
														<img src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/page_white_connect.png" title="Map Indicators and Dimensions" align="absmiddle" border="0"/>
													</a>
												</td>
												<td align="center">
													<a href="setAttributes.form?sdmxhdMessageId=${sdmxhdMessage.id}&keyfamilyid=${keyFamilyMapping.keyFamilyId}">
														<img src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/add.png" title="Add Attributes" title="Run Report" align="absmiddle" border="0"/>
													</a>
												</td>
											</tr>
											<c:set var="index2" value="${index2 + 1}" />
										</c:if>
									</c:forEach>
								<tbody>
							</table>
						</div>
					</c:forEach>
				</div>
			</td>		
		</tr>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
