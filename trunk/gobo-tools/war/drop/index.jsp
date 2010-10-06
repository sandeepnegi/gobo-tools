<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Drop</title>
</head>
<body>
<div>Please select the kind(s) to drop.</div>
<form action="${f:url("start")}" method="POST">
<ul>
<c:forEach items="${list}" var="row">
<li>
  <input type="checkbox" name="kindArray" value="${row.name}" id="kind_${row.name}" />
  <label for="kind_${row.name}">${row.name}&nbsp;/&nbsp;<fmt:formatNumber>${row.count}</fmt:formatNumber> records</label>
</li>
</c:forEach>
</ul>
<input type="submit" value="execute" />
</form>
</body>
</html>
