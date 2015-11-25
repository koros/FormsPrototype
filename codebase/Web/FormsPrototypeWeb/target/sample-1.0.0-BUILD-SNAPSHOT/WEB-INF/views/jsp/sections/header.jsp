<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">

		<button type="button" class="btn btn-navbar" data-toggle="collapse"
			data-target=".nav-collapse">
			<span class="icon-bar"></span> <span class="icon-bar"></span> <span
				class="icon-bar"></span>
		</button>

		<a class="brand" href="#">Kot System</a>

		<div class="nav-collapse">
			<ul class="nav">
				<c:url var="homeUrl" value="/Admin/adminHome" />
				<li class="active"><a href="${homeUrl}">Home</a></li>

				
			</ul>

			<ul class="nav pull-right">
				<li class="dropdown">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="icon-white icon-user"></i>  <sec:authentication property="name" /> <b class="caret"> </b></a>
					<ul class="dropdown-menu">
						<li><a href="<c:url value='/logout' />"><i class="icon-off"></i>  Sign Out</a></li>
						<li><a href="#"><i class="icon-wrench"> </i>  Settings</a></li>
						<li><a href="#"><i class="icon-lock"> </i>  Change Password</a></li>
						<li class="divider"></li>
						<li class="nav-header">My Account</li>
						<li><a href="#"><i class="icon-envelope"></i>   Messages</a></li>
						<li><a href="#">One more separated link</a></li>
					</ul>
				</li>
			</ul>
		</div>

	</div>
</div>