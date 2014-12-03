import util.*
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.blobstore.BlobKey 
import com.google.appengine.api.blobstore.BlobInfo
import com.google.appengine.api.files.FileReadChannel

def logList = datastore.execute {
            select all from "log" 
			sort desc by dateCreated
        }

//def blobs = blobstore.getUploadedBlobs(request)

logList.each { l ->
	println "${l.dateCreated} ${l.email} -> <a href='/download?key=${l.templateKey}'>template</a> <a href='/download?key=${l.csvKey}'>csv</a> <a href='/download?key=${l.pdfKey}'>certificado</a> <a href='/certificado/${l.key.id}'>gerar certificado</a><br>"
	
}
