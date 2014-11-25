package util

import groovyx.gaelyk.GaelykBindings

import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.blobstore.BlobKey 
import com.google.appengine.api.blobstore.BlobInfo
import com.google.appengine.api.files.FileReadChannel

 
// annotate your class with the transformation
@GaelykBindings
class Blob {
		
	def getUploadedBlobs(request) {
		blobstore.getUploadedBlobs(request)
	}

	def getFile(key) {
		new BlobKey(key)       
	}

	def serveContent(file, response) {
	    blobstore.serve(file.blobKey, response)	   
	}	

	def getBytes(file) {
		blobstore.fetchData(file.blobKey, 0, getSize(file)	- 1) 
	}

	def getSize(file) {
  		file.blobKey.info.size
	}
}
