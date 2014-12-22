package model

import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import groovyx.gaelyk.GaelykBindings
import util.*

@GaelykBindings
public class UserPreferences {

    def get(id) {
        def longId = Long.parseLong(id)
        Key key = KeyFactory.createKey("user_preferences", longId)
        datastore.get(key)
    }

    def getByEmail(email) {
        datastore.execute {
            select single
            from "user_preferences"
            where userEmail == email
        }
    }
    
    def listAll() {
        datastore.execute {
            select all
            from "user_preferences"
            sort desc by dateCreated
        }
    }

    def add(mailchimpApiKey, mailchimpListID, userEmail) {
	    datastore.withTransaction {
			Entity  e = getByEmail(userEmail)
	    	if (e == null) {
				e = new Entity("user_preferences")
        	}
			e.mailchimpApiKey = mailchimpApiKey
			e.mailchimpListID = mailchimpListID
			e.userEmail = userEmail
			e.dateCreated = (new Clock()).getDateTime()
			e.save()
		}        	
    }
}
