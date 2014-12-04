<% include '/WEB-INF/includes/header.gtpl' %>

		<div class="row"> <!-- BARRA DE PROGRESSO -->
			<div class="progress">
				<%
				def percent = 33
				def title = "Template PDF (1/3)"
				def info = "Pdf"
				if (request.status == 'GETCSV') {
					percent = 66
					title = "Dados CSV (2/3)"
					info = "Csv"
				} else if (request.status == 'GETMSGDATA') {
					title = "Enviar Email (3/3)"
					percent = 100
					info = "Email"
				}
				%>
				<div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100" style="width: ${percent}%">
				</div>
			</div>
		</div> <!-- END: BARRA DE PROGRESSO -->
		
		<div class="row"> <!-- ICONES A DIREITA -->
			<div class="pull-left"> 
				<h1>$title</h1>
			</div>
			<div class="pull-right">
				<h1>
					<a href="#" data-toggle="tooltip" title="Saiba mais" onclick="showDiv${info}()">
						<span class="glyphicon glyphicon-info-sign"></span>
					</a> 
					<a href="/" data-toggle="tooltip" title="Página inicial">
						<span class="glyphicon glyphicon-home"></span>
					</a>
				<h1>
			</div>
		</div> <!-- END: ICONES A DIREITA -->
    
    	<hr>
	  
	  	<div class="row">
    	  
			<% if (request.statusError != null && request.statusError != "") {%>
				<div class="col-md-12">
					<div class="alert alert-danger alert-dismissable">
					  <a href="#" class="alert-link">$request.statusError</a>
					</div>
				</div>      
			<% } else if (request.flushError != null && request.flushError != "") {%>
				<div class="col-md-12">
					<div class="alert alert-danger alert-dismissable">
					  <a href="#" class="alert-link">$request.flushError</a>
					</div>
				</div>      
			<% } else if (request.flush != null && request.flush != "") {%>
				<div class="col-md-12">
					<div class="alert alert-success alert-dismissable">
					  <a href="#" class="alert-link">$request.flush</a>
					</div>
				</div>      
			<% } %>	
      
			<div class="col-md-2">  
				<%
				def image = ""
				if (request.status == 'GETPDF') {
					image = "pdf"
				} else if (request.status == 'GETCSV') {
					image = "csv"
				} else if (request.status == 'GETMSGDATA') {
					image = "email"
				}
				
				if (image != "") {
				%>	    
					<p class="text-center"><img src="/images/${image}.png" width="128px" height="128px" alt="PDF" ng-show="radioData != 'MC'"></p>	
					<p class="text-center"><img src="/images/mailchimp.png" width="128px" height="128px" alt="PDF" ng-show="radioData == 'MC'"></p>	
				<%}%>
			</div>	  
	    
	   		<div class="col-md-10">
				<%
				if (request.status == 'GETPDF' || request.status == 'GETCSV') {
				%>  
				<form id="pdfForm" class="form-horizontal" role="form" action="${blobstore.createUploadUrl('/main.groovy')}" 
				 method="post" enctype="multipart/form-data">
				<%  
				} else {
				%>  
				<form id="pdfForm" class="form-horizontal" role="form" action="/upload" method="post">
				<%  
				}
				%>

				<input name="pdfKey" type="hidden" value="$request.pdfKey"/>
				<input name="pdfName" type="hidden" value="$request.pdfName"/>  
				<input name="pdfFields" type="hidden" value="$request.pdfFields"/>    

				<%
				if (request.status == 'GETPDF') {
				%>       
				<input name="status" type="hidden" value="GETCSV"/>          

				<div class="form-group input-lg">
					<label for="file" class="col-lg-2 control-label">PDF</label>
					<span class="input-group-btn">
						<input type="file" class="input-lg" name="pdfFile" required/><br/>
					</span>
				</div>   		
						
				<div class="form-group input-lg">
					<div class="col-lg-2"></div>
					<div class="col-lg-6">
						<div class="checkbox">&nbsp;&nbsp;
							<label>
								<input type="checkbox" name="chkSalvarPDF" ng-model="chkSalvarPDF">Salvar template PDF para uso futuro
								<input type="text" class="form-control" size="10" name="templateName" id="templateName" placeholder="Nome do Template" ng-show="chkSalvarPDF" />
							</label>
						</div>						
					</div>
				</div>   
				<%  
				} else if (request.status == 'GETCSV') {
          		%>
          		<input type="hidden" name="status" value="GETMSGDATA"/>
				<div class="radio">
					<h3><input type="radio" id="radioData1" name="radioData" ng-model="radioData" value="CSV" checked="checked">Arquivo CSV</h3>
				</div>

				<div class="form-group input-lg" ng-show="radioData == 'CSV'">
					<label for="file" class="col-lg-2 control-label">CSV</label>
					<span class="input-group-btn">
						<input type="file" class="input-lg" name="csvFile" ng-required="radioData == 'CSV'"/><br/>
					</span>
				</div>  	
								
				<div class="radio">
					<h3><input type="radio" id="radioData2" name="radioData" ng-model="radioData" value="MC">Lista do MailChimp</h3>
				</div>          		
  
				<div class="form-group input-lg" ng-show="radioData == 'MC'">
					<div class="col-lg-2"></div>
					<div class="col-lg-6">
						<div class="input-group">
							<input type="text" class="form-control" size="10" name="apiKey" id="apiKey" placeholder="Mailcimp ApiKey" ng-model="apikey" ng-required="radioData == 'MC'"/>
							<span class="input-group-btn">
								<a class="btn btn-primary" ng-click="listasMailchimp()">Obter Listas</span></a><span ng-model="wait" ng-show="wait">Aguarde...</span>
							</span>
						</div>	
						<span ng-model="wait" ng-show="error != ''">{{error}}</span>
						<select class="form-control" id="selListaMC" name="selListaMC" ng-show="mailchimpLists.total > 0" ng-required="radioData == 'MC'"> 
							<option value="">--- Selecione a lista ---</option>										
							<option value="{{l.id}}" ng-repeat="l in mailchimpLists.list">{{l.name}} ({{l.size}})</option>
						</select>
					</div>
				</div> 				          
				<%
				} else if (request.status == 'GETMSGDATA') {
				%>
				$request.listaMC
          		<input type="hidden" id="status" name="status" value="SENDPDF"/>  
				<input name="csvKey" type="hidden" value="$request.csvKey"/>          
				<div class="form-group input-lg">
					<label for="fromEmail" class="col-lg-2 control-label">Email do rementente</label>
					<div class="col-lg-10">
						<input type="email" class="form-control input-lg" name="fromEmail" id="fromEmail" placeholder="Email" value="certificadospdf@gmail.com" disabled/>
					</div>
				</div>

				<div class="form-group input-lg">
					<label for="fromName" class="col-lg-2 control-label">Nome do remetente</label>
					<div class="col-lg-10">
						<input type="text" class="form-control input-lg" name="fromName" id="fromName" value="${params.fromName ?: 'Certificados PDF'}" required/>
					</div>
				</div>    

				<div class="form-group input-lg">
					<label for="replyTo" class="col-lg-2 control-label">Responder para</label>
					<div class="col-lg-10">
						<input type="email" class="form-control input-lg" name="replyTo" id="replyTo" placeholder="Responder para qual e-mail?" value="${params.replyTo ?: ''}" required/>
					</div>
				</div>
          
				<div class="form-group input-lg">
					<label for="subject" class="col-lg-2 control-label">Assunto *</label>
					<div class="col-lg-10">
						<input type="text" class="form-control input-lg" name="subject" id="subject" value="${params.subject ?: 'Seu certificado está pronto!'}" required/>
					</div>
				</div>    
						  
				<div class="form-group input-lg">
					<label for="message" class="col-lg-2 control-label">Mensagem *</label>
					<div class="col-lg-10">
						<textarea id="message" name="message" class="form-control input-lg" rows="8" required>
<%
// MANTENHA ESSE BLOCO NA ESQUERDA!!!
if (!params.message) {%>Olá,

Seu certificado de participação está disponível para download:
\$link

---
Certificados PDF
certificadospdf.appspot.com
<%} else {%>
$params.message
<%}%>
               			</textarea>
            		</div>
          		</div>              

				<div class="form-group">
					<div class="col-lg-10 col-lg-offset-2">
						<%if (request.pdfFields != "null" && request.pdfFields != null) {%> 
						* É obrigatório usar <b>\$link</b>. Você também poderá usar <b>$request.pdfFields</b>
						<%} else {%>                 
						* O template PDF não possui nenhum campo que possa ser utilizado.
						<%}%>
					</div>
				</div>    

				<br/> 
				<div class="form-actions">
				<%
				} 
				if (request.status == "SUCCESS") {
					submitButton = "Reiniciar <span class=\"glyphicon glyphicon-home\">"
				} else {
					submitButton = "Próximo <span class=\"glyphicon glyphicon-chevron-right\">"
				}
				if (request.status == 'GETMSGDATA') {
				%>
				<div class="pull-left">
					<button class="btn btn-primary" data-toggle="tooltip" data-placement="top" title="Veja uma prévia de como ficará o PDF" data-original-title="Veja uma prévia de como ficará o PDF" onclick="submitPreview();"><span class="glyphicon glyphicon-eye-open"></span> Preview</button>
				</div>
				<%
					submitButton = "Enviar certificados <span class=\"glyphicon glyphicon-ok\">"            
				}
				%>
				<div class="pull-right">
					<button class="btn btn-primary btn-lg" onclick="submitForm();">$submitButton</span></button>
				</div>
			</div> <!-- end: form-actions -->
        </form>	                             
      </div> <!-- end: col-md-10 -->
    </div> <!-- end: row -->

	<div class="container">
		<% include '/WEB-INF/includes/divPdf.gtpl' %>	
		<% include '/WEB-INF/includes/divEmail.gtpl' %>     
		<% include '/WEB-INF/includes/divCsv.gtpl' %>		      
	</div>
 
<% include '/WEB-INF/includes/footer.gtpl' %>

