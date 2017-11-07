import java.nio.file.Path;

/**
 * Interface with method that encodes path file\directory to MD5 hash
 */
public interface Encoder {
    /**
     * Encodes specified path
     * @param path file path
     * @return byte array of hash
     * @throws Exception
     */
    byte[] encode(Path path) throws Exception;
}
