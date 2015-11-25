<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<h2>Users</h2>
				<c:choose>
					<c:when test="${fn:length(users)==0}">
						<em></em>
						<div class="alert alert-info">
    							<!-- <button type="button" class="close" data-dismiss="alert">&times;</button> -->
    							<strong>No User Added Yet </strong> 
    							<br />You have not added any User yet, use New tab to add them
    					</div>
					</c:when>
					<c:otherwise>
						<table class="table table-striped">
							<thead>
								<tr>
									<th>Id</th>
									<th>Name</th>
									<th>Email</th>
									<th>Phone</th>
									<th>Role</th>
									<th>Enabled</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${users}" var="user">
									<tr>
										<td>${user.id}</td>
										<td>${user.firstName} ${user.otherNames}</td>
										<td>${user.email}</td>
										<td>${user.phoneNumber}</td>
										<td>${user.role}</td>
										<td>${user.enabled}</td>
								</c:forEach>
							</tbody>
						</table>
						
					</c:otherwise>
				</c:choose>
