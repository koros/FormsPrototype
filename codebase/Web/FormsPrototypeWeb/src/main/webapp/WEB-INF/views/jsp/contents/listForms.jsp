<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="wizard" class="card wizard-card ct-wizard-orange">

	<!-- You can switch "ct-wizard-orange"  with one of the next bright colors: "ct-wizard-blue", "ct-wizard-green", "ct-wizard-orange", "ct-wizard-red"  -->

	<div class="wizard-header">
		<h3>
			 Forms <br> 
			 <small>This is list of forms saved so far</small>
		</h3>
	</div>
	
	<!-- ======================================================================== -->
	<c:choose>
		<c:when test="${fn:length(forms)==0}">
			<em></em>
			<div class="alert alert-info">
				<!-- <button type="button" class="close" data-dismiss="alert">&times;</button> -->
				<strong>No Forms Added Yet </strong> <br />You don't have any
				forms right now
			</div>
		</c:when>
		<c:otherwise>
			<table class="table table-striped">
				<thead>
					<tr>
						<th></th>
						<th>Name</th>
						<th>Form Id</th>
						<th>Url</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:set var="count" value="0" />
					<c:forEach items="${forms}" var="form" varStatus="formsLoop">
						<tr>
							<td>${formsLoop.index+1}</td>
							<td>${form.formName}</td>
							<td>${form.formId}</td>
							<td>${form.formUrl}</td>
							
							<c:url var="editUrl" value="/editForm/${form.id}" />
							<td><a href="${editUrl}"> Edit </a></td>
					</c:forEach>
				</tbody>
			</table>

		</c:otherwise>
	</c:choose>
	
    
	<!-- ======================================================================== -->
	
	
</div>


