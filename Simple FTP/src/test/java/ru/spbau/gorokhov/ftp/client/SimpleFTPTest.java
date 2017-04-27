package ru.spbau.gorokhov.ftp.client;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPAlreadyConnectedException;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPDisconnectedException;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPException;
import ru.spbau.gorokhov.ftp.server.SimpleFTPServer;
import ru.spbau.gorokhov.ftp.server.utils.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;


public class SimpleFTPTest {
    private static final int TEST_PORT = 8080;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    private List<FileInfo> currentFiles = new ArrayList<>();

    private static SimpleFTPServer server;
    private SimpleFTPClient client;

    @BeforeClass
    public static void runServer() {
        server = new SimpleFTPServer(TEST_PORT);
        server.start();
    }

    @Before
    public void setUp() throws SimpleFTPException {
        client = new SimpleFTPClient("localhost", TEST_PORT);
        client.connect();
    }

    @Test(expected = SimpleFTPAlreadyConnectedException.class)
    public void alreadyConnectedTest() throws SimpleFTPException {
        client.connect();
    }

    @Test
    public void listTest() throws IOException, SimpleFTPDisconnectedException {
        for (int iteration = 0; iteration < 10; iteration++) {
            for (int i = 0; i < 5; i++) {
                createFile();
                createFolder();
            }

            List<FileInfo> actual = client.executeList(testFolderName());

            assertListEquals(actual, currentFiles);
        }
    }

    @Test
    public void getTest() throws IOException, SimpleFTPDisconnectedException {
        int fileSize = 1;
        for (int iteration = 0; iteration < 9; iteration++) {
            Path filePath = createFile();

            byte[] expected = getRandomBytes(fileSize);
            Files.write(filePath, expected);

            byte[] actual = client.executeGet(filePath.toString());

            assertArrayEquals(actual, expected);

            fileSize *= 10;
        }

        // directory test
        byte[] empty = new byte[0];
        byte[] actual = client.executeGet(testFolderName());
        assertArrayEquals(empty, actual);

        // not existing file test
        actual = client.executeGet("randomfilenamethatsurelydoesntexist");
        assertArrayEquals(empty, actual);
    }

    @Test(expected = SimpleFTPDisconnectedException.class)
    public void disconnectTest() throws SimpleFTPDisconnectedException {
        client.disconnect();

        client.executeGet("fileName");
    }

    @Test
    public void multipleClientsTest() throws SimpleFTPException, IOException {
        SimpleFTPClient client1 = new SimpleFTPClient("localhost", TEST_PORT);
        client1.connect();

        SimpleFTPClient client2 = new SimpleFTPClient("localhost", TEST_PORT);
        client2.connect();

        for (int i = 0; i < 1000; i++) {
            createFile();
        }

        List<FileInfo> actual = client.executeList(testFolderName());
        List<FileInfo> actual1 = client1.executeList(testFolderName());
        List<FileInfo> actual2 = client2.executeList(testFolderName());

        assertListEquals(actual, currentFiles);
        assertListEquals(actual1, currentFiles);
        assertListEquals(actual2, currentFiles);

        client1.disconnect();
        client2.disconnect();
    }

    private Path createFile() throws IOException {
        File newFile = testFolder.newFile();
        String fileName = newFile.getName();
        currentFiles.add(new FileInfo(fileName, false));
        return newFile.toPath();
    }

    private Path createFolder() throws IOException {
        File newFolder = testFolder.newFolder();
        String folderName = newFolder.getName();
        currentFiles.add(new FileInfo(folderName, true));
        return newFolder.toPath();
    }

    private byte[] getRandomBytes(int count) {
        byte[] res = new byte[count];
        RANDOM.nextBytes(res);
        return res;
    }

    private static <T> void assertListEquals(List<T> actual, List<T> expected) {
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    private String testFolderName() {
        return testFolder.getRoot().getAbsolutePath();
    }

    @After
    public void tearDown() throws SimpleFTPDisconnectedException {
        if (client.connectedToServer()) {
            client.disconnect();
        }
    }

    @AfterClass
    public static void stopServer() {
        server.stop();
    }
}
