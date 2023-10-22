package client;

import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Holds all encryption related functions
 */
public class encrypt {
    /**
     * Generates RSA keypair for file encryption
     * @return generated keypair
     */
    public static KeyPair generateRSAKeys() { 
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @deprecated Used to generate identifier key for identifying sender to the receiver
     * @return generated key
     */
    @Deprecated public static int  generateSimpleKey() {
       int ran = (int) Math.random() * 100000;
       return ran;
    }

    /**
     * Encrypts [data] with RSA [public key]
     * @param data data to encrypt
     * @param publicKey public key used to encrypt the data
     * @return encrypted data
     */
    public static ByteBuffer encryptData(ByteBuffer data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            ByteBuffer ret = ByteBuffer.wrap(cipher.doFinal(data.array())) ;
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * Decrypts [data] using RSA [private key]
     * @param data data to decrypt
     * @param privateKey key used to decrypt data
     * @return decrypted data
     */
    public static byte[] decryptData(ByteBuffer data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data.array());
        } catch (Exception e) {
            e.printStackTrace();
            return data.array();
        }
    }

    /**
     * Generate AES256 secret key
     * @return Secret Key
     */
    public static SecretKey generateAESkey() {
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(256);
            return gen.generateKey();
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
        
    }

    //TODO: Actuall security
    /**
     * Encrypt data in a bytebuffer using AES/ECB/NoPadding
     * @param data Bytebuffer with data to encrypt
     * @param key Key to use
     */
    public static ByteBuffer AESEncrypt(ByteBuffer data, SecretKey key) {
        return data;
    }

    /**
     * Decrypt data in a bytebuffer using AES/ECB/NoPadding
     * @param data Bytebuffer with data to decrypt
     * @param key Key to use
     */
    public static ByteBuffer AESDecrypt(ByteBuffer data, SecretKey key) {
        return data;
    }
}
