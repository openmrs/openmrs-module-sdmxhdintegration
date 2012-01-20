<h2><spring:message code="@MODULE_ID@.title"/></h2>

<ul id="menu">
	<li class="first<c:if test='<%= request.getRequestURI().contains("/messageUpload") %>'> active</c:if>">
		<a href="messageUpload.form"><spring:message code="@MODULE_ID@.general.uploadlink"/></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("/messages") %>'>class="active"</c:if>>
		<a href="messages.list"><spring:message code="@MODULE_ID@.general.viewlink"/></a>
	</li>
	
	<li <c:if test='<%= request.getRequestURI().contains("/config") %>'>class="active"</c:if>>
		<a href="config.form"><spring:message code="@MODULE_ID@.general.configlink"/></a>
	</li>
</ul>