package com.coxandkings.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class DeflateToInflate {

	public static String decompress(String url,String method,String payload,String proxy,String port,String header){
		String response="";
		try{
			System.setProperty("http.proxyHost", proxy);
			System.setProperty("http.proxyPort", port);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
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
			BufferedReader br = new BufferedReader(new InputStreamReader(new InflaterInputStream(conn.getInputStream(),new Inflater(true))));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				response+=temp;
			}
			conn.disconnect();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();	 
		}
		return response;
	}
	//Below Method has been added to support callouts for those  where payload is null	
	public static String decompress_NullPayload(String url,String method,String payload,String proxy,String port,String header){
		String response="";
		try{
			System.setProperty("http.proxyHost", proxy);
			System.setProperty("http.proxyPort", port);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
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
			BufferedReader br = new BufferedReader(new InputStreamReader(new InflaterInputStream(conn.getInputStream(),new Inflater(true))));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				response+=temp;
			}
			conn.disconnect();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();	 
		}
		return response;
	}
	//Below Method has been added to support callouts for those  where payload is in gzip format. Added for Acco-DIDA
	public static String decompress_GZIP(String url,String method,String payload,String proxy,String port,String header){
          String response="";
          final int buffersize=4096;
          
          try{
                          System.setProperty("http.proxyHost", proxy);
                          System.setProperty("http.proxyPort", port);
                          HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
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
                          BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream(),buffersize)));
                          String temp = "";
                          while ((temp = br.readLine()) != null) {
                                          response+=temp;
                          }
                          conn.disconnect();
          }catch (MalformedURLException e) {
                          e.printStackTrace();
          }catch (IOException e) {
                          e.printStackTrace();        
          }
          return response;
}


}
