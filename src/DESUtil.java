import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.io.*;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class DESUtil {

   
    /**
     * 　　* encrypt
     * 　　* @param key 
     * 　　* @param data 
     * 　　* @return 
     * 　　* @throws EncryptException
     */
    public static byte[] encrypt(Key key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
  
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] nbyte= cipher.doFinal(data);
            return nbyte;
    }



    /**
     * 　　* decrypt
     * 　　* @param key 
     * 　　* @param raw 
     * 　　* @return 
     * 　　* @throws EncryptException
     */
    public static byte[] decrypt(Key key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(cipher.DECRYPT_MODE, key);
            byte[] nbyte= cipher.doFinal(data);
            return nbyte;
    }
    /**
     * 　　*
     * 　　* @param args
     * 　　* @throws Exception
     */
  

}