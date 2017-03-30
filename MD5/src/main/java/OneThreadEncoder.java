import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements Encoder interface with one thread
 */
public class OneThreadEncoder implements Encoder {
    public byte[] encode(Path path) throws IOException, NoSuchAlgorithmException {
        if (Files.isDirectory(path)) {
            return encodeDirectory(path);
        } else {
            return encodeFile(path);
        }
    }

    private byte[] encodeFile(Path path) throws IOException, NoSuchAlgorithmException {
        byte[] content = Files.readAllBytes(path);
        byte[] result = MD5Encoder.encode(content);
        return result;
    }

    private byte[] encodeDirectory(Path path) throws IOException, NoSuchAlgorithmException {
        byte[] directoryNameBytes = path.getFileName().toString().getBytes();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Utils.writeBytesToStream(directoryNameBytes, byteArrayOutputStream);
        List<Path> subPaths = Files.list(path).collect(Collectors.toList());
        for (Path subPath : subPaths) {
            Utils.writeBytesToStream(encode(subPath), byteArrayOutputStream);
        }
        byte[] directoryBytes = byteArrayOutputStream.toByteArray();
        byte[] result = MD5Encoder.encode(directoryBytes);
        return result;
    }
}
