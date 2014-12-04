package util

import groovy.json.*

class Mailchimp {
	private String dc
	private String endpointV1
	private String endpointV2	
	private String apikey
	
	Mailchimp(apikey) {
		this.dc = apikey[-3..-1]
		this.apikey =  apikey
		def endpoint = "https://${dc}.api.mailchimp.com"
		this.endpointV1 = "$endpoint"
		this.endpointV2 = "$endpoint/2.0"		
	}
	
	// retorna uma lista de id e nome das listas
	def lists() {
		def json = HTTP.post("$endpointV2/lists/list.json", "{\"apikey\": \"$apikey\"}")
		def lists = [:]
		json.data.each { l ->
			lists.put(l.id, l.name)
		}
		lists
	}
	
	def listExport(listID) {
		HTTP.get("$endpointV1/export/1.0/list","apikey=$apikey&id=$listID")
	}	

}
