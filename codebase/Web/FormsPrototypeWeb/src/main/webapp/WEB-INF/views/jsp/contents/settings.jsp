<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<div id="wizard" class="card wizard-card ct-wizard-orange">

	<!--        You can switch "ct-wizard-orange"  with one of the next bright colors: "ct-wizard-blue", "ct-wizard-green", "ct-wizard-orange", "ct-wizard-red"             -->

	<div class="wizard-header">
		<h3>
			 More Options <br> 
		</h3>
	</div>
	
	<!-- ======================================================================== -->
		<c:url var="formsUrl" value="/prototype/forms" />
        <c:url var="addFormUrl" value="/prototype/addForm" />
        <c:url var="rapidproWebhookTestUrl" value="/prototype/webhook" />
        <c:url var="viewOnaAccountUrl" value="/prototype/viewOnaAccount" />
        <c:url var="linkFormsUrl" value="/prototype/linkForms" />
		
			<table class="table table-striped">
				<thead>
					<tr>
						<th> Actions </th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td> <i class="fa fa-pinterest"></i>  <a href="${rapidproWebhookTestUrl}"> View Requests Logs </a></td>
					</tr>
					<tr>
						<td> <i class="fa fa-pinterest"></i>  <a href="${viewOnaAccountUrl}"> View Ona account </a></td>
					</tr>
					<tr>
						<td> <i class="fa fa-pinterest"></i>  <a href="${linkFormsUrl}"> Link Forms </a></td>
					</tr>
				</tbody>
			</table>
			
		<!-- ======================================================================== -->
		
	    
</div>
		
		

		

