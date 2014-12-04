import util.*

import groovy.json.*

if (user == null) {
	redirect "/login"
	return
} 

def mc = new Mailchimp(params.apikey)
try {
	def res =  mc.listExport(params.listID)
	println res
	
	res.eachLine { l ->
		//l = l.replaceAll("[","") //.replaceAll("]","").replaceAll("\"","")				
		//println "linexx: $l" //{l[0]}"+l //"${l[0]} ${l[1]} ${l[2]}"

		def var = Eval.me(l)
		
		println var[0]
		println var[1]
		println var[2]				
	}
		
	//println new JsonBuilder( res ).toPrettyString()
} catch (Exception ex) {
	ex.printStackTrace()
}
