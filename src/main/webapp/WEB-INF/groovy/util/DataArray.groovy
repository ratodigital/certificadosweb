package util

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataArray implements Serializable {
	def fileKey
	def fileName
	def fieldNames
	def fileContent = []

	def DataArray(fileName, fileKey) {
		def content
		fileKey.withStream { inputStream ->
			def fieldNames = []
			def f = 1 
			inputStream.splitEachLine(",") { fields ->
				if (fields.size() > 1) {
					if (f++==1) {
						fieldNames = fields      
					} else {
						def fieldsMap = [:]
						fieldNames.eachWithIndex { key, index ->
							fieldsMap[key] = "\"${fields[index]}\""
						}
						content << fieldsMap
					}
			  	}
			}
		}
		this.fileName = fileName
		this.fileKey = fileKey
		this.fileContent = fileContent
		this.fieldNames = fieldNames
		println "FC:" + fileContent
	}

	def getContent() {
		this.fileContent
	}
	
	def getSize() {
		this.fileContent.size()
	}
		
	def getHeader() {
		this.fieldNames
	}
}
