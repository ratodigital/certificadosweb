<html>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
  	<meta name="keywords" content="certificado, pdf, mala direta, envio, email, template, libreoffice, csv" />
		<meta name="description" content="<%if (!params.title) {%>Certificados PDF<%} else {%>Certificados PDF - $params.title<%}%>" />
		<meta name="author" content="Serge Rehem" />
		<meta name="robots" content="follow, index" />		
    <link rel="shortcut icon" href="/favicon.ico">

    <!-- Bootstrap core CSS -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
		<script src="/js/jquery-1.7.2.min.js"></script>
		
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="/js/html5shiv.js"></script>
      <script src="/js/respond.min.js"></script>
    <![endif]-->
  </head>

  <body>
  
  	<!-- Main jumbotron for a primary marketing message or call to action
	<div class="jumbotron">
	  <div class="container">
	    <h1>Certificados PDF <small>Beta</small></h1>
	    <p class="text-center">A maneira mais fácil e rápida de enviar certificados de participação em cursos e eventos por e-mail. Com apenas 3 passos todos os participantes recebem um email padrão com o certificado anexado em formato PDF.</p>
	  </div>
  </div>
   -->

	<div class="container">
	
	    <%if (user) {%>
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
				  <span class="sr-only">Toggle navigation</span>
				  <span class="icon-bar"></span>
				  <span class="icon-bar"></span>
				  <span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/">CertificadosPDF</a>
			</div>

			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav navbar-right">
					<%if (user != null) {%>
					<li class="dropdown user-dropdown">
					  <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-user"></i> ${user?.nickname}<b class="caret"></b></a>
					  <ul class="dropdown-menu">
					    <li><a href="${users.createLogoutURL('/')}"><i class="fa fa-sign-out"></i> Logout</a></li>
					  </ul>
					</li>
					<%} else {%>
					<li><a href="/login?continueTo=http://${request.serverName}:${request.serverPort}"><i class="fa fa-sign-in"></i> Login </a></li>
					<%}%>
				</ul>
			</div><!-- /.navbar-collapse -->
		</nav><!-- /.nav -->
        <br/>		
		<%}%>



