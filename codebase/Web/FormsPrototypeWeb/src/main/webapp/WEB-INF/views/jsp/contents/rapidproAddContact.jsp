<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<form:form action="addNewContactAction" modelAttribute="contact" class="form-horizontal">
<fieldset>

<!-- Form Name -->
<legend>Add  a new contact</legend>

<!-- Text input-->
<div class="form-group">
  <label class="col-md-4 control-label" for="name"> Name </label>  
  <div class="col-md-4">
  <form:input id="name" path="name" name="name" placeholder="Name" class="form-control input-md" required="" type="text" />
  </div>
</div>

<!-- Text input-->
<div class="form-group">
  <label class="col-md-4 control-label" for="country"> Phone </label>  
  <div class="col-md-4">
  <form:input id="phone" path="phone" name="phone" placeholder="+254726279315" class="form-control input-md" type="text" />
  </div>
</div>

<hr />
	<div class="row-fluid">
		<div class="span6 bgcolor">
			<button type="submit" class="btn btn-success span4">Save changes</button>
		</div>
		<!--/span-->
	</div>

</fieldset>
</form:form>