import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * Created by wackloner on 30-Mar-17.
 */
public class ForkJoinEncoder {
    public byte[] encode(Path path) {
        return new ForkJoinPool().invoke(new MD5Counter(path));
    }

    class MD5Counter extends RecursiveTask<byte[]> {
        private final Path filePath;

        public MD5Counter(Path filePath) {
            this.filePath = filePath;
        }

        @Override
        protected byte[] compute() {
            try {
                if (!Files.isDirectory(filePath)) {
                    byte[] content = Files.readAllBytes(filePath);
                    byte[] result = MD5Encoder.encode(content);
                    return result;
                } else {
                    List<Path> subPaths = Files.list(filePath).collect(Collectors.toList());

                    List<MD5Counter> subTasks = new ArrayList<>();

                    for (Path subPath : subPaths) {
                        MD5Counter subTask = new MD5Counter(subPath);
                        subTask.fork();
                        subTasks.add(subTask);
                    }

                    byte[] directoryNameBytes = filePath.getFileName().toString().getBytes();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Utils.writeBytesToStream(directoryNameBytes, byteArrayOutputStream);

                    for (MD5Counter subTask : subTasks) {
                        Utils.writeBytesToStream(subTask.join(), byteArrayOutputStream);
                    }

                    byte[] directoryBytes = byteArrayOutputStream.toByteArray();
                    byte[] result = MD5Encoder.encode(directoryBytes);
                    return result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
