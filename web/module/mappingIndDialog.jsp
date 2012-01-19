<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
<%@ include file="localInclude.jsp" %>

<form id="indMappingForm" method="POST">
	<label for="mapping">Map indicator to -> </label>
	<select name="mappedOMRSIndicatorId" id="mappedOMRSIndicatorId" class="text ui-widget-content ui-corner-all" />
		<c:forEach var="omrsIndicator" items="${omrsIndicators}">
			<c:choose>
				<c:when test="${mappedOMRSIndicatorId == omrsIndicator.id}">
					<option value="${omrsIndicator.id}" selected="selected">${omrsIndicator.name}</option>
				</c:when>
				<c:otherwise>
					<option value="${omrsIndicator.id}">${omrsIndicator.name}</option>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</select>
	<br />
	<input type="hidden" id="sdmxhdMessageId" value="${sdmxhdMessageId}" />
	<input type="hidden" id="sdmxhdIndicator" value="${sdmxhdIndicator}" />
	<input type="hidden" id="keyfamilyid" value="${keyfamilyid}" />
	<input type="submit" value="Save" />
</form>

<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>