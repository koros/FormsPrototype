<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!doctype html>
<html>
<head>
<meta charset="utf-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Error 404</title>

<style>
.center {
	text-align: center;
	margin-left: auto;
	margin-right: auto;
	margin-bottom: auto;
	margin-top: 100px;
}
</style>


<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/kot7hack.css"/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/bootstrap.css"/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/bootstrap-responsive.css"/>"/>

<!-- HTML5 shim for IE backwards compatibility -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->



</head>

<body>
	<div class=" center">
		<h1>
			Page Not Found <small><font face="Tahoma" color="red">Error	404</font></small>
		</h1>
		<br />
		<p>
			The page you requested could not be found, either contact your
			webmaster or try again. Use your browsers <b>Back</b> button to
			navigate to the page you have prevously come from
		</p>
		<p>
			<b>Or you could just press this neat little button:</b>
		</p>
		<a href="/Kot7H/Admin/adminHome" class="btn btn-large btn-info"><i class="icon-home icon-white"></i> Take Me Home</a>
	</div>
	<br />

	<br />
	
	<br />
	<p></p>
	
	
<script src= "<c:url value="/static/resources/js/jquery_1.9.1.js"/>" > </script>
<script src=" <c:url value="/static/resources/js/bootstrap.js"/>  "> </script>
<script src= "<c:url value="/static/resources/js/jquery.chained.js"/>" > </script>

</body>
</html>
	