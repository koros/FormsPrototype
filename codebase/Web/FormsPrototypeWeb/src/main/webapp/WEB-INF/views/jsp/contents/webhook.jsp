<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<div id="wizard" class="card wizard-card ct-wizard-orange">

	<!--        You can switch "ct-wizard-orange"  with one of the next bright colors: "ct-wizard-blue", "ct-wizard-green", "ct-wizard-orange", "ct-wizard-red"             -->

	<div class="wizard-header">
		<h3>
			 Requests <br> 
			 <small>This is list of total requests received so far</small>
		</h3>
	</div>

	<h2></h2>
	<c:choose>
		<c:when test="${fn:length(requestsLogs)==0}">
			<em></em>
			<div class="alert alert-info">
				<!-- <button type="button" class="close" data-dismiss="alert">&times;</button> -->
				<strong>No Requests Added Yet </strong> <br />You don't have any Requests right now
			</div>
		</c:when>
		<c:otherwise>
			<table class="table table-striped">
				<thead>
					<tr>
						<th>Id</th>
						<th>Time</th>
						<th>Params</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="count" value="0" />
					<c:forEach items="${requestsLogs}" var="request" varStatus="formsLoop">
						<tr>
							<td>${request.id}</td>
							<td>${request.time}</td>
							<td>${request.allRequestParams}</td>
							
							<c:url var="editUrl" value="#" />
							<td><a href="${editUrl}"> View </a></td>
					</c:forEach>
				</tbody>
			</table>

		</c:otherwise>
	</c:choose>

</div>


