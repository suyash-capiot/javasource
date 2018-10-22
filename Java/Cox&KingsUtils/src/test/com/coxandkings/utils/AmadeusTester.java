package test.com.coxandkings.utils;

import com.coxandkings.utils.AmadeusTokenGenerator;

public class AmadeusTester {

	public static void main(String[] args) {
		try {
			AmadeusTokenGenerator.tokenGenerator("48s6ISDP");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
