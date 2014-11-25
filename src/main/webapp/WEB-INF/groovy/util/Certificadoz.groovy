package util

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


public class Certificadoz {
	PdfReader pdf;
	PdfStamper pdfStamper;
	
	public Certificadoz() {
	}

	public Certificadoz openPdf(String pdfPath) {
        try {
			this.pdf = new PdfReader(pdfPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return this;
	}

	public List<String> listFormFields() {
		// TODO Auto-generated method stub
		List <String> list = new ArrayList<String>();
		AcroFields pdfForm = pdf.getAcroFields();
		Set<String> fields = pdfForm.getFields().keySet();
		for (String field: fields) {
			System.out.println("key"+ field);
			list.add(field);
		}
		return list;
	}


	public boolean changeFieldValue(String field, String value) {
		// TODO Auto-generated method stub
        AcroFields form = pdfStamper.getAcroFields();
        try {
			form.setField(field, value);
			pdfStamper.close();
	        pdf.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		return true;
	}

	public Certificadoz createPdfStamper(String pdfStamperPath) {
	    try {
	    	File f = new File(pdfStamperPath);
	    	f.setReadable(true);
	        f.setWritable(true);
	    	if(!f.exists()) {
	    		//f.mkdirs();
	    	    f.createNewFile();
	    	} 
			this.pdfStamper = new PdfStamper(pdf, new FileOutputStream(f));
			return this;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

