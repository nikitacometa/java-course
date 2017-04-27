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

            String directoryName = testFolder.getRoot().getAbsolutePath();
            List<FileInfo> actual = client.executeList(directoryName);

            assertThat(actual, containsInAnyOrder(currentFiles.toArray()));
        }
    }

    @Test
    public void getTest() throws IOException, SimpleFTPDisconnectedException {
        int fileSize = 10;
        for (int iteration = 0; iteration < 7; iteration++) {
            Path filePath = createFile();

            byte[] expected = getRandomBytes(fileSize);
            Files.write(filePath, expected);

            byte[] actual = client.executeGet(filePath.toString());

            assertArrayEquals(actual, expected);

            fileSize *= 10;
        }

        // directory test
        byte[] empty = new byte[0];
        byte[] actual = client.executeGet(testFolder.getRoot().getAbsolutePath());
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