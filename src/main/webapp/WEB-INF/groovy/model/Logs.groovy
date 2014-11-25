package model

import com.google.appengine.api.datastore.*
import com.google.appengine.api.datastore.KeyFactory
import com.google.appengine.api.datastore.Key
import groovyx.gaelyk.GaelykBindings
import util.*

@GaelykBindings
public class Logs {

    def get(id) {
        def longId = Long.parseLong(id)
        Key key = KeyFactory.createKey("log", longId)
        def goal = datastore.get(key)
    }

    def listAll() {
        datastore.execute {
            select all
            from "log"
            sort desc by dateCreated
        }
    }

    def add(name,email,replyTo,subject,message,pdfKey,pdfName,status) {
        datastore.withTransaction {
			Entity e = new Entity("log")
			e.name = name
			e.email = email
			e.replyTo = replyTo
			e.subject = subject
			e.message = message
			e.pdfKey = pdfKey
			e.pdfName = pdfName
			e.status = status
			e.dateCreated = new Date()
			e.save()
        }
    }
}
