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
			<td valign="top" width="60%">
				<b class="boxHeader">
					<spring:message code="@MODULE_ID@.general.messages" />
				</b>
				<div class="box">
					<table width="100%" cellspacing="0">
						<tr>
							<th>&nbsp;</th>
							<th><spring:message code="general.name" /></th>
							<th><spring:message code="general.description" /></th>
							<th><spring:message code="@MODULE_ID@.messages.uploader" /></th>
							<th><spring:message code="@MODULE_ID@.messages.uploaded" /></th>
							<th align="center"><spring:message code="@MODULE_ID@.general.attributes" /></th>
							<th align="center"><spring:message code="@MODULE_ID@.general.keyFamilies" /></th>
							<th>&nbsp;</th>
						</tr>
						<c:forEach var="message" items="${messages}" varStatus="index1">
							<tr class="<c:choose><c:when test="${index1.index % 2 == 0}">evenRow</c:when><c:otherwise>oddRow</c:otherwise></c:choose>">
								<td align="center">
									<a href="messages.list?deleteMsgId=${message.id}" onclick="return confirm('<spring:message code="@MODULE_ID@.messages.deleteMessageConfirm" />')">
										<img src='<c:url value="/images/trash.gif"/>' align="absmiddle" border="0"/>							
									</a>
								</td>
								<td>				
									<a href="messageUpload.form?messageId=${message.id}">${message.name}</a>
								</td>
								<td>${message.description}</td>
								<td>${message.creator.personName}</td>
								<td><openmrs:formatDate date="${not empty message.dateChanged ? message.dateChanged : message.dateCreated}" /></td>
								<td align="center">
									<a href="messageAttributes.form?messageId=${message.id}">
										<img width="20" height="20" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/attributes.png" align="absmiddle" border="0"/>	
									</a>
								</td>
								<td align="center">
									<input type="button" value="Show &gt;" onclick="toggleKF('${message.id}')" />
								</td>
								<td id="selectedIcon${message.id}" style="display:none" align="center">
									<img width="25" height="25" src="${pageContext.request.contextPath}/moduleResources/sdmxhdintegration/images/next.png" />
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>			
			</td>
			
			<td valign="top" width="40%">
				<b class="boxHeader">
					<spring:message code="@MODULE_ID@.general.keyFamilies" />
				</b>
				<div class="box">
					<span id="keyFamilyTableHelp">
						<i><spring:message code="@MODULE_ID@.messages.keyFamilyTableHelp" /></i>
					</span>
					
					<c:forEach var="message" items="${messages}">
						<div id="keyFamilyTable${message.id}" style="display:none">
							<table width="100%" cellspacing="0">
							
								<tr>
									<th>&nbsp;</th>
									<th><spring:message code="general.name" /></th>
									<th><spring:message code="@MODULE_ID@.general.indicators" /></th>
									<th><spring:message code="@MODULE_ID@.general.attributes" /></th>
								</tr>
								
								<c:set var="index2" value="0" />
								
								<c:forEach var="keyFamilyMapping" items="${keyFamilyMappings}">
									<c:if test="${keyFamilyMapping.message.id == message.id}">
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
												<input type="button" value="Map..." onclick="location.href='mapping.form?messageId=${message.id}&amp;keyFamilyId=${keyFamilyMapping.keyFamilyId}'" />
											</td>
											<td align="center">
												 <input type="button" value="Edit..." onclick="location.href='keyFamilyAttributes.form?messageId=${message.id}&keyFamilyId=${keyFamilyMapping.keyFamilyId}'" />
											</td>
										</tr>
										<c:set var="index2" value="${index2 + 1}" />
									</c:if>
								</c:forEach>
							</table>
						</div>
					</c:forEach>
				</div>
			</td>		
		</tr>
	</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>
