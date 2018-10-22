package com.coxandkings.utils;

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
                                                System.out
														.println(payload);
                                                java.net.URL wsURL = new URL (null,url,new sun.net.www.protocol.https.Handler());
//                                         
                                                HttpsURLConnection conn = (HttpsURLConnection) wsURL.openConnection();
                                                conn.setConnectTimeout(60000);
                                                conn.setDoOutput(true);
                                                conn.setRequestMethod(method);
                                                if(header!=null && header.length()>0){
                                                String[] headerProperty = header.split("&");
                                                for (String h : headerProperty) {
                                                                String[] tempHead = h.trim().split(":");
                                                                conn.setRequestProperty(tempHead[0],tempHead[1]);
                                                                
                                                }}
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
               /* public static void main(String args[]){
                	String url="https://api-demo.distribusion.com:443/reseller/v2/connections/find?affiliate_partner_number=222222&api_key=_oM3u_hq3UMdzkQQbOlU-g&arrival_station_ids=1967&date=2017-07-05&customer_currency=EUR&departure_station_ids=115";
                	String method="GET";
                	String payload="";
                	String proxy="172.21.200.37";
                	String port="3128";
                	String headers="";
                	
                	String output=decompress_NullPayload(url, method, payload, proxy, port, headers);
                	System.out.println(output);
                	
                }*/
                
//Below Method has been added to support callouts for those  where payload is null 
                public static String decompress_NullPayload(String url,String method,String payload,String proxy,String port,String header){
                    String response="";
                    try{
                                    System.setProperty("https.proxyHost", proxy);
                                    System.setProperty("https.proxyPort", port);
                                    System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
                                    System.out
											.println(payload);
                                    java.net.URL wsURL = new URL (null,url,new sun.net.www.protocol.https.Handler());
//                             
                                    HttpsURLConnection conn = (HttpsURLConnection) wsURL.openConnection();
                                    conn.setConnectTimeout(60000);
                                    conn.setDoOutput(true);
                                    conn.setRequestMethod(method);
                                    if(header!=null && header.length()>0){
                                    String[] headerProperty = header.split("&");
                                    for (String h : headerProperty) {
                                                    String[] tempHead = h.trim().split(":");
                                                    conn.setRequestProperty(tempHead[0],tempHead[1]);
                                                    
                                    }}
                                    if(payload!=null && payload.length()>0){
                                    DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
                        wr.writeBytes (payload);
                        wr.flush ();
                        wr.close ();}
                        if (conn.getResponseCode() != 200) {
                                                    throw new RuntimeException("Failed : HTTP error code : "+ conn.getResponseCode());
                                    }    
                        
                        
                        BufferedReader in = new BufferedReader(new InputStreamReader(
                                conn.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) 
    	response+=inputLine;
    in.close();

                        
                        
//                                    BufferedReader br = new BufferedReader(new InputStreamReader(new InflaterInputStream(conn.getInputStream(),new Inflater(true))));
//                                    String temp = "";
//                                    while ((temp = br.readLine()) != null) {
//                                                    response+=temp;
//                                    }
                                    conn.disconnect();
                    }catch (MalformedURLException e) {
                                    e.printStackTrace();
                    }catch (IOException e) {
                                    e.printStackTrace();        
                    }
                    System.out.println(response);
                    return response;
    }
}
