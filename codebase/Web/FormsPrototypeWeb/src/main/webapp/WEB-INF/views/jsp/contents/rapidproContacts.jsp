<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<div id="wizard" class="card wizard-card ct-wizard-orange">

	<!--        You can switch "ct-wizard-orange"  with one of the next bright colors: "ct-wizard-blue", "ct-wizard-green", "ct-wizard-orange", "ct-wizard-red"             -->

	<div class="wizard-header">
		<h3>
			 Contacts <br> 
			 <small>This is list of total contacts added so far</small>
		</h3>
	</div>

	<h2></h2>
	<c:choose>
		<c:when test="${fn:length(contacts)==0}">
			<em></em>
			<div class="alert alert-info">
				<!-- <button type="button" class="close" data-dismiss="alert">&times;</button> -->
				<strong>No Contacts Added Yet </strong> <br />You don't have any Contacts right now
			</div>
		</c:when>
		<c:otherwise>
			<table class="table table-striped">
				<thead>
					<tr>
						<th>Id</th>
						<th>Name</th>
						<th>Phone</th>
						<th>Uuid</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="count" value="0" />
					<c:forEach items="${contacts}" var="contact" varStatus="contactsLoop">
						<tr>
							<td>${contact.id}</td>
							<td>${contact.name}</td>
							<td>${contact.phone}</td>
							<td>${contact.uuid}</td>
							<c:url var="rapidproStartFlowUrl" value="/prototype/rapidpro/runSampleAncFlow/${contact.id}" />
							<td><a href="${rapidproStartFlowUrl}"> RunSampleFlow </a></td>
					</c:forEach>
				</tbody>
			</table>

		</c:otherwise>
	</c:choose>

</div>


