package test.com.coxandkings.utils;

import com.coxandkings.utils.HotelBedsSHA256Converter;

public class SHA256 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			System.out.println(HotelBedsSHA256Converter.getSignature("1", "2"));
			}catch(Exception e){
				e.printStackTrace();
			}
	}

}
