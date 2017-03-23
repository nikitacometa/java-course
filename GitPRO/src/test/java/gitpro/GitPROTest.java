package gitpro;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by wackloner on 23-Mar-17.
 */
public class GitPROTest {
    private static final String TEST_FOLDER = "testDir";

    private static Path testPath;

    @BeforeClass
    public static void setUp() throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        testPath = Paths.get(currentDirectory, TEST_FOLDER);
        Files.createDirectory(testPath);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        Files.delete(testPath);
    }
}