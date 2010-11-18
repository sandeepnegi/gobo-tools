<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<link rel="stylesheet" href="/gobo/css/global.css" />
</head>
<body>
<h1>Gobo Tools</h1>
<h2>Restore</h2>
<div style="position:absolute; top:10px; right:10px;"><a href="../index.gobo">Menu</a>&nbsp;<a href="../logout.gobo">logout</a></div>
<div id="main">
<div id="msg">Please select a spreadsheet to restore.</div>
<ul>
<c:forEach items="${list}" var="row">
<li><a href="sheet.gobo?ssKey=${row.key}">${row.title}</a></li>
</c:forEach>
</ul>
</div>
</body>
</html>
