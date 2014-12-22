package util

import groovyx.gaelyk.GaelykBindings

import com.google.appengine.api.blobstore.BlobKey 
import com.google.appengine.api.blobstore.BlobInfo

import javax.servlet.http.HttpServletResponse 

@GaelykBindings
class Blob {

	def createFile(fileType, fileName, fileContent) {
		def file = files.createNewBlobFile(fileType, fileName)
		file.withWriter { writer ->
			writer << fileContent
		}
	}			

	def getUploadedBlobs(request) {
		blobstore.getUploadedBlobs(request)
	}

	def getFile(key) {
		new BlobKey(key)       
	}

	def getBytes(file) {
		blobstore.fetchData(file.blobKey, 0, getSize(file)	- 1) 
	}

	def getContentByKey(fileKey) {
		def f = getFile(fileKey)
		def bytes = blobstore.fetchData(f, 0, f.size - 1) 
		def data = new String(bytes)
		println data
		//return Eval.me(data)
	}
	
	def getSize(file) {
  		file.blobKey.info.size
	}
}
