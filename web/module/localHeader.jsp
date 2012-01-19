<h2><spring:message code="@MODULE_ID@.title"/></h2>

<ul id="menu">
	<li class="first<c:if test='<%= request.getRequestURI().contains("/messageUpload") %>'> active</c:if>">
		<a href="messageUpload.form"><spring:message code="@MODULE_ID@.general.uploadlink"/></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("/viewSDMXHDMessages") %>'>class="active"</c:if>>
		<a href="viewSDMXHDMessages.list"><spring:message code="@MODULE_ID@.general.viewlink"/></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("/configPage") %>'>class="active"</c:if>>
		<a href="configPage.list"><spring:message code="@MODULE_ID@.general.configlink"/></a>
	</li>
</ul>