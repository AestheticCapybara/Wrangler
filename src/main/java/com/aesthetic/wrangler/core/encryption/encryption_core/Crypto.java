package com.aesthetic.wrangler.core.encryption.encryption_core;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Crypto {
    private static Key secretKey;
    private static Cipher cipher;

    private static long getCRC32Checksum(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

    public static void init(String algorithm, String transformation, int cipherMode, byte[] hash, byte[] iv)
            throws CryptoException {
        try {
            secretKey = new SecretKeySpec(hash, algorithm);
            cipher = Cipher.getInstance(transformation);
            if (transformation.contains("CBC"))
                cipher.init(cipherMode, secretKey, new IvParameterSpec(iv));
            else cipher.init(cipherMode, secretKey);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                 | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            throw new CryptoException(ex);
        }
    }
    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    public static byte[] update(byte[] inputBytes) {
        return cipher.update(inputBytes);
    }
    public static byte[] doFinal(byte[] inputBytes)
            throws CryptoException {
        try {
            return cipher.doFinal(inputBytes);
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            throw new CryptoException(ex);
        }
    }
}
