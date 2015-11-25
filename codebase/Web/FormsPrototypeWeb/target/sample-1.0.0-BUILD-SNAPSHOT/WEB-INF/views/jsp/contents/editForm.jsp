<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<form:form action="editFormAction" modelAttribute="form" class="form-horizontal">
<fieldset>

<!-- Form Name -->
<legend>Add  a new Form</legend>

<!-- Text input-->
<div class="form-group">
  <label class="col-md-4 control-label" for="formName">Name</label>  
  <div class="col-md-4">
  <form:input id="formName" path="formName" name="formName" placeholder="Name" class="form-control input-md" required="" type="text" />
  </div>
</div>

<!-- Text input-->
<div class="form-group">
  <label class="col-md-4 control-label" for="formUrl">URL</label>  
  <div class="col-md-4">
  <form:input id="formUrl" path="formUrl" name="formUrl" placeholder="http://sample.com" class="form-control input-md" type="text" />
  </div>
</div>

<!-- Text input-->
<div class="form-group">
  <label class="col-md-4 control-label" for="formNode">Form</label>  
  <div class="col-md-4">
  <form:textarea rows="10" path="formNode" class="form-control" id="formNode" name="formNode"></form:textarea>
  </div>
</div>


<!-- Text input-->
<div class="form-group">
  <label class="col-md-4 control-label" for="modelNode">Form</label>  
  <div class="col-md-4">
  <form:textarea rows="10" path="modelNode" class="form-control" id="modelNode" name="modelNode"></form:textarea>
  </div>
</div>
<hr />
	<!-- <div class="row-fluid">
		<div class="span6 bgcolor">
			<button type="submit" class="btn btn-success span4">Save changes</button>
		</div>
		/span
	</div> -->

</fieldset>
</form:form>