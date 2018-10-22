package com.coxandkings.utils.files;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DeflateToInflate_HTTPs {

                public static String decompress(String url,String method,String payload,String proxy,String port,String header){
                                String response="";
                                try{
                                                System.setProperty("https.proxyHost", proxy);
                                                System.setProperty("https.proxyPort", port);
                                                System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
                                           
                                                java.net.URL wsURL = new URL (null,url,new sun.net.www.protocol.https.Handler());
//                                         
                                                HttpsURLConnection conn = (HttpsURLConnection) wsURL.openConnection();
                                                conn.setConnectTimeout(60000);
                                                conn.setDoOutput(true);
                                                conn.setRequestMethod(method);
                                                String[] headerProperty = header.split("&");
                                                for (String h : headerProperty) {
                                                                String[] tempHead = h.trim().split(":");
                                                                conn.setRequestProperty(tempHead[0],tempHead[1]);
                                                                
                                                }
                                                DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
                                    wr.writeBytes (payload);
                                    wr.flush ();
                                    wr.close ();
                                    if (conn.getResponseCode() != 200) {
                                                                throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
                                                }    
                                    
                                    
                                    BufferedReader in = new BufferedReader(new InputStreamReader(
                                            conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) 
                     response+=inputLine;
                in.close();
            
                                    
                                    
//                                                BufferedReader br = new BufferedReader(new InputStreamReader(new InflaterInputStream(conn.getInputStream(),new Inflater(true))));
//                                                String temp = "";
//                                                while ((temp = br.readLine()) != null) {
//                                                                response+=temp;
//                                                }
                                                conn.disconnect();
                                }catch (MalformedURLException e) {
                                                e.printStackTrace();
                                }catch (IOException e) {
                                                e.printStackTrace();        
                                }
                                System.out.println(response);
                                return response;
                }
                public static void main(String[] args) {
                	String ss = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\"><soap:Body><hotelSearchRequest versionNumber=\"7.0\"><requestAuditInfo><agentCode>EZE003</agentCode><requestPassword>85R66X82S32F82V71N98</requestPassword><requestID>100010</requestID><requestDateTime>2016-10-04T14:50:47.608</requestDateTime></requestAuditInfo><hotelSearchCriteria 	currencyCode=\"EUR\" paxNationality=\"GB\" languageCode=\"en\"><destination><cityNumbers><cityNumber>20142</cityNumber></cityNumbers></destination><stayPeriod><checkinDate>2016-12-28</checkinDate><numberOfNights>1</numberOfNights></stayPeriod><rooms><room><roomNo>1</roomNo><guests><guest><type>ADT</type></guest><guest><type>CHD</type><age>5</age></guest></guests></room><room><roomNo>2</roomNo><guests><guest><type>ADT</type></guest><guest><type>ADT</type></guest><guest><type>CHD</type><age>6</age></guest></guests></room></rooms><resultDetails><returnDailyPrices>true</returnDailyPrices><returnHotelInfo>1</returnHotelInfo><returnSpecialOfferDetails>1</returnSpecialOfferDetails></resultDetails></hotelSearchCriteria></hotelSearchRequest></soap:Body></soap:Envelope>";
                    decompress("https://test.miki.co.uk/interfaceWL/ws/7.0","POST",ss,"172.21.200.37","3128","Content-Type:application/soap+xml;charset=UTF-8;action=\"hotelSearch\"");
               }
}
