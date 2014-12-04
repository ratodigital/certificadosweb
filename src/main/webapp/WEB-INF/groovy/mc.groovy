import util.*

import groovy.json.*

if (user == null) {
	redirect "/login"
	return
} 

def mc = new Mailchimp(params.apikey)
try {
	def res =  mc.lists()
	println new JsonBuilder( res ).toPrettyString()
} catch (Exception ex) {
	ex.printStackTrace()
}
