package util

import com.google.appengine.api.datastore.*
import java.text.SimpleDateFormat

public class Clock {
	static def sdfDate
	static def sdfTime
	def date

	public Clock() {
		sdfDate = new SimpleDateFormat("dd/MM/yyyy")
		sdfDate.setTimeZone(TimeZone.getTimeZone("GMT-3"))
		sdfTime = new SimpleDateFormat("kk:mm:ss")
		sdfTime.setTimeZone(TimeZone.getTimeZone("GMT-3"))
		date = new Date()
	}

	String getDateTime() {
		getDate() + " " + getTime()
	}

	String getDate() {
		sdfDate.format(date)
	}

	String getTime() {
		sdfTime.format(date)
	}

	String getDay() {
		getDate()[0..1]
	}

	String getMonth() {
		getDate()[3..4]
	}

	String getYear() {
		getDate()[6..9]
	}

	String getAAAMMDD() {
		getYear()+getMonth()+getDay()
	}
	
	def isBirthday(aDate) {
	  if (aDate != null && aDate.trim() != "") {
  	  return getDate()[0..4] == aDate[0..4]
    }
    return false	  
    //aDate
	}

   
}

