package test.com.coxandkings.utils;

import com.coxandkings.utils.RedBusOAuthHeaderGenerator;

public class TestRedBusOAuthHeaderGenerator {

	public static void main(String[] args) {
		try {
			String oAuthHeader = RedBusOAuthHeaderGenerator.generateHeader("GET", "http://api.seatseller.travel/destinations", "source=1230", "CUxXnxQK2LRnBkhxFS2hJec526sh4S", "A32A3KJ52C2APz469UGHD9pbhp5mlx");
			System.out.println("OAuth Header = <" + oAuthHeader + ">");
		}
		catch (Exception x) {
			x.printStackTrace();
		}
		
		System.exit(0);
	}

}
