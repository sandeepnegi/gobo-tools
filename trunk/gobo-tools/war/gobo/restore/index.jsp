<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
</head>
<body>
<div style="position:absolute; top:10px; right:10px;"><a href="../index.gobo">Menu</a>&nbsp;<a href="../logout.gobo">logout</a></div>
<div>Please select a spreadsheet to restore.</div>
<ul>
<c:forEach items="${list}" var="row">
<li><a href="sheet.gobo?ssKey=${row.key}">${row.title}</a></li>
</c:forEach>
</ul>
</body>
</html>
