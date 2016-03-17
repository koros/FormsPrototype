<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="wizard" class="card wizard-card ct-wizard-orange">

	<!-- You can switch "ct-wizard-orange"  with one of the next bright colors: "ct-wizard-blue", "ct-wizard-green", "ct-wizard-orange", "ct-wizard-red"  -->

	<div class="wizard-header">
		<h3>
			 <br> 
		</h3>
	</div>
	
	<!-- ======================================================================== -->
	<form:form action="addNewFormAction" class="form-horizontal">
	<fieldset>
	
	<!-- Form Name -->
	<legend> Add  a new Form </legend>
	
	<!-- Text input-->
	<c:forEach items="${forms}" var="form" varStatus="formsLoop">
						
		<div class="form-group">
		  <label class="col-md-4 control-label" for="formName">Name</label>  
		  <div class="col-md-4">
		  <form:checkbox id="formName" path="formName" name="formName" value="${form.formName}" class="form-control input-md" />
		  </div>
		</div>
	
	</c:forEach>
	
	<hr />
		<div class="row-fluid">
			<div class="span6 bgcolor">
				<button type="submit" class="btn btn-success span4">Save changes</button>
			</div>
			<!--/span-->
		</div>
	
	</fieldset>
	</form:form>
    
	<!-- ======================================================================== -->
	
	
</div>