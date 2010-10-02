<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
</head>
<body>
ワークシート一覧
<form action="${f:url("start")}" method="POST">
<input type="hidden" name="ssKey" value="${ssKey}" />
<ul>
<c:forEach items="${list}" var="row">
<li>
  <input type="checkbox" name="wsIDArray" value="${row.wsID}" id="title_${row.wsID}" />
  <label for="title_${row.wsID}">${row.wsTitle}:${row.rowCount}</label>
  <a href="${f:url("tasks/restore")}?token=${sessionScope.token}&ssKey=${ssKey}&wsID=${row.wsID}&rowNum=0" target="_blank">test</a>
</li>
</c:forEach>
</ul>
<input type="submit" value="実行" />
</form>
</body>
</html>
