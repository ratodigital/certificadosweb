import util.*

def returnMap = Certificado.enviarCertificados(
	params.csvKey, 
	params.pdfKey, 
	params.message, 
	params.subject, 
	params.fromName, 
	params.replyTo,
	params.userEmail)

request.status = returnMap.status
request.statusError = returnMap.statusError			
request.flush = returnMap.flush
request.flushError = returnMap.flushError


