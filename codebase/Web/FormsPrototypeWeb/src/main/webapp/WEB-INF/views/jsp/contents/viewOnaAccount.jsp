<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="wizard" class="card wizard-card ct-wizard-orange">

	<!-- You can switch "ct-wizard-orange"  with one of the next bright colors: "ct-wizard-blue", "ct-wizard-green", "ct-wizard-orange", "ct-wizard-red"  -->

	<div class="wizard-header">
		<h3>
			 Add/Edit Ona Account <br> 
		</h3>
	</div>
	
	<!-- ======================================================================== -->
	
	<form:form action="editOnaAccountAction" modelAttribute="user" class="form-horizontal">
	<fieldset>
	
	<!-- Form Name -->
	<legend></legend>
	
	<!-- Text input-->
	<div class="form-group">
	  <label class="col-md-4 control-label" for="name"> Username </label>  
	  <div class="col-md-4">
	  <form:input id="onaAccountName" path="onaAccountName" name="onaAccountName" placeholder="Ona Account Name" class="form-control input-md" required="" type="text" />
	  </div>
	</div>
	
	<!-- Text input-->
	<div class="form-group">
	  <label class="col-md-4 control-label" for="onaAccountPassword"> Password </label>
	  <div class="col-md-4">
	  <form:input id="onaAccountPassword" path="onaAccountPassword" name="onaAccountPassword" placeholder="Ona Password" class="form-control input-md" type="text" />
	  </div>
	</div>
	
	<hr />
		<div class="row-fluid">
			<div class="span6 bgcolor">
				<button type="submit" class="btn btn-success span6">Save changes</button>
			</div>
			<!--/span-->
		</div>
	
	</fieldset>
	</form:form>
    
	<!-- ======================================================================== -->
	
	
</div>
