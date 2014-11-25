import util.*
import model.*

/**
 * WORKFLOW:
 * PASSO 1: GETPDF      - Obter template PDF
 * PASSO 2: GETCSV      - Obter arquivo CSV
 * PASSO 3: GETMSGDATA  - Obter dados para envio
 * PASSO 4: SEND_EMAILS - Enviar emails
 *
 * ALTERNATIVAS:
 * PREVIEW: VISUALIZAR CERTIFICADO GERADO
 */

def blob = new Blob()

def flush = ""
def flushError = ""

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
		pdfFile.withStream { inputStream -> 
			try {
				def pdf = new PDF()
				pdf.open(inputStream) 
				if (pdf.listFormFields().size() > 0) {
					request.pdfFields = "\$" + pdf.listFormFields().inject() { s,e -> s += ", \$$e" }
				}
			} catch (com.itextpdf.text.exceptions.InvalidPdfException e) {
				request.status = "GETPDF"
				request.flushError = 'Selecione  um arquivo PDF válido.'
			}
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
		blob.serveContent(file, response)
		break   
	default: // PASSO 3. ENVIAR CERTIFICADOS
		if (params.message.indexOf("\$link") == -1) {
			flushError = 'Você obrigatoriamente deve usar o campo \$link.'
		} else {      
			def csvFile = blob.getFile(params.csvKey)          
			def csvData = CSV.getCSVData(csvFile)
			def pdfFile

			if (csvData.size() <= Config.ROWS_LIMIT) {
				pdfFile = blob.getFile(params.pdfKey) 
				sucesso = 0
				erro = 0
				for (data in csvData) {
					def outputPdfName = "${data['email']}_${pdfFile.filename}"
					def pdfStamper = PDF.gerarPDF(pdfFile, data, outputPdfName)
					def outputPdfBytes = blob.getBytes(pdfStamper) 

					//println "$params.subject Enviando arquivo '$outputPdfName' para email '${data['email']}'<br/>"	    
					def pdfKey = pdfStamper.blobKey.keyString

					def vars = PDF.getMessageVars(pdfFile, data)
					vars.put "link", DOWNLOAD_LINK "${Config.DOWNLOAD_LINK}?key=$pdfKey"

					def message = params.message
					def subject = params.subject		  

					try {
						message = Script.evalScript(vars, params.message)
						subject = Script.evalScript(vars, params.subject)

						//println "MAIL SENT: $FROM_EMAIL $params.fromName ${data['email']} ${data['email']} $subject $message $outputPdfName<br><br>"

						/*
						Mail.send(Config.FROM_EMAIL, params.fromName, 
						data['email'], data['email'], 
						subject, message, params.replyTo,
						outputPdfName, outputPdfBytes)
						*/

						new Logs().add(
							params.fromName, 
							data['email'],
							params.replyTo,
							subject,
							message,
							pdfStamper.blobKey.keyString,
							outputPdfName,
							"OK")

						sucesso++          
					} catch (groovy.lang.MissingPropertyException ex) {
						def m = ex.getMessage()
						def campo = m.substring(18,m.indexOf(" for"))
						flushError = 'O campo \$' + campo + ' não existe no Template PDF.'
						erro++
					} finally {
						if (sucesso > 0) {
							flush = "$sucesso certificados enviados por email com sucesso!"
						}
						if (erro > 0) {
							flushError = "$erro certificados apresentaram erro no envio."
						}
						//pdfStamper.delete()
					} // end-try
				} // end-for
			} else {
				status = "ERRO"
				flushError = "Na versão Beta não é possível enviar mais de 100 certificados."
			}

			request.flush = flush
			request.flushError = flushError
			if (flushError == "") {
				forward "/WEB-INF/pages/success.gtpl"      
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

