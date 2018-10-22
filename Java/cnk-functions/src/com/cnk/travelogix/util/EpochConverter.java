package com.cnk.travelogix.util;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EpochConverter {

	
	public static String epochToDate (String epochValue){
		  System.out.println(epochValue);
		  
		  int multiFactor ;
		  
		  if (epochValue.length()==13) 
		  multiFactor  = 1;
		  else
		  multiFactor = 1000;	  
		  
		  long epochVal = 0;
		  try{
		  epochVal = Long.valueOf(epochValue);
		  }
		  catch (Exception e) {
              e.printStackTrace();      
              return epochValue;
		  }
		  		  
		  Date d = new Date(epochVal*multiFactor);
		  DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		  String formattedDate = df.format(d);
		  System.out.println(formattedDate);
		  return formattedDate;
		
	}
	
}
