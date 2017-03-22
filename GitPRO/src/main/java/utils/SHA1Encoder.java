package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class SHA1Encoder {

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
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()){
            try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)){
                objectStream.writeObject(object);
            } catch (IOException e) {
                throw e;
            }
            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String getHash(Object object) {
        // TODO
        return null;
    }
}
