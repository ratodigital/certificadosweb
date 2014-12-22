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
} catch (Exception ex) {
	ex.printStackTrace()
}
