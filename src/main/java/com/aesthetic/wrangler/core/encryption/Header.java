package com.aesthetic.wrangler.core.encryption;

import com.aesthetic.wrangler.core.LogHandler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Header {
    private byte[] hash;
    private byte[] iv;
    private byte[] extension;

    public Header(byte[] header, int hashLength) {
        try {
            hash = Arrays.copyOfRange(header, 0, hashLength);
            iv = Arrays.copyOfRange(header, hashLength, hashLength + 16);
            extension = Arrays.copyOfRange(header, hashLength + 16, header.length);
        } catch (Exception ex) {
            LogHandler.log("[Encryption Handler: Header] Couldn't dissect header. " + ex);
        }
    }
    public Header(byte[] hash, byte[] iv, byte[] extension) {
        this.hash = hash;
        this.iv = iv;
        this.extension = extension;
    }

    public byte[] getHash() {
        return hash;
    }
    public byte[] getIv() {
        return iv;
    }
    public byte[] getExtension() {
        return extension;
    }
    public String getExtensionString() {
        return new String(extension, StandardCharsets.UTF_8);
    }

    public byte[] getBytes(int size) {
        byte[] combined = new byte[size];
        System.arraycopy(hash, 0, combined, 0, hash.length);
        System.arraycopy(iv, 0, combined, hash.length, iv.length);
        System.arraycopy(extension, 0, combined, hash.length + iv.length, extension.length);
        return combined;
    }
}