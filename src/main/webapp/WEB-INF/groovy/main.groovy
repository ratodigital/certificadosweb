import util.*
import model.*

/**
 * WORKFLOW:
 * PASSO 1: GETPDF      - Obter template PDF
 * PASSO 2: GETCSV      - Obter arquivo CSV
 * PASSO 3: GETMSGDATA  - Obter dados para envio
 * PASSO 4: SEND_EMAILS - Enviar emails
 * PASSO 5: SUCCESS     - Foi tudo OK!
 *
 * ALTERNATIVAS:
 * PREVIEW: VISUALIZAR CERTIFICADO GERADO
 */

if (user == null) {
	redirect "/login"
	return
} 

def blob = new Blob()

if (!params.status) {
	request.status = "GETPDF" 
	forward "/WEB-INF/pages/main.gtpl"
	return 
} 

// MAIN CONTROLLER
request.status = params.status
switch (params.status) {
	case "GETCSV": 
		//PDF
		def blobs = blob.getUploadedBlobs(request)
		def pdfFile = blobs["pdfFile"]
		request.pdfKey = pdfFile.keyString
		request.pdfName = pdfFile.filename    
		request.pdfFields = getTemplateFieldsAsString(pdfFile)
		if (request.pdfFields == null) {
			request.status = "GETPDF"
			request.flushError = 'Selecione  um arquivo PDF válido.'
		}
		forward "/WEB-INF/pages/main.gtpl"
		break
	case "GETMSGDATA":
		//CSV
		def blobs = blob.getUploadedBlobs(request)
		def csvFile = blobs["csvFile"]
		request.pdfKey = params.pdfKey
		request.pdfName = params.pdfName
		request.pdfFields = params.pdfFields
		request.csvKey = csvFile.keyString      
		forward "/WEB-INF/pages/main.gtpl"
		break
	case "PREVIEW":     
		def pdfFile = blob.getFile(params.pdfKey)
		def csvFile = blob.getFile(params.csvKey)       
		def csvData = CSV.getCSVData(csvFile) 
       
		def outputPdfName = "preview.pdf"
		def pdfStamper = PDF.gerarPDF(pdfFile, csvData[0], outputPdfName)

		response.setHeader("Content-Type", "application/pdf");
		response.setHeader("Content-Length", String.valueOf(pdfStamper.blobKey.info.size));
		response.setHeader("Content-Disposition", "attachment;filename=\"$outputPdfName\"");
		blobstore.serve(pdfStamper.blobKey, response)
		break
	default: // PASSO 3. ENVIAR CERTIFICADOS
		if (params.message.indexOf("\$link") == -1) {
			flushError = 'Você obrigatoriamente deve usar o campo \$link.'
		} else {      

			def returnMap = Certificado.enviarCertificados(
				params.csvKey, 
				params.pdfKey, 
				params.message, 
				params.subject, 
				params.fromName, 
				params.replyTo,
				user.email,
				user.nickname,
				true) // Simula o envio de certificados, para testar se tem problema nos parâmetros

			request.statusError = returnMap.statusError			
			request.flushError = returnMap.flushError

			if (request.flushError == "" && request.statusError == "") {

				def csvSize = CSV.getCSVSize(blob.getFile(params.csvKey))

				// Adiciona o envio na fila de processamento
				defaultQueue << [
					url: "/send",
					method: 'PUT', 
					params: [csvKey: params.csvKey, 
							 pdfKey: params.pdfKey, 
							 message: params.message, 
							 subject: params.subject, 
							 fromName: params.fromName, 
							 replyTo: params.replyTo,
							 userEmail: user.email,
							 userName: user.nickname]
				]	
			
				request.flush = "$csvSize certificado(s) estão sendo gerados e preparados para envio.<br><br>" +
						 "Em pouco tempo você receberá um e-mail de <strong>certificadospdf@gmail.com</strong>," + 
						 "<br>assim que o processamento for concluído."
			
				request.status = "SUCCESS"
				forward "/WEB-INF/pages/main.gtpl"      
			} else {
				request.status = "GETMSGDATA"
				request.pdfKey = params.pdfKey
				request.pdfName = params.pdfName
				request.pdfFields = params.pdfFields
				request.replyTo = params.replyTo        
				request.fromName = params.fromName
				request.csvKey = params.csvKey
				request.subject = params.subject
				request.message = params.message
				forward "/WEB-INF/pages/main.gtpl"      
			}
		} // end-if
} // end-switch

// Lê um arquivo pdf do BLOB e obtem a coleção de campos do template
def getTemplateFieldsAsString(pdfFile) {
	def pdfFields = "" 
	pdfFile.withStream { inputStream -> 
		try {
			def pdf = new PDF()
			pdf.open(inputStream) 
			if (pdf.listFormFields().size() > 0) {
				pdfFields = "\$" + pdf.listFormFields().inject() { s,e -> s += ", \$$e" }
				return pdfFields
			}
		} catch (com.itextpdf.text.exceptions.InvalidPdfException e) {
			return null;
		}
	}
}
