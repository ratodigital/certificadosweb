package util

import groovy.json.*

public class HTTP {

	static def post(url, params) {
		def conn = new URL(url).openConnection()
		conn.setRequestMethod("POST")
		conn.setRequestProperty("Content-type", "application/json;charset=iso-8859-1")
		conn.doOutput = true

		Writer wr = new OutputStreamWriter(conn.outputStream)
		wr.write(params)
		wr.flush()
		wr.close()

		conn.connect()
		def result = conn.content.getText("iso-8859-1") 
		conn.disconnect()
		new JsonSlurper().parseText(result)
	}	
	
	static def get(url, params) {
		new URL(url + "?" + params).getText("iso-8859-1")
	}		
}
