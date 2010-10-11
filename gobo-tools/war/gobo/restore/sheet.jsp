<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript">
google.load("jquery", "1.4.2");
function initialize() {
	$("#checkall").click(function(){
		$("#form1 input[type='checkbox']").attr('checked', true);
        return false; 
	});
	$("#uncheckall").click(function(){
		$("#form1 input[type='checkbox']").attr('checked', false);
        return false; 
	});
}
google.setOnLoadCallback(initialize);
</script>
</head>
<body>
<div>Prease select worksheet(s) to restore.</div>
<form action="start.gobo" method="POST" id="form1">
<input type="hidden" name="ssKey" value="${ssKey}" />
<input type="button" value="Check All" id="checkall" />
<input type="button" value="Uncheck All" id="uncheckall" />
<ul>
<c:forEach items="${list}" var="row">
<li>
  <input type="checkbox" name="wsTitleArray" value="${row.wsTitle}" id="title_${row.wsTitle}" />
  <label for="title_${row.wsTitle}">${row.wsTitle}&nbsp;/&nbsp;<fmt:formatNumber>${row.rowCount}</fmt:formatNumber> records</label>
</li>
</c:forEach>
</ul>
<input type="submit" value="execute" />
</form>
</body>
</html>
