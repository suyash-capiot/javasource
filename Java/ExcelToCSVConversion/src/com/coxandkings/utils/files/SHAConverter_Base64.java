package com.coxandkings.utils.files;

import java.security.MessageDigest;


import javax.xml.bind.DatatypeConverter;

public class SHAConverter_Base64 {

       public static String encryptSHAString(String base, String hash_algo) throws Exception {
           try{
              
              MessageDigest digest = MessageDigest.getInstance(hash_algo);
               byte[] hash = digest.digest(base.getBytes("UTF-8"));
               StringBuffer hexString = new StringBuffer();
            
               for (int i = 0; i < hash.length; i++) {
                   String hex = Integer.toHexString(0xff & hash[i]);
                   if(hex.length() == 1) hexString.append('0');
                   hexString.append(hex);
               }
              
               
       
         
               
              byte[] hex_byte= DatatypeConverter.parseHexBinary(hexString.toString());
       String base64_encoded= DatatypeConverter.printBase64Binary(hex_byte);
       return base64_encoded;
               
         
           }
         
               
            catch(Exception ex){
              throw new RuntimeException(ex);
           }
      }
       
       public static void main(String args[]){
              String test="Bo97XgLt85Nwi3TF12378920161019MASTERCARDUSD2101443000010000EZEEGO";
              String test_hash;
              try {
                     test_hash = SHAConverter_Base64.encryptSHAString(test,"SHA-1");
                     System.out.println(test_hash);
              } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
              }
              
              
       }
}
