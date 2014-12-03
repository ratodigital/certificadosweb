package util

import groovyx.gaelyk.GaelykBindings

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import com.google.appengine.api.files.FileReadChannel

@GaelykBindings
class PDF {
	PdfReader pdf;
	PdfStamper pdfStamper;

	def name = "xFxILE.PDF"

	def open(pdfPath) {
		pdf = new PdfReader(pdfPath)
	}

	def listFormFields() {
		def list = []
		def fields = pdf.getAcroFields().getFields().keySet();
		fields.each { field ->
			list << field
		}
		list
	}

	def preparePdfStamper(pdfStamperOutputStream) {
		try {
			this.pdfStamper = new PdfStamper(pdf, pdfStamperOutputStream)
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	def changeFieldValue(field, newValue) {
	    AcroFields form = pdfStamper.getAcroFields()
	    try {
				form.setField(field, newValue)
			} catch (Exception e) {
				e.printStackTrace()
			}        
		}

		def closePdf() {
	    pdf.close();			
	}
	
	def closePdfStamper() {
		pdfStamper.close()
	}
		
	def closeAll() {
		pdfStamper.close()
    		pdf.close();			
	}

	def getPdfReader() {
		pdfReader()
	}

	static def gerarPDF(pdfFile, data, outputPdfName) {
		byte[] outputPdfBytes
		def pdfStamper
		String messageVars = ""
		pdfFile.withStream { inputStream -> 
			def pdf = new PDF()
			pdf.open(inputStream) 
			pdfStamper = files.createNewBlobFile("application/pdf", outputPdfName)
			pdfStamper.withOutputStream(locked: true, finalize: true) { outputStream ->
				pdf.preparePdfStamper(outputStream)
				pdf.listFormFields().each { fieldName ->
					pdf.changeFieldValue(fieldName, data[fieldName])
					messageVars += "$fieldName = \"${data[fieldName]}\";"
				}
				pdf.closeAll()
			}
		}
		pdfStamper
	}

	static def Map getMessageVars(pdfFile, data) {
		Map messageVars = [:]
		pdfFile.withStream { inputStream -> 
			def pdf = new PDF()
			pdf.open(inputStream)   
			pdf.listFormFields().each { fieldName ->
			messageVars[fieldName] = data[fieldName]
		}
			pdf.closePdf()	
		}
		messageVars
	}
}
