import util.*
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.blobstore.BlobKey 
import com.google.appengine.api.blobstore.BlobInfo
import com.google.appengine.api.files.FileReadChannel

def FROM_EMAIL = "certificadospdf@gmail.com"

def flush = ""
def flushError = ""

//import java.nio.channels.Channels
if (!params.status) {
  request.status = "GETPDF"
  forward "/WEB-INF/pages/upload.gtpl"
} else {
  request.status = params.status
  switch (params.status) {
    case "GETCSV": 
      //PDF
      def blobs = blobstore.getUploadedBlobs(request)
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
      forward "/WEB-INF/pages/upload.gtpl"
      break
    case "GETMSGDATA":
      //CSV
      def blobs = blobstore.getUploadedBlobs(request)
      def csvFile = blobs["csvFile"]
      request.pdfKey = params.pdfKey
      request.pdfName = params.pdfName
      request.pdfFields = params.pdfFields
      request.csvKey = csvFile.keyString      
      forward "/WEB-INF/pages/upload.gtpl"
      break
    case "PREVIEW":     
      def pdfFile = new BlobKey(params.pdfKey) 
      def csvFile = new BlobKey(params.csvKey)       
      def csvData = getCSVData(csvFile)         
      def outputPdfName = "preview.pdf"
      def pdfStamper = gerarPDF(pdfFile, csvData[0], outputPdfName)
      
      response.setHeader("Content-Type", "application/pdf");
      response.setHeader("Content-Length", String.valueOf(pdfStamper.blobKey.info.size));
      response.setHeader("Content-Disposition", "attachment;filename=\"$outputPdfName\"");
      blobstore.serve(pdfStamper.blobKey, response)	   
      break   
    default:
      if (params.message.indexOf("\$link") == -1) {
            flushError = 'Você obrigatoriamente deve usar o campo \$link.'
      } else {      
		  def csvFile = new BlobKey(params.csvKey)          
		  def csvData = getCSVData(csvFile)
				def pdfFile
		  if (csvData.size() <= 101) {
		    pdfFile = new BlobKey(params.pdfKey) 
		    sucesso = 0
		    erro = 0
		    for (data in csvData) {
		      def outputPdfName = "${data['email']}_${pdfFile.filename}"
		      def pdfStamper = gerarPDF(pdfFile, data, outputPdfName)
		      def outputPdfBytes = getBytes(pdfStamper) 

		      //println "$params.subject Enviando arquivo '$outputPdfName' para email '${data['email']}'<br/>"	    
			  def pdfKey = pdfStamper.blobKey.keyString
			  
		      def vars = getMessageVars(pdfFile, data)
			  vars.put "link", "http://localhost:8080/download?key=$pdfKey"
			  
		      Entity e = new Entity("log")

			  e.name = params.fromName
		      e.email = data['email']
		      e.replyTo = params.replyTo
		      e.subject = params.subject
		      e.message = params.message
		      e.pdfKey = pdfStamper.blobKey.keyString
		      e.pdfName = outputPdfName
		      e.status = "OK"
			  e.dateCreated = new Date()
		      
		      try {
		        def subject = evalScript(vars, params.subject)
		        def message = evalScript(vars, params.message)

		        e.subject = subject
		        e.message = message
		        //println "MAIL SENT: $FROM_EMAIL $params.fromName ${data['email']} ${data['email']} $subject $message $outputPdfName<br><br>"
		        
		        Mail.send(FROM_EMAIL, params.fromName, 
		                  data['email'], data['email'], 
		                  subject, message, params.replyTo,
		                  outputPdfName, outputPdfBytes)
		        sucesso++          
		      } catch (groovy.lang.MissingPropertyException ex) {
		        def m = ex.getMessage()
		        def campo = m.substring(18,m.indexOf(" for"))
		        flushError = 'O campo \$' + campo + ' não existe no Template PDF.'
		        erro++
		      } finally {
		        e.save()
		        if (sucesso > 0) {
	  		      flush = "$sucesso certificados enviados por email com sucesso!"
		        }
		        if (erro > 0) {
		          flush += " $erro certificados apresentaram erro no envio."
		        }
		        //pdfStamper.delete()
		      } 
		    }
		  } else {
		    status = "ERRO"
		    flushError = "Na versão Beta não é possível enviar mais de 100 certificados."
		  }
	  }
	   
      request.flush = flush
      request.flushError = flushError
      if (flushError == "") {
        //pdfFile.delete()
        //csvFile.delete()			
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
        forward "/WEB-INF/pages/upload.gtpl"      
      }
  }
}

String evalScript(Map vars, String script) {
  def binding = new Binding()
  vars.entrySet().each { var ->
    binding.setVariable(var.key, var.value);
  }
  def shell = new GroovyShell(binding) 
    def groovyScript = """
${script.trim()}
"""
  shell.evaluate("return \"\"\"$groovyScript\"\"\"")
}

def gerarPDF(pdfFile, data, outputPdfName) {
  byte[] outputPdfBytes
  def pdfStamper
  String messageVars = ""
  pdfFile.withStream { inputStream -> 
  	def pdf = new PDF()
    pdf.open(inputStream) 
    pdfStamper = files.createNewBlobFile("application/pdf")
    pdfStamper.withOutputStream(locked: true, finalize: true) { outputStream ->
      pdf.preparePdfStamper(outputStream)
      pdf.listFormFields().each { fieldName ->
        pdf.changeFieldValue(fieldName, data[fieldName])
        messageVars += "$fieldName = \"${data[fieldName]}\";"
      }
      pdf.closeAll()
    }
  }
  pdfStamper
}

Map getMessageVars(pdfFile, data) {
  Map messageVars = [:]
  pdfFile.withStream { inputStream -> 
  	def pdf = new PDF()
    pdf.open(inputStream)   
    pdf.listFormFields().each { fieldName ->
      messageVars[fieldName] = data[fieldName]
    }
    pdf.closePdf()
  }
  messageVars
}

def getMessageVarsOLD(pdfFile, data) {
  String messageVars = ""
  pdfFile.withStream { inputStream -> 
  	def pdf = new PDF()
    pdf.open(inputStream)   
    pdf.listFormFields().each { fieldName ->
      messageVars += "$fieldName = \"${data[fieldName]}\";"
    }
    pdf.closePdf()
  }
  messageVars
}

def getBytes(file) {
  blobstore.fetchData(file.blobKey, 0, getSize(file)	- 1) 
}

def getSize(file) {
  file.blobKey.info.size
}

def getCSVData(csvFile) {
  def csvData = []
  csvFile.withStream { csvInputStream ->
    def fieldNames = []
    def f = 1 
    csvInputStream.splitEachLine(",") { fields ->
      if (fields.size() > 1) {
        if (f++==1) {
          fieldNames = fields      
        } else {
          def fieldsMap = [:]
          fieldNames.eachWithIndex { key, index ->
            fieldsMap[key] = fields[index]
          }
          csvData << fieldsMap
        }
      }
    }
  }
  csvData
  //println "CSVDATA=$csvData<br/>"
}


/*
response.setHeader("Content-Type", "application/pdf");
response.setHeader("Content-Length", String.valueOf(inf.size));
response.setHeader("Content-Disposition", "attachment;filename=\"$outputPdfName\"");
blobstore.serve(pdfStamper.blobKey, response)	    
*/

