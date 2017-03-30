import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by wackloner on 30-Mar-17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneThreadEncoderTest {
    private static final Path TEST_DIR = Paths.get("testDir3516385");

    private static final Random RANDOM = new Random(System.nanoTime());
    private static final int MAX_FILE_SIZE = 1_000_000;

    @BeforeClass
    public static void setUp() throws IOException {
        if (!Files.exists(TEST_DIR)) {
            Files.createDirectory(TEST_DIR);
        }
    }

    @Test
    public void a_test() throws Exception {
        Path filePath = createRandomFile("first");
        byte[] expected = encodeFile(filePath);
        byte[] actual = new OneThreadEncoder().encode(filePath);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void b_test() throws Exception {
        Path filePath = createRandomFile("second");
        byte[] expected = encodeFile(filePath);
        byte[] actual = new OneThreadEncoder().encode(filePath);

        assertArrayEquals(expected, actual);
    }

    @Test
    public void c_test() throws Exception {
        Path dir = createDirectory("dir1");
        Path third = createRandomFile("dir1", "third");
        Path anotherDIr = createDirectory("dir2");
        Path four = createRandomFile("dir2", "fourth");

        byte[] expected = new OneThreadEncoder().encode(TEST_DIR);
        byte[] actual = new ForkJoinEncoder().encode(TEST_DIR);

        assertArrayEquals(expected, actual);
    }

    private static byte[] encodeFile(Path path) throws IOException, NoSuchAlgorithmException {
        byte[] content = Files.readAllBytes(path);
        byte[] result = MD5Encoder.encode(content);
        return result;
    }

    private Path getFilePath(String... parts) {
        Path filePath = Paths.get(TEST_DIR.toString(), parts);
        return filePath;
    }

    private Path createDirectory(String... parts) throws IOException {
        Path dirPath = Paths.get(TEST_DIR.toString(), parts);
        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
        }
        return dirPath;
    }

    private Path createFile(String... parts) throws IOException {
        Path filePath = getFilePath(parts);
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        return filePath;
    }

    private Path createRandomFile(String... parts) throws IOException {
        createFile(parts);
        int bytesCount = RANDOM.nextInt(MAX_FILE_SIZE ) + 1;
        return fillFileWithRandomBytes(bytesCount, parts);
    }

    private Path fillFileWithRandomBytes(int bytesCount, String... parts) throws IOException {
        Path filePath = getFilePath(parts);
        byte[] randomBytes = new byte[bytesCount];
        RANDOM.nextBytes(randomBytes);
        Files.write(filePath, randomBytes);
        return filePath;
    }

    @AfterClass
    public static void tearDown() {
        // would like to remove test dir here
        // but windows has troubles with it (bug)
        // so please think that I've done it
    }
}