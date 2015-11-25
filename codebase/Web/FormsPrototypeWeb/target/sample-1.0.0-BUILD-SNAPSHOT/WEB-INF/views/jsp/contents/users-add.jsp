<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
 
 <sec:authentication var="role" property="principal.role" />

<div class="container-fluid">
	<form:form commandName="newUser" action="addUser" id="addUserForm">
		<legend> Add a New User </legend>
		<div class="row-fluid">
			<div class="span12">
			
				<div class="row-fluid">
                            <div class="span12 bgcolor">
                              <div class="alert alert-info">
                              	<a href="#" class="close" data-dismiss="alert">×</a>
                                Add a new User by filling the information below.
                              </div>
                            </div>
                          </div> 
				
				<div class="row-fluid">
					<div class="span6 lightblue">
						<form:label path="firstName" class="control-label" for="firstName">First Name:</form:label>
						<form:input type="text" value=" " path="firstName" class="input-xlarge span12" id="firstName"	placeholder="First Name" />
					</div>
					<!--/span-->
				</div>
				<!--/row-->
				
				<div class="row-fluid">
					<div class="span6 lightblue">
						<form:label path="otherNames" class="control-label" for="otherNames">Other Names:</form:label>
						<form:input type="text" value=" " path="otherNames" class="input-xlarge span12" id="otherNames"	placeholder="Other Names" />
					</div>
					<!--/span-->
				</div>
				<!--/row-->
				
				<div class="row-fluid">
					<div class="span6 lightblue">
						<form:label path="email" class="control-label" for="email">Email:</form:label>
						<form:input type="text" value=" " path="email" class="input-xlarge span12" id="email"	placeholder="joedoe@gmail.com" />
					</div>
					<!--/span-->
				</div>
				<!--/row-->
				
				<div class="row-fluid">
					<div class="span6 lightblue">
						<form:label path="phoneNumber" class="control-label" for="phoneNumber">Phone:</form:label>
						<form:input type="text" value=" " path="phoneNumber" class="input-xlarge span12" id="phoneNumber"	placeholder="0720112112" />
					</div>
					<!--/span-->
				</div>
				<!--/row-->
				
				<div class="row-fluid">
					<div class="span6 lightblue">
						<form:label path="role" class="control-label" for="role">Role:</form:label>
						
						<form:select class="span12" path="role" id="role">
                             <c:forEach items="${roles}" var="urole">
									<option value="${urole}" class="${urole}"> <c:out value="${urole}" /></option>
							</c:forEach>
						</form:select>
						
					</div>
					<!--/span-->
				</div>
				<!--/row-->
				
				<!--Show the caretaker options-->
				<c:if test="${role=='Landlord'}" >
				
					<c:choose>
					<c:when test="${fn:length(apartments)==0}">
						<em></em>
						    <div class="alert alert-info">
    							<strong>No registered Apartments </strong> 
    							<br />You have not added any apartments yet, use Apartments tab to add apartments
    						</div>
					</c:when>
					<c:otherwise>
					
						<c:forEach items="${apartments}" var="apartment">
							
							<div class="row-fluid">
								<div class="span6 lightblue">
									<input type = "checkbox" name = "apartment_ids" value = "${apartment.id}" /> <c:out value = "${apartment.name}" />
								</div>
								<!--/span-->
							</div>
							<!--/row-->
						</c:forEach>
						
					</c:otherwise>
				</c:choose>
					
				</c:if>
				
				<hr />
				<div class="row-fluid">
					<div class="span6 bgcolor">
						<button type="submit" class="btn btn-success span4">Save changes</button>
						<button class="btn span4">Cancel</button>
					</div>
					<!--/span-->
				</div>
				<br />

			</div>
		</div>
	</form:form>
</div>


