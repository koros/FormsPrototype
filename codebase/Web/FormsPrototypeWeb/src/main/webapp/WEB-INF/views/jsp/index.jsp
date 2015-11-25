<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>

	<head>
		<title>Real Estate &amp; Property Management Management System</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/screen.css"/>"/>
	</head>

	<body>
		<div id="container">
			<div class="dualbrand">
				<img src="<c:url value="/static/resources/gfx/logo.jpg"/>"/>
			</div>
			<div id="content">
				<h1>Real Estate &amp; Property Management System</h1>

				<div>
					<p>You have successfully deployed a Spring MVC web application.</p>
					
					<img src="<c:url value="/static/resources/gfx/login_required.png"/>"/>
				</div>

				<form name='f' action="<c:url value='j_spring_security_check' />" method='POST'>
					<h2>Login:</h2>
					<p>Please use your email and password to login</p>
					<table>
						<tbody>
							<tr>
								<td><label for="j_username">Username</label></td>
								<td><input type="text" name="j_username" id="j_username"/></td>
								<td><!-- <errors class="invalid" path="email"/> --></td>
							</tr>
							<tr>
								<td><label for="j_password">Password</label>
								<td><input type="password" name="j_password" id="j_password"/></td>
								<td><!-- <errors class="invalid" path="password"/> --></td>
							</tr>
	
						</tbody>
					</table>
					<table>
						<tr>
							<td>
								<input type="submit" value="Login" class="button"/>
							</td>
						</tr>
					</table>
				</form>
				
					
			</div>
			<div id="aside">
				<p>Learn more about JBoss Enterprise Application Platform 6.</p>
				<ul>
					<li><a
						href="http://red.ht/jbeap-6-docs">Documentation</a></li>
					<li><a href="http://red.ht/jbeap-6">Product Information</a></li>
				</ul>
				<p>Learn more about JBoss AS 7.</p>
				<ul>
					<li><a
						href="https://docs.jboss.org/author/display/AS7/Getting+Started+Developing+Applications+Guide">Getting Started Developing Applications Guide</a></li>
					<li><a href="http://jboss.org/jbossas">Community Project Information</a></li>
				</ul>
			</div>
			<div id="footer">
			    <p>
					korosync inc. copyright &copy; 2013 <br />
			    </p>
			</div>
		</div>
	</body>
</html>
