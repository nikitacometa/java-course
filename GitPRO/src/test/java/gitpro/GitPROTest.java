package gitpro;

import gitpro.exceptions.GitPROException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by wackloner on 23-Mar-17.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GitPROTest {
    private static final String TEST_FOLDER = "testDir";

    private static Path testPath;

    @BeforeClass
    public static void setUp() throws IOException {
        String currentDirectory = System.getProperty("user.dir");
        testPath = Paths.get(currentDirectory, TEST_FOLDER);
        Files.createDirectory(testPath);
    }

    @Test
    public void a_test() throws GitPROException, IOException {
        GitPRO gitPRO = new GitPRO(testPath);
        gitPRO.initNewRepository();

        createAndWrite("first", "123");

        gitPRO.add("first");
        gitPRO.commit("first added");
        gitPRO.createBranch("other");
        gitPRO.checkout("other");

        writeToFile("first", "456");
        createAndWrite("second", "789");

        gitPRO.add("second");
        gitPRO.commit("second added, first reworked");
        gitPRO.checkout("master");

        assertDoesntExist("second");
        assertThat(readFile("first"), is("123"));

        gitPRO.checkout("other");

        assertExistsAndContains("second", "789");
    }

    @Test
    public void b_test() throws GitPROException, IOException {
        GitPRO gitPRO = new GitPRO(testPath);
        gitPRO.loadRepository();
        gitPRO.checkout("master");
        gitPRO.createBranch("onemore");
        gitPRO.checkout("onemore");

        createDirectory("folder");
        createAndWrite("folder/third", "012");
        writeToFile("second", "345");

        gitPRO.add("folder/third");
        gitPRO.commit("add third");
        gitPRO.checkout("other");

        assertDoesntExist("folder");

        gitPRO.merge("onemore");

        assertExistsAndContains("folder/third", "012");
        assertExistsAndContains("second", "345");
    }

    private static void createDirectory(String directoryName) throws IOException {
        Path directoryPath = getFilePath(directoryName);
        Files.createDirectory(directoryPath);
    }

    private static void createFile(String fileName) throws IOException {
        Path filePath = getFilePath(fileName);
        Files.createFile(filePath);
    }

    private static void writeToFile(String fileName, String content) throws IOException {
        Path filePath = getFilePath(fileName);
        Files.write(filePath, content.getBytes());
    }

    private static void createAndWrite(String fileName, String content) throws IOException {
        createFile(fileName);
        writeToFile(fileName, content);
    }

    private static String readFile(String fileName) throws IOException {
        Path filePath = getFilePath(fileName);
        byte[] content = Files.readAllBytes(filePath);
        return new String(content);
    }

    private static boolean exists(String fileName) {
        Path filePath = getFilePath(fileName);
        return Files.exists(filePath);
    }

    private static void assertExistsAndContains(String fileName, String content) throws IOException {
        assertExists(fileName);
        assertThat(readFile(fileName), is(content));
    }

    private static void assertExists(String fileName) {
        assertTrue(exists(fileName));
    }

    private static void assertDoesntExist(String fileName) {
        assertFalse(exists(fileName));
    }

    private static Path getFilePath(String fileName) {
        return Paths.get(testPath.toString(), fileName);
    }

    private static void deleteDirectory(Path directoryPath) throws IOException {
        List<Path> paths = Files.walk(directoryPath)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    @AfterClass
    public static void tearDown() throws IOException {
        deleteDirectory(testPath);
    }
}