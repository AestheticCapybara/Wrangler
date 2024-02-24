package com.aesthetic.wrangler.core.encryption;

import com.aesthetic.wrangler.core.FileHandler;
import com.aesthetic.wrangler.core.LogHandler;
import com.aesthetic.wrangler.core.encryption.encryption_core.Crypto;

import java.io.*;
import java.security.*;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncryptionHandler {
    private static final int HEADER_LENGTH = 128;
    private static final int BUFFER_BLOCK_SIZE = 4096;

    private static String hashAlgorithm = "SHA256";
    private static int hashLength = 32;
    private static String cryptoAlgorithm = "AES";
    private static String cryptoTransformation = "AES/CBC/PKCS5Padding";
    private static String pass;

    public static void init(String _hashAlgorithm, String _cryptoAlgorithm, String _mode, String _pass) {
        hashAlgorithm = _hashAlgorithm;
        switch (_hashAlgorithm) {
            case "MD5":
                hashLength = 16;
                break;
            case "SHA-256":
                hashLength = 32;
                break;
        }
        cryptoAlgorithm = _cryptoAlgorithm;
        cryptoTransformation = (_cryptoAlgorithm + "/" + _mode + "/PKCS5Padding");
        pass = _pass;
    }

    private static byte[] generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            LogHandler.log("[Encryption Handler] Couldn't generate hash. " + ex);
            return null;
        }
    }
    private static byte[] trimNullBytes(byte[] input) {
        int i = input.length - 1;
        while (i >= 0 && input[i] == 0) {
            --i;
        }
        return Arrays.copyOfRange(input, 0, i + 1);
    }

    public static void encrypt() {
        try {
            byte[] hash = generateHash(pass);

            BufferedInputStream inputStream;
            BufferedOutputStream outputStream;

            for (File file : FileHandler.getSelectedFiles()) {
                byte[] iv = Crypto.generateIV();

                String baseFilename = FileHandler.trimFilename(file.getAbsolutePath());
                String extension = ".wrangled";
                File outputFile = new File(baseFilename + extension);

                int i = 0;
                while (outputFile.exists()) {
                    i++;
                    outputFile = new File(baseFilename + "_" + i + extension);
                }
                outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

                Header header = new Header(hash, iv, FileHandler.getExtension(file.getName()).getBytes());
                outputStream.write(header.getBytes(HEADER_LENGTH));
                inputStream = new BufferedInputStream(new FileInputStream(file));

                Crypto.init(cryptoAlgorithm, cryptoTransformation, Cipher.ENCRYPT_MODE, hash, iv);

                byte[] block = new byte[BUFFER_BLOCK_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(block)) != -1) {
                    byte[] encryptedBlock;
                    inputStream.mark(block.length + 1);
                    if (inputStream.read() != -1) {
                        inputStream.reset();
                        encryptedBlock = Crypto.update(Arrays.copyOf(block, bytesRead));
                    } else encryptedBlock = Crypto.doFinal(Arrays.copyOf(block, bytesRead));

                    outputStream.write(encryptedBlock);
                }

                inputStream.close();
                outputStream.close();
            }
        } catch (Exception ex) {
            LogHandler.log("[Encryption Handler] Couldn't encrypt file." + ex);
        }
    }
    public static void decrypt() {
        try {
            byte[] hash = generateHash(pass);

            BufferedInputStream inputStream;
            BufferedOutputStream outputStream;

            for (File file : FileHandler.getSelectedFiles()) {
                inputStream = new BufferedInputStream(new FileInputStream(file));

                byte[] header = new byte[HEADER_LENGTH];
                inputStream.read(header);
                Header dissectedHeader = new Header(trimNullBytes(header), hashLength);

                if (!Arrays.equals(hash, dissectedHeader.getHash())) {
                    LogHandler.log("[Encryption Handler] Aborting decryption - hash discrepancy. Check your password!");
                    inputStream.close();
                    return;
                }

                String baseFilename = FileHandler.trimFilename(file.getAbsolutePath());
                File outputFile = new File(baseFilename + "." + dissectedHeader.getExtensionString());

                int i = 0;
                while (outputFile.exists()) {
                    i++;
                    outputFile = new File(baseFilename + "_" + i + "." + dissectedHeader.getExtensionString());
                }
                outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

                Crypto.init(cryptoAlgorithm, cryptoTransformation, Cipher.DECRYPT_MODE, hash, dissectedHeader.getIv());

                byte[] block = new byte[BUFFER_BLOCK_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(block)) != -1) {
                    byte[] encryptedBlock;
                    inputStream.mark(block.length + 1);

                    if (inputStream.read() != -1) {
                        inputStream.reset();
                        encryptedBlock = Crypto.update(Arrays.copyOf(block, bytesRead));
                    } else encryptedBlock = Crypto.doFinal(Arrays.copyOf(block, bytesRead));

                    outputStream.write(encryptedBlock);
                }

                inputStream.close();
                outputStream.close();
            }
        } catch (Exception ex) {
            LogHandler.log("[Encryption Handler] Couldn't decrypt file. " + ex);
        }
    }
}