<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="localInclude.jsp" %>

<script type="text/javascript">
	var url = "${param["url"]}";
	window.parent.location = url.replace("%26", "&");
</script>