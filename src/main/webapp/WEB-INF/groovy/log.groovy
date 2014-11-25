import util.*
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.blobstore.BlobKey 
import com.google.appengine.api.blobstore.BlobInfo
import com.google.appengine.api.files.FileReadChannel

def logList = datastore.execute {
            select all from "log"
        }

//def blobs = blobstore.getUploadedBlobs(request)

logList.each { l ->
	println "${l.email} -> <a href='/download?key=${l.pdfKey}'>download certificado</a><br>"
	
}
