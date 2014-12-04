import util.*

def mc = new Mailchimp("6e5dbfb2074d3435fffa69d860e6a9b8-us7")

lists = mc.lists()
println lists.size()
lists.each {k, v ->
	println "$k -> $v"
}
println "<hr>"
println mc.listExport("9632ff5257")

/*
import groovy.json.*

def url = "https://us7.api.mailchimp.com/2.0/lists/list.json"
def params = '{"apikey": "6e5dbfb2074d3435fffa69d860e6a9b8-us7"}'
def result = HTTP.post(url, params)
//println result
//println "<hr>"

def json = new JsonSlurper().parseText(result)
//println json.toString()
//println "<hr>"

//def prettyJson = JsonOutput.prettyPrint(json.toString())
def prettyJson = JsonOutput.prettyPrint(result)
println prettyJson
println "<hr>"
print json.total
json.data.each { l ->
	println "$l.id -> $l.name<br>"
}
*/

/*
import groovy.json.*

def json = new JsonBuilder()
json.message {
header {
	from('mrhaki')  // parenthesis are optional
	to 'Groovy Users', 'Java Users'
	body "Check out Groovy's gr8 JSON support."
}

URL.metaClass {

    post { String params ->

        def conn = delegate.openConnection()
        conn.setRequestMethod("POST")
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=iso-8859-1")
        conn.doOutput = true
  	
        Writer wr = new OutputStreamWriter(conn.outputStream)
        wr.write(params)
        wr.flush()
        wr.close()

        conn.connect()
        response = conn.content.getText("iso-8859-1") 
        conn.disconnect()
        return response
    }
}

def url = new URL ("https://us7.api.mailchimp.com/2.0/lists/list.json")
def conteudo = url.post('{"apikey": "6e5dbfb2074d3435fffa69d860e6a9b8-us7"}')
println conteudo
def json = new JsonSlurper().parseText(conteudo)
//prinln JsonOutput.preetyPrint(json.toString())
*/

/*
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonSlurper
*/
//def bodyMap = new JsonSlurper().parseText('{"apikey": "6e5dbfb2074d3435fffa69d860e6a9b8-us7"}')
//println bodyMap

/*
def myClient = new groovyx.net.http.HTTPBuilder('https://us7.api.mailchimp.com/2.0/lists/list.json')
myClient.setHeaders(Accept: 'application/json')

results = myClient.request(POST, JSON) { req ->
    requestContentType = JSON
    body = "{\"apikey\":\"6e5dbfb2074d3435fffa69d860e6a9b8-us7\"}"
    response.success = { resp, reader ->
        println "SUCCESS! ${resp.statusLine}"
    }

    response.failure = { resp ->
        println "FAILURE! ${resp.properties}"
    }
}
*/



//url = "https://us7.api.mailchimp.com/2.0/lists/list.json"

//result = url.toURL().text

//println result
/*
http.request(POST) {
    uri.path = "https://us7.api.mailchimp.com/2.0/lists/list.json"
    
    requestContentType = ContentType.JSON
    body = "{"apikey": "6e5dbfb2074d3435fffa69d860e6a9b8-us7"}" //[name: 'bob', title: 'construction worker']

    response.success = { resp ->
        println "Success! ${resp.status}"
    }

    response.failure = { resp ->
        println "Request failed with status ${resp.status}"
    }
}
*/

/*
def http = new HTTPBuilder("https://us7.api.mailchimp.com/2.0")

http.request( POST, JSON ) {
  uri.path = '/lists/list.json'
  uri.query = [ apikey:'6e5dbfb2074d3435fffa69d860e6a9b8-us7' ]

  response.success = { resp, json ->
    println resp.statusLine

    json.responseData.results.each {
      println "  ${it.titleNoFormatting} : ${it.visibleUrl}"
    }

  }

  response.failure = { resp ->
    println "Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}"
  }
}
*/
