
package ug.co.yo.payments;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @author Munaawa Philip (swiftugandan@gmail.com)
 * This class is a sample application of the Yo! Payments API (https://payments.yo.co.ug)
 */
public class YoSecurityUtils {
    
    private static SecretKey key;
    //private static String randPart;
    
    YoSecurityUtils() {
    }

    private static SecretKey makeKey(String pin) {
        //get the random part from the database and add it to the pin
        try {
            byte[] secretBytes = pin.getBytes("UTF8");
            DESKeySpec keySpec = new DESKeySpec(secretBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            key = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            System.out.println("Invalid PIN ...");
            return null;
            //e.printStackTrace();
        }
        return key;
    }

    private static String encryptString(String rawInput, SecretKey key) {
        if (key != null) {
            try {
                byte[] cleartext = rawInput.getBytes("UTF8");

                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] clearBytes = cipher.doFinal(cleartext);
                byte[] encryptedPwd = Base64.encodeBase64(clearBytes);
                return new String(encryptedPwd, "UTF8");
            } catch (Exception e) {
                System.out.println("Invalid key ...");
                // e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private static String decryptString(String encryptedInput, SecretKey key) {
        String pw = null;
        byte[] userpw = null;
        if (key != null) {
            try {
                userpw = encryptedInput.getBytes("UTF8");
                byte[] encrypedPwdBytes = Base64.decodeBase64(userpw);
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] plainTextPwdBytes = cipher.doFinal(encrypedPwdBytes);
                pw = new String(plainTextPwdBytes, "UTF8");
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return pw;
    }
    
    private static String generateString(Random rng, String characters, int length)
    {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
    
    /**
     * @param args
     */
    public final static void main(String[] args) throws Exception {
        HashMap<String, String> encryptedCredentials = new HashMap<String, String>();
        /*encryptedCredentials = encryptCredentials("username", "password", "1234");

        System.out.println(encryptedCredentials.get("encryptedUsername"));
        System.out.println(encryptedCredentials.get("encryptedPassword"));
        System.out.println(encryptedCredentials.get("randomPinPart"));
        
        HashMap<String, String> decryptedCredentials = new HashMap<String, String>();
        decryptedCredentials = decryptCredentials("V3Xi9RUYxAKiV7zgFsQTgw==", "U1m39c9jEdmiV7zgFsQTgw==", "0289","1234");
        System.out.println(decryptedCredentials.get("username"));
        System.out.println(decryptedCredentials.get("password"));*/
        
        encryptedCredentials = changePin("1234", "4567","V3Xi9RUYxAKiV7zgFsQTgw==", "U1m39c9jEdmiV7zgFsQTgw==", "0289");
        if (encryptedCredentials != null) {
            System.out.println(encryptedCredentials.get("encryptedUsername"));
            System.out.println(encryptedCredentials.get("encryptedPassword"));
            System.out.println(encryptedCredentials.get("randomPinPart"));
        }else{
            System.out.println("PIN change was unsuccessful");
        }

    }

    
    /**
     * This function encrypts the username and password of the user using the pin, for storage on the device.
     * @param username
     * @param password
     * @param pin
     * @return encryptedCredentials hashmap
     */
    public static HashMap<String, String> encryptCredentials(String username, String password, String pin) {
        HashMap<String, String> encryptedCredentials = new HashMap<String, String>();
        
        if (isValidFormat(pin)) {
            String randomPinPart = YoSecurityUtils.generateString(new Random(), "0123456789", 4);
            SecretKey yoNewSecretKey = YoSecurityUtils.makeKey(pin + randomPinPart);
            String encryptedUsername = YoSecurityUtils.encryptString(username, yoNewSecretKey);
            String encryptedPassword = YoSecurityUtils.encryptString(password, yoNewSecretKey);
            encryptedCredentials.put("encryptedUsername", encryptedUsername);
            encryptedCredentials.put("encryptedPassword", encryptedPassword);
            encryptedCredentials.put("randomPinPart", randomPinPart);
            return encryptedCredentials;
        }else{
            return null;
        }
    }
    
    /**
     * The first three parameters are got from local storage eg. a database
     * @param encryptedUsername 
     * @param encryptedPassword
     * @param randPinPartDecode
     * @param pin This is a string greater than 4 characters entered by the user
     * @return
     */
    
    public static HashMap<String, String> decryptCredentials(String encryptedUsername,
            String encryptedPassword, String randPinPartDecode, String pin) {
        HashMap<String, String> credentials = new HashMap<String, String>();
        
        if (isValidFormat(pin)) {
            SecretKey yoSecretKey = YoSecurityUtils.makeKey(pin + randPinPartDecode);
            String username = YoSecurityUtils.decryptString(encryptedUsername, yoSecretKey);
            String password = YoSecurityUtils.decryptString(encryptedPassword, yoSecretKey);
            // return the credentials
            credentials.put("username", username);
            credentials.put("password", password);
            return credentials;
        }else{
            return null;
        }
    }
    /**
     * Changes the current pin of a user
     * @param oldPin
     * @param newPin
     * @param encryptedUsername
     * @param encryptedPassword
     * @param randPinPartDecode
     * @return
     */
    
    public static HashMap<String, String> changePin(String oldPin, String newPin,
            String encryptedUsername, String encryptedPassword, String randPinPartDecode) {
        HashMap<String, String> decryptedCredentials = new HashMap<String, String>();
        HashMap<String, String> encryptedCredentials = new HashMap<String, String>();
        
        decryptedCredentials = decryptCredentials(encryptedUsername, encryptedPassword,
                randPinPartDecode, oldPin);
        
        if (isValidPin(encryptedUsername, encryptedPassword,randPinPartDecode, oldPin)) {
            encryptedCredentials = encryptCredentials(decryptedCredentials.get("username"),
                    decryptedCredentials.get("password"), newPin);
            return encryptedCredentials;
        } else {
            return null;
        }
    }
    
    public static boolean isValidPin(String pin, String encryptedUsername,
            String encryptedPassword, String randPinPartDecode) {
        HashMap<String, String> decryptedCredentials = new HashMap<String, String>();
        decryptedCredentials = decryptCredentials(encryptedUsername, encryptedPassword,
                randPinPartDecode, pin);
        if (decryptedCredentials.get("username") != null
                || decryptedCredentials.get("password") != null) {
            return true;
        } else {
            return false;
        }
    }
    
    private static boolean isValidFormat(String pin) {
        if (pin.length() > 3) {
            return true;
        } else {
            return false;
        }
    }
}
