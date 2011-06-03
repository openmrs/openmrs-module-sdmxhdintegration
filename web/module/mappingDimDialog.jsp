<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
<%@ include file="include.jsp"%>

<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<script type="text/javascript">
<!--
	$j(function() {
		$j("#mappedOMRSDimensionId").change(function() {
			var omrsDimensionId = $j("#mappedOMRSDimensionId").val();
			var sdmxhdIdRequestParam = 'sdmxhdMessageId=<c:out value="${sdmxhdMessageId}" />';
			var sdmxhdDimensionRequestParam = 'sdmxhdDimension=<c:out value="${sdmxhdDimension}" />';
			var omrsDimensionRequestParam = 'omrsDimension=' + omrsDimensionId;
			var keyFamilyRequestParam = 'keyfamilyid=<c:out value="${keyfamilyid}" />';
			window.location = 'mappingDimDialog.form?'
				+ sdmxhdIdRequestParam + '&'
				+ sdmxhdDimensionRequestParam + '&'
				+ omrsDimensionRequestParam + '&'
				+ keyFamilyRequestParam;
		});
	});

	function toggleComboBox(checkBox, id) {
		var comboBox = document.getElementById(id);
		if (checkBox.checked) {
			comboBox.disabled = false;
		} else {
			comboBox.disabled = true;
		}
	}
-->
</script>

<form method="POST">
<table>
	<tr>
		<td>
			<label>Dimension ${sdmxhdDimension} maps to -> </label>
		</td>
		<td>
			<select name="mappedOMRSDimensionId" id="mappedOMRSDimensionId" class="text ui-widget-content ui-corner-all" />
				<option value=""></option>
				<c:forEach var="omrsDimension" items="${omrsDimensions}">
					<c:choose>
						<c:when test="${mappedOMRSDimensionId == omrsDimension.id}">
							<option value="${omrsDimension.id}" selected="selected">${omrsDimension.name}</option>
						</c:when>
						<c:otherwise>
							<option value="${omrsDimension.id}">${omrsDimension.name}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>
	
	<input type="hidden" id="sdmxhdMessageId" value="${sdmxhdMessageId}" />
	<input type="hidden" id="sdmxhdDimension" value="${sdmxhdDimension}" />
	<input type="hidden" id="keyfamilyid" value="${keyfamilyid}" />

	<c:if test="${omrsDimensionOptions != null}">
		<c:forEach var="sdmxhdDimensionOptionElement" items="${sdmxhdDimensionOptions}">
			<tr>
				<td>
					<label> - ${sdmxhdDimensionOptionElement} -> </label>
				</td>
				<td>
					<select id="dimOptMapping.${sdmxhdDimensionOptionElement}" name="dimOptMapping.${sdmxhdDimensionOptionElement}" class="text ui-widget-content ui-corner-all" />
						<option value=""></option>
						<c:forEach var="omrsDimensionOptionElement" items="${omrsDimensionOptions}">
							<c:choose>
								<c:when test="${mappedDimOpts[sdmxhdDimensionOptionElement] == omrsDimensionOptionElement}">
									<option value="${omrsDimensionOptionElement}" selected="selected">${omrsDimensionOptionElement}</option>
								</c:when>
								<c:otherwise>
									<option value="${omrsDimensionOptionElement}">${omrsDimensionOptionElement}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</td>
			</tr>
		</c:forEach>
	</c:if>
	
	<tr>
		<td>
			<b>OR</b>
		</td>
	</tr>
	<tr>
		<td>
			<c:choose>
				<c:when test="${fixedValueCheckbox}">
					<input type="checkbox" name="fixedValueCheckbox" checked="checked" onClick="toggleComboBox(this, 'fixedValue')" />
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="fixedValueCheckbox" onClick="toggleComboBox(this, 'fixedValue')" />
				</c:otherwise>
			</c:choose>
			<lable>Fixed Value: </lable>
		</td>
		<td>
			<c:choose>
				<c:when test="${fixedValueCheckbox}">
					<select id="fixedValue" name="fixedValue">
				</c:when>
				<c:otherwise>
					<select id="fixedValue" name="fixedValue" disabled="disabled">
				</c:otherwise>
			</c:choose>
				<option value=""></option>
				<c:forEach var="sdmxhdDimensionOptionElement" items="${sdmxhdDimensionOptions}">
					<c:choose>
						<c:when test="${fixedValue == sdmxhdDimensionOptionElement}">
							<option value="${sdmxhdDimensionOptionElement}" selected="selected">${sdmxhdDimensionOptionElement}</option>
						</c:when>
						<c:otherwise>
							<option value="${sdmxhdDimensionOptionElement}">${sdmxhdDimensionOptionElement}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
		</td>
	</tr>

	<tr>
		<td><input type="submit" value="Save" /></td>
	</tr>
</table>
</form>

<%@ include file="/WEB-INF/template/footerMinimal.jsp"%>