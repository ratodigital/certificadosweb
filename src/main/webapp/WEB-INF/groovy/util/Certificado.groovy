package util

import model.*

import groovyx.gaelyk.GaelykBindings

@GaelykBindings
public class Certificado {

	static def enviarCertificados(dataKey, pdfKey, message, subject, fromName, replyTo, userEmail, userName, teste = false) {
		def blob = new Blob()

		def flush = "" // MENSAGENS DE SUCESSO ENTRAM AQUI
		def flushError = "" // MENSAGENS DE ERRO ENTRAM AQUI
		def statusError = "" //ERROS GENERICO ENTRAM AQUI

		def dataArray = blob.getContentByKey(dataKey)

		def sucesso = 0
		def erro = 0
		for (data in dataArray) {
			def pdfFile = blob.getFile(pdfKey)		
			def outputPdfName = "${data['email']}_${pdfFile.filename}"
			def pdfStamper = PDF.gerarPDF(pdfFile, data, outputPdfName)

			def vars = PDF.getMessageVars(pdfFile, data)
			vars.put "link", "<a href=\"${Config.DOWNLOAD_LINK}?key=${pdfStamper.blobKey.keyString}\">Clique AQUI para baixar</a>"

			try {
				message = Script.evalScript(vars, message)
				subject = Script.evalScript(vars, subject)

				if (teste == false) {
					if (Config.ENV == "PRO") {
						Mail.send(Config.FROM_EMAIL, fromName, 
						data['email'], data['email'], 
						subject, message, replyTo)
					}

					new Logs().add(
						fromName, 
						data['email'],
						replyTo,
						subject,
						message,
			            vars as String,
						pdfKey,
						dataKey,
						pdfStamper.blobKey.keyString,
						outputPdfName,
						userEmail,							
						"OK")

					sucesso++          
				} else { // Se for um teste, executa apenas uma vez para testar se ocorre MissingPropertyException 
					return [statusError: statusError, flush: flush, flushError: flushError]
				}

			} catch (groovy.lang.MissingPropertyException ex) {
				def m = ex.getMessage()
				def campo = m.substring(18,m.indexOf(" for"))
				statusError = 'O campo \$' + campo + ' não existe no Template PDF.'
				erro++
				//ex.printStackTrace();
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
		if (teste == false) {
			if (Config.ENV == "PRO") {
				def mensagemFinal = "$flush\n$flushError\n---\nCertificadosPDF" 
				Mail.send(
					Config.FROM_EMAIL, //fromEmail
					"CertificadosPDF", //fromName
					userEmail, //to Email
					userName, //to Name
					"Concluímos a emissão dos seus certificados!", 
					mensagemFinal, 
					Config.FROM_EMAIL)				
			}
		}										
		
		return [statusError: statusError, flush: flush, flushError: flushError]
	}   
}

