<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
 
 <sec:authentication var="role" property="principal.role" />

<div class="tabbable">

	<!-- Only required for left/right tabs -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="#userList" data-toggle="tab"> Manage Users </a> </li>
		<li><a href="#addUser" data-toggle="tab">Add New User</a></li>
		<li><a href="#tab3" data-toggle="tab">Misc...</a></li>
	</ul>
	
	<div class="tab-content">

		<div class="tab-pane active" id="userList">
			<tiles:insertAttribute name="userList-content" />
		</div>
		
		<div class="tab-pane" id="addUser">
			<tiles:insertAttribute name="addUser-content" />
		</div>
		
		<div class="tab-pane" id="tab3">
			<p>Set up edit tab here..</p>
		</div>

	</div>
</div>

<p></p>
