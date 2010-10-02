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
  <input type="checkbox" name="wsTitleArray" value="${row.wsTitle}" id="title_${row.wsTitle}" />
  <label for="title_${row.wsTitle}">${row.wsTitle}:${row.rowCount}</label>
</li>
</c:forEach>
</ul>
<input type="submit" value="実行" />
</form>
</body>
</html>
