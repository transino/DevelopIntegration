package net.zhangqiu.utils;

import java.util.Random;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AESKeyUtils {

    private static Logger logger = LoggerFactory.getLogger(AESKeyUtils.class);

	private static String getKeyFromFile(String key){
		if(StrUtils.isEmpty(key)){
			byte[] _key1 = new byte[16];
	        Random ran1 = new Random(23);
	        for (int i = 0; i < 16; i++) {
	            _key1[i]=(byte)ran1.nextInt(16);
	        }
			key = new String(_key1);
		}

		return key;
	}
	
	public static String DecryString(String sSrc, String key) {  
        try {  
        	key = getKeyFromFile(key);
            byte[] raw = key.getBytes();  
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); 
            byte[] _key1 = new byte[16];
	        Random ran1 = new Random(16);
	        for (int i = 0; i < 16; i++) {
	            _key1[i]=(byte)ran1.nextInt(16);
	        }
            IvParameterSpec iv = new IvParameterSpec(_key1);  
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);  
            byte[] encrypted1 = Base64.decodeBase64(sSrc);
            try {  
                byte[] original = cipher.doFinal(encrypted1);  
                String originalString = new String(original);  
                return originalString;  
            } catch (Exception ex) {
                logger.error("",ex);
                return null;  
            }  
        } 
        catch (Exception ex) {
            logger.error("",ex);
            return null;  
        }  
    }  
}
