import util.*

def blob = new Blob()

def f = blob.getFile(params.key)
response.setHeader("Content-Type", f.contentType);
response.setHeader("Content-Length", String.valueOf(f.size));
response.setHeader("Content-Disposition", "attachment;filename=\"$f.filename\"");
blobstore.serve(f, response)	 

/*
println "File name: ${f.filename} <br/>"
println "Content type: ${f.contentType}<br/>"
println "Creation date: ${f.creation}<br/>"
def cert = datastore.execute {
	select single from "log" 
	where pdfKey == params.key
}

//PDF.getThumbnail(f,"${f.filename}.png")
println "foi"
f.withStream { inputStream -> 
	try {
		def pdf = new PDF()
		pdf.open(inputStream) 
		new ExtractImages().extractImages(pdf.pdf, "dest");
	} catch (com.itextpdf.text.exceptions.InvalidPdfException e) {
		return null;
	}
}
*/
