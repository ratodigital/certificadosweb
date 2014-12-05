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
		
	//println new JsonBuilder( res ).toPrettyString()
} catch (Exception ex) {
	ex.printStackTrace()
}
