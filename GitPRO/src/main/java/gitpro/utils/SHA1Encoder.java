package gitpro.utils;

import gitpro.exceptions.GitPROException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class SHA1Encoder {
    private static final String ALGORITHM_NAME = "SHA-1";

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++ ) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] objectToBytes(Object object) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getHash(Object object) throws GitPROException {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_NAME);
            byte[] objectBytes = objectToBytes(object);
            byte[] byteHash = md.digest(objectBytes);
            return bytesToHex(byteHash);
        } catch (NoSuchAlgorithmException e) {
            throw new GitPROException("Failed to load encrypting algorithm.", e);
        }
    }
}
