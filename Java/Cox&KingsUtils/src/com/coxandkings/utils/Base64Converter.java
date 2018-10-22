package com.coxandkings.utils;
   
import javax.xml.bind.DatatypeConverter;

public class Base64Converter {
    public Base64Converter() {
        super();
    }
    
    
    public static String encodeBase64 (String str) {
            String encoded = DatatypeConverter.printBase64Binary(str.getBytes());
            return encoded;
        }
    public static String decodeBase64 (String str) {
            String decoded = new String(DatatypeConverter.parseBase64Binary(str));
            return decoded;
        }
}
