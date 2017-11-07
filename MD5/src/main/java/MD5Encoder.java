import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wackloner on 30-Mar-17.
 */
public class MD5Encoder {
    public static byte[] encode(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] result = messageDigest.digest(bytes);
        return result;
    }
}
