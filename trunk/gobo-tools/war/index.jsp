<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<style>
.button a {
font-size:100px;
display: block;
background: #fff;
color: #0000ff;
text-decoration: none;
padding: 50px;
}
.button a:hover {
color: #ff0000;
background: #ddd;
}
</style>
</head>
<link rel="stylesheet" href="css/global.css" />
<body>
<h1>Gobo Tools</h1>
<div style="text-align:center;">
  <div class="button"><a href="${f:url("/dump/")}"/>Dump</a></div>
  <div class="button"><a href="${f:url("/restore/")}"/>Restore</a></div>
  <div class="button"><a href="${f:url("/drop/")}"/>Drop</a></div>
</div>
<div style="position:absolute; top:10px; right:10px;"><a href="${f:url("/logout")}">logout</a></div>
</body>
</html>
