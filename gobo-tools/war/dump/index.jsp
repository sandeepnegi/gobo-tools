<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Dump</title>
</head>
<body>
<div>Please select the kind(s) to get dump.</div>
<form action="${f:url("start")}" method="POST">
<ul>
<c:forEach items="${list}" var="row">
<li>
  <input type="checkbox" name="kindArray" value="${row}" id="kind_${row}" />
  <label for="kind_${row}">${row}</label>
</li>
</c:forEach>
</ul>
<input type="submit" value="execute" />
</form>
</body>
</html>
