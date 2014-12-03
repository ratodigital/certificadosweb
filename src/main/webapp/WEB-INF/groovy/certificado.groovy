import util.*
import model.*

def cert = new Logs().get(params.id)

/*
println "cert"
println "tk:$cert.templateKey<br>"
println "pk:$cert.pdfKey<br>"
println "ck:$cert.csvKey<br>"
println "vars:$cert.vars<br>"
*/
try {
	blob = new Blob()
	def pdfFile = blob.getFile(cert.templateKey)
	def csvFile = blob.getFile(cert.csvKey)       
	def csvData = CSV.getCSVData(csvFile)         

	def pdfStamper = PDF.gerarPDF(pdfFile, cert.vars, cert.pdfName)

	response.setHeader("Content-Type", "application/pdf");
	response.setHeader("Content-Length", String.valueOf(pdfStamper.blobKey.info.size));
	response.setHeader("Content-Disposition", "attachment;filename=\"$cert.pdfName\"");
	blobstore.serve(pdfStamper.blobKey, response)
} catch (groovy.lang.MissingPropertyException ex) {
	def m = ex.getMessage()
	def campo = m.substring(18,m.indexOf(" for"))
	println 'O campo \$' + campo + ' não existe no Template PDF.'
}
/*
			e.name = name
			e.email = email
			e.replyTo = replyTo
			e.subject = subject
			e.message = message
			e.templateKey = templateKey
			e.csvKey = csvKey
			e.pdfKey = pdfKey
			e.pdfName = pdfName
			e.status = status
			e.dateCreated = new Date()
*/
