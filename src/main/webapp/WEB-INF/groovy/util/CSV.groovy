package util

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class CSV {
	static def getData(csvFile) {
		def csvData = []
		csvFile.withStream { csvInputStream ->
			def fieldNames = []
			def f = 1 
			csvInputStream.splitEachLine(",") { fields ->
				if (fields.size() > 1) {
					if (f++==1) {
						fieldNames = fields      
					} else {
						def fieldsMap = [:]
						fieldNames.eachWithIndex { key, index ->
							fieldsMap[key] = "\"${fields[index]}\""
						}
						csvData << fieldsMap
					}
			  	}
			}
		}
		csvData
	}

	static def getSize(csvFile) {
		getData(csvFile).size()
	}
		
	static def getHeader(csvFile) {
		def header
		csvFile.withReader { header = it.readLine() }  
		header.split(",")
	}
}
