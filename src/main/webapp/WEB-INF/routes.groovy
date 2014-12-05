
//get "/", forward: "/WEB-INF/pages/index.gtpl"

get "/favicon.ico", redirect: "/images/certificado.png"

get "/", forward: "/WEB-INF/pages/index.gtpl"
get "/pdf", forward: "/WEB-INF/pages/pdf.gtpl"
get "/email", forward: "/WEB-INF/pages/email.gtpl"
get "/login", forward: "/login.groovy"
get "/upload", forward: "/main.groovy"
post "/upload", forward: "/main.groovy"
get "/success", forward: "/WEB-INF/pages/success.gtpl"
get "/failure", forward: "/WEB-INF/pages/failure.gtpl"

put "/send", forward: "/enviarcertificados.groovy"

get "/log", forward: "/log.groovy"
get "/download", forward: "/download.groovy"
get "/certificado/@id", forward: "/certificado.groovy?id=@id"

get "/mailchimp/lists/@apikey", forward: "/mc.groovy?apikey=@apikey"
get "/mailchimp/list/export/@apikey/@listID", forward: "/mclist.groovy?apikey=@apikey&listID=@listID"

get "/blobs", forward: "/blobs.groovy"

