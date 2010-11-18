<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
<link rel="stylesheet" href="/gobo/css/global.css" />
</head>
<body>
<h1>Gobo Tools</h1>
<div style="text-align:center;">
  <div class="button"><a href="dump/index.gobo"/>Dump</a></div>
  <div class="button"><a href="restore/index.gobo"/>Restore</a></div>
  <div class="button"><a href="drop/index.gobo" style="font-size:30px;" />Drop</a></div>
</div>
</body>
</html>
