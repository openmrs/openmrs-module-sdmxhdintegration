<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/headerMinimal.jsp"%>
<%@ include file="localInclude.jsp" %>

<script type="text/javascript">
	var $j = jQuery.noConflict();
</script>

<form method="POST">
	<table>
		<tr>
			<td colspan="2"><label><b>Mandatory Attributes:</b></label></td>
		</tr>
		<c:forEach var="attr" items="${mandAttributes}">
			<tr>
				<td>${attr.conceptRef}</td>
				<td>
					<c:choose>
						<c:when test="${mandAttrDataTypes[attr.conceptRef] == 'Coded'}">
							<select name="attribute.${attr.conceptRef}">
								<option></option>
								<c:forEach var="code" items="${codelistValues[attr.conceptRef]}">
									<c:choose>
										<c:when test="${code.value == attributeValues[attr.conceptRef]}">
											<option value="${code.value}" selected="selected">${code.description}</option>
										</c:when>
										<c:otherwise>
											<option value="${code.value}">${code.description}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</c:when>
						<c:when test="${mandAttrDataTypes[attr.conceptRef] == 'Date'}">
							<script type="text/javascript">
								$j(function() {
									var dp = "#" + "${attr.conceptRef}" + "DatePicker";
									$j(dp).datepicker({dateFormat: "yy-mm-dd"});
								});
							</script>
								
							<input id="${attr.conceptRef}DatePicker" type="text" name="attribute.${attr.conceptRef}" value="${attributeValues[attr.conceptRef]}" />
						</c:when>
						<c:otherwise>
							<input type="text" name="attribute.${attr.conceptRef}" value="${attributeValues[attr.conceptRef]}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="2"><label><b>Conditional Attributes:</b></label></td>
		</tr>
		<c:forEach var="attr" items="${condAttributes}">
			<tr>
				<td>${attr.conceptRef}</td>
				<td>
					<c:choose>
						<c:when test="${condAttrDataTypes[attr.conceptRef] == 'Coded'}">
							<select name="attribute.${attr.conceptRef}">
								<option></option>
								<c:forEach var="code" items="${codelistValues[attr.conceptRef]}">
									<c:choose>
										<c:when test="${code.value == attributeValues[attr.conceptRef]}">
											<option value="${code.value}" selected="selected">${code.description}</option>
										</c:when>
										<c:otherwise>
											<option value="${code.value}">${code.description}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</c:when>
						<c:when test="${condAttrDataTypes[attr.conceptRef] == 'Date'}">
							<script type="text/javascript">
								$j(function() {
									var dp = "#" + "${attr.conceptRef}" + "DatePicker";
									$j(dp).datepicker({dateFormat: "yy-mm-dd"});
								});
							</script>
								
							<input id="${attr.conceptRef}DatePicker" type="text" name="attribute.${attr.conceptRef}" value="${attributeValues[attr.conceptRef]}" />
						</c:when>
						<c:otherwise>
							<input type="text" name="attribute.${attr.conceptRef}" value="${attributeValues[attr.conceptRef]}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		<tr><td><input type="submit" value="<spring:message code="general.save" />" /></td></tr>
	</table>
	
	<input type="hidden" id="messageId" value="${messageId}" />
	<input type="hidden" id="attachmentLevel" value="${attachmentLevel}" />
	<input type="hidden" id="keyFamilyId" value="${keyFamilyId}" />
</form>