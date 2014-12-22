import util.*

import com.google.appengine.api.blobstore.BlobKey 

def session = request.getSession()
session['varname'] = new Date()
println session['varname']
/*
def blob = new Blob()
def a = blob.getContentByKey(params.key) as List
println a
println "<hr>"
println a.size()
println a[0]
for (item in a) {
	println item['email'] + "<br>"
}


def ar = Eval.me(a)
println "<hr>"

ar.each {
	println it + "<br>"
}*/

/*
def f = blob.getFile(params.key)
println "Key: ${f.blobKey} <br/>"
println "File name: ${f.filename} <br/>"
println "Content type: ${f.contentType}<br/>"
println "Creation date: ${f.creation}<br/>"
println "Size: ${f.size}<br/>"
def bytes = blobstore.fetchData(f, 0, f.size - 1) 
*/



/*
def dataArray = blob.getBytesByKey(params.dataKey)
prinln dataArray


blobstore.each { b ->
  println "$b.blobKey.keyString $b.filename $b.creation $b.contentType $b.size<br/>"
  def blob = b.blobKey 
  blob.delete()
}
*/

