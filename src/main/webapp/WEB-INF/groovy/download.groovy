import com.google.appengine.api.blobstore.BlobKey

def blob = new BlobKey(params.key)
//println blob == null
//println "size:" + String.valueOf(blob.info.size)
/*
println "File name: ${blob.filename} <br/>"
println "Content type: ${blob.contentType}<br/>"
println "Creation date: ${blob.creation}<br/>"
println "Size: ${blob.size}"
*/
def cert = datastore.execute {
	select single from "log" 
	where pdfKey == params.key
}

//println cert.email
def filename = "${cert.email}.pdf" 
response.setHeader("Content-Type", blob.contentType);
response.setHeader("Content-Length", String.valueOf(blob.size));
response.setHeader("Content-Disposition", "attachment;filename=\"$filename\"");
blobstore.serve(blob, response)	 

