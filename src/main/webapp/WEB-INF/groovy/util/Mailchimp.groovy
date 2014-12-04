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
		def ret
		try {	
			def json = HTTP.post("$endpointV2/lists/list.json", "{\"apikey\": \"$apikey\"}")
			if (json.errors?.size() == 0) { // TUDO OK!
				def listMap = []
				json.data.each { l ->
					listMap << [id:l.id, name:l.name, size:listSize(l.id)]
				}
				ret = [status: "OK", total: json.total, list : listMap]
			} else {
				ret = [status: "ERRO", error: json.error, name: json.name, code: json.code]			
			}
		} catch(Exception ex) {
			ret = [status: "ERRO", error: ex.getMessage(), total: 0, list : ""]
		}
		return ret		
	}
	
	def listSize(listID) {
		def json = HTTP.post("$endpointV2/lists/members.json", 
			"{\"apikey\": \"$apikey\", \"id\": \"$listID\", \"opts\": {\"start\": 0, \"limit\": 0}}")
		return json.total
	}	
		
	def listExport(listID) {
		try {
			def ret = HTTP.get("$endpointV1/export/1.0/list","apikey=$apikey&id=$listID")
			//ret = ret.replaceAll("\"","")
			//ret.eachLine { l ->
				//l = l.replaceAll("[","") //.replaceAll("]","").replaceAll("\"","")				
				//println "linex: ${l[0]}"+l //"${l[0]} ${l[1]} ${l[2]}"
			//}
		} catch(Exception ex) {
			return ex.getMessage()
		}
	}	

}
