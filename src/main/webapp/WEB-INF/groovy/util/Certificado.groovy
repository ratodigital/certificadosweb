package util

import model.*

import groovyx.gaelyk.GaelykBindings

@GaelykBindings
public class Certificado {

	static def enviarCertificados(csvKey, pdfKey, message, subject, fromName, replyTo, userEmail, teste = false) {
		def blob = new Blob()

		def flush = "" // MENSAGENS DE SUCESSO ENTRAM AQUI
		def flushError = "" // MENSAGENS DE ERRO ENTRAM AQUI
		def statusError = "" //ERROS GENERICO ENTRAM AQUI

		def csvFile = blob.getFile(csvKey)
		def csvData = CSV.getCSVData(csvFile)

		if (csvData.size() <= Config.ROWS_LIMIT) {

			def sucesso = 0
			def erro = 0
			for (data in csvData) {
				def pdfFile = blob.getFile(pdfKey)		
				def outputPdfName = "${data['email']}_${pdfFile.filename}"
				def pdfStamper = PDF.gerarPDF(pdfFile, data, outputPdfName)

				//println "$subject Enviando arquivo '$outputPdfName' para email '${data['email']}'<br/>"	    

				def vars = PDF.getMessageVars(pdfFile, data)
				vars.put "link", "<a href=\"${Config.DOWNLOAD_LINK}?key=${pdfStamper.blobKey.keyString}\">Clique AQUI para baixar</a>"

				try {
					message = Script.evalScript(vars, message)
					subject = Script.evalScript(vars, subject)

					//println "MAIL SENT: $FROM_EMAIL $fromName ${data['email']} ${data['email']} $subject $message $outputPdfName<br><br>"

					if (teste == false) {
						if (Config.ENV == "PRO") {
							Mail.send(Config.FROM_EMAIL, fromName, 
							data['email'], data['email'], 
							subject, message, replyTo)
						}

						// armazena o CSV no blob
						blob.createFile("text/csv", csvFile.filename, csvData)
println "GRAVEI LOG"
						new Logs().add(
							fromName, 
							data['email'],
							replyTo,
							subject,
							message,
				            vars as String,
							pdfKey,
							csvKey,
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
		} else {
			statusError = "Na versão Beta não é possível enviar mais de 100 certificados."
		}
		
		if (teste == false) {
			Mail.send(
				Config.FROM_EMAIL, 
				"CertificadosPDF", 
				userEmail, userEmail, 
				"Concluímos a emissão dos seus certificados!", 
				flush + "\n" + flushError + "\n---\nCertificadosPDF" , Config.FROM_EMAIL)
		}										
		return [statusError: statusError, flush: flush, flushError: flushError]
	}   
}

