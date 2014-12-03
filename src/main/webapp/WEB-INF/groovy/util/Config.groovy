package util

class Config {
	//ENVIROMENT

	//static def ENV = "DES"
	static def ENV = "PRO"

	static def FROM_EMAIL = "certificadospdf@gmail.com"
	static def ROWS_LIMIT = 101

	static def DOWNLOAD_LINK = (ENV == "PRO") ? "http://web.certificadospdf.appspot.com/download" : "http://localhost:8080/download"
}
