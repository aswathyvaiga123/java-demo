package Java.Crypto;

import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

public class CryptoSymmetricCipherTest {





    public static String encrypt(SecretKeySpec skeySpec, String initVector, String value) {
        try {


            // Key length - 16 for AES 128, 32 for AES256
            int key_length = 32;

            // AES is a 128-bit block cipher, so IVs and counter nonces are 16 bytes
            int iv_length = 128 / 8;

            // Padding PKCS7

            // # COMBINE SALT, DIGEST AND DATA
            // hmac = HMAC(b_key2, hashes.SHA256(), CRYPTOGRAPHY_BACKEND)
            // hmac.update(b_ciphertext)
            // b_hmac = hmac.finalize()


            //skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));


            // Note
            // KerbTicket Encryption Type: AES-256-CTS-HMAC-SHA1-96
            //  AES in CTR (Counter) mode, and append an HMAC.
            // https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html

            // Type instance can be found here:
            // https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            // Other type getInstance
            // bouncycastle library
            // https://bouncycastle.org/
            // Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(SecretKeySpec key, String initVector, String encrypted) {

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            final byte[] decode = Base64.getDecoder().decode(encrypted);
            byte[] original = cipher.doFinal(decode);

            return new String(original);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void base64Test() {
        String encode = Base64.getEncoder().encodeToString("Nico".getBytes());
        byte[] decode = Base64.getDecoder().decode(encode);
        System.out.println(new String(decode,StandardCharsets.UTF_8));

    }

    @Test
    public void testEnc() throws NoSuchAlgorithmException, InvalidKeySpecException {

        String key = "welcome1";

        // key lengths: 128, 192 and 256 bits.
        // https://en.wikipedia.org/wiki/Advanced_Encryption_Standard
        final int keyLength = 256;

        // random bytes
        byte[] salt = new byte[32];
        new Random().nextBytes(salt);
        // Create a secret key from a password
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec ks = new PBEKeySpec(key.toCharArray(),salt,1024,keyLength);
        SecretKey s = f.generateSecret(ks);
        SecretKeySpec skeySpec = new SecretKeySpec(s.getEncoded(),"AES");




        String initVector = "RandomInitVector"; // 16 bytes IV

        final String encrypt = encrypt(skeySpec, initVector, "Hello World");
        System.out.println(encrypt);
        final String decrypt = decrypt(skeySpec, initVector, encrypt);
        System.out.println(decrypt);

    }
}
