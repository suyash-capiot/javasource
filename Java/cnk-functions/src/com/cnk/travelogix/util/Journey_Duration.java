package com.cnk.travelogix.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Journey_Duration {

	public static String calculateJourneyDuration (String departureTime, String departureOffset , String arrivalTime , String arrivalOffset) {
		
//		departureTime="2016-09-30T16:40:00";
//		arrivalTime="2016-09-30T21:10:00";
//		departureOffset = 0;
//		arrivalOffset = 0;
		
		String depDate = (departureTime.replace('T', ' '));
		String arrDate = (arrivalTime.replace('T', ' '));
		long diffInMinutes =0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
		Date deptDate;
		try {
			deptDate = df.parse(depDate);
		
		Calendar departCal = Calendar.getInstance();
		departCal.setTime(deptDate);
		departCal.add(Calendar.MINUTE, Integer.parseInt(departureOffset));
		depDate = df.format(departCal.getTime());
		
		
		Date arrvDate = df.parse(arrDate);
		Calendar arrvCal = Calendar.getInstance();
		arrvCal.setTime(arrvDate);
		arrvCal.add(Calendar.MINUTE, Integer.parseInt(arrivalOffset));
		arrDate = df.format(arrvCal.getTime());
		
		DateTimeFormatter datediffFT = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime arrDateTime = LocalDateTime.parse(depDate,
				datediffFT);
		LocalDateTime deptDateTime = LocalDateTime.parse(arrDate, datediffFT);

		diffInMinutes = java.time.Duration.between(arrDateTime,
				deptDateTime).toMinutes();
		
		System.out.println(diffInMinutes);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Long.toString(diffInMinutes);
	}
	
}
