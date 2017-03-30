import java.io.ByteArrayOutputStream;

/**
 * Created by wackloner on 30-Mar-17.
 */
public class Utils {
    public static String bytesToString(byte[] bytes) {
        String bytesString = new String(bytes);
        return bytesString;
    }

    public static void writeBytesToStream(byte[] bytes, ByteArrayOutputStream byteArrayOutputStream) {
        byteArrayOutputStream.write(bytes, 0, bytes.length);
    }
}
