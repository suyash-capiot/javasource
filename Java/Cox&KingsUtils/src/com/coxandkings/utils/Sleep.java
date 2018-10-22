package com.coxandkings.utils;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class Sleep {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		System.out.println("Befor sleep Current Time Stamp : "+ts);
		
		int hrs = 0,min = 0,sec=5;
		
		Timestamp ts3 = Sleep.sleepHMS(hrs,min,sec);
	
		System.out.println("Returned Time Stamp : "+ts3);
	}
	public static Timestamp sleepHMS(int hrs, int min, int sec) {
		// TODO Auto-generated method stub
		try {
			TimeUnit.HOURS.sleep(hrs);
			TimeUnit.MINUTES.sleep(min);
			TimeUnit.SECONDS.sleep(sec);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Timestamp ts1 = new Timestamp(System.currentTimeMillis());
		System.out.println("Return sleep Current Time Stamp : "+ts1);
		return ts1;
	}
}
