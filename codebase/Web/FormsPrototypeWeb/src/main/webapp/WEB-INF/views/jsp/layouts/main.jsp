<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Prototype Admin</title>

    <!-- Font awesome core CSS -->
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/bootstrap.min.css"/>"/>
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/gsdk-base.css"/>"/>
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/font-awesome.css"/>"/>
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/bootstrap-toggle.css"/>"/>
	
    <!-- Custom styles for this template -->
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/landing-page.css"/>"/>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>

  <body style="background-image: url(<c:url value="/static/resources/images/wooden_background2.jpg"/>)">
  <div class="image-container set-full-height">
   <nav role="navigation" class="navbar navbar-transparent navbar-top">
	    <div class="container">
	    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
	      <button data-target="#example" data-toggle="collapse" class="navbar-toggle" type="button" id="menu-toggle">
	        <span class="sr-only">Toggle navigation</span>
	        <span class="icon-bar bar1"></span>
	        <span class="icon-bar bar2"></span>
	        <span class="icon-bar bar3"></span>
	      </button>
	      <a href="#">
	                   <div class="logo-container">
	                        <div class="logo">
	                            <img alt="Creative Tim Logo" src="<c:url value="/static/resources/images/new_logo.png"/>">
	                        </div>
	                        <div class="brand">
	                            Prototype
	                        </div>
	                    </div>
	              </a>
	    </div>
		
		<!-- ======================================================================== -->
		<c:url var="formsUrl" value="/prototype/forms" />
        <c:url var="addFormUrl" value="/prototype/addForm" />
        <c:url var="rapidproWebhookTestUrl" value="/prototype/webhook" />
        <c:url var="rapidproAddContactUrl" value="/prototype/rapidpro/addContact" />
        <c:url var="rapidproContactsUrl" value="/prototype/rapidpro/contacts" />
        <c:url var="settingsUrl" value="/prototype/settings" />
		
		<div id="example" class="collapse navbar-collapse">
	      <ul class="nav navbar-nav navbar-right">
	            <li>
	                <a href="${formsUrl}"> 
	                    <i class="fa fa-facebook-square"></i>
	                    Forms
	                </a>
	            </li>
	            <li>
	                <a href="${addFormUrl}"> 
	                    <i class="fa fa-twitter"></i>
	                    Add Form
	                </a>
	            </li>
	            <li>
	                <a href="${rapidproContactsUrl}"> 
	                    <i class="fa fa-pinterest"></i>
	                    Contacts
	                </a>
	            </li>
	            <li>
	                <a href="${rapidproAddContactUrl}"> 
	                    <i class="fa fa-pinterest"></i>
	                    Add Contact
	                </a>
	            </li>
	            <li>
	                <a href="${settingsUrl}"> 
	                    <i class="fa fa-pinterest"></i>
	                    More
	                </a>
	            </li>
	            
	            
	       </ul>
	      
	    </div><!-- /.navbar-collapse -->
			
		<!-- ======================================================================== -->
	
	    
	  </div>
	</nav>
    
    <!--   Big container   -->
    <div class="container">
        <div class="row">
        <div class="col-sm-8 col-sm-offset-2">
           
            <!-- main content area here -->
	        <tiles:insertAttribute name="primary-content" />
	        
        </div>
        </div><!-- end row -->
    </div> <!--  big container -->
    
    
     <div class="footer">
      <!-- <div class="container">
             Made with <i class="fa fa-heart heart"></i> by <a href="http://www.creative-tim.com">Creative Tim</a>. Free download <a href="http://www.creative-tim.com/product/wizard">here.</a>
      </div> -->
      
      <footer class="container">
        	<tiles:insertAttribute name="footer-content" />
      </footer>
      
    </div>
    
	
	<!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
	<script src= "<c:url value="/static/resources/js/jquery.min.js"/>" > </script>
	<script src=" <c:url value="/static/resources/js/bootstrap.min.js"/>  "> </script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src= "<c:url value="/static/resources/js/ie10-viewport-bug-workaround.js"/>" > </script>
	<script src= "<c:url value="/static/resources/js/jquery.chained.js"/>" > </script>
	<script src= "<c:url value="/static/resources/js/jquery.validate.min.js"/>" > </script>
	
	<script src= "<c:url value="/static/resources/js/jquery.bootstrap.wizard.js"/>" > </script>
	<script src= "<c:url value="/static/resources/js/wizard.js"/>" > </script>
	<script src= "<c:url value="/static/resources/js/bootstrap-toggle.js"/>" > </script>
	
	<tiles:insertAttribute name="includes-bottom-content" />
	
  </body>
  
	
</html>
