<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
 
 		 <sec:authentication var="role" property="principal.role" />
 		 
 			
 		  <ul class="nav nav-sidebar">
            <li class="active"><a href="#">Overview <span class="sr-only">(current)</span></a></li>
            
            <c:url var="newsSourcesUrl" value="/Admin/newsSources" />
            <c:url var="addNewsSourceUrl" value="/Admin/addNewsSource" />
            
			<li> <a href="${newsSourcesUrl}"> News Sources </a> </li>
			<li><a href="${addNewsSourceUrl}"> Add News Source </a></li>
			
          </ul>
          
          <!-- <ul class="nav nav-sidebar">
            <li><a href="">Nav item again</a></li>
            <li><a href="">One more nav</a></li>
            <li><a href="">Another nav item</a></li>
          </ul> -->