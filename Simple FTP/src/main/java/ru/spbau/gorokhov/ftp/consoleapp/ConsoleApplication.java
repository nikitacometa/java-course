package ru.spbau.gorokhov.ftp.consoleapp;

import ru.spbau.gorokhov.ftp.client.SimpleFTPClient;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPException;
import ru.spbau.gorokhov.ftp.server.utils.FileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Class implements simple console application for working with server client.
 */
public class ConsoleApplication {
    private static final String CONNECT_COMMAND = "connect";
    private static final String LIST_COMMAND = "list";
    private static final String GET_COMMAND = "get";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String EXIT_COMMAND = "exit";
    private static final String HELP_COMMAND = "help";

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    private static final boolean SAVING_FILES_IN_TEMPORARY_DIRECTORY = true;
    private static final String TEMPORARY_DIRECTORY_NAME = "FilesDownloadedFromSimpleFTPServer";

    public static void main(String[] args) {
        new ConsoleApplication().work();
    }

    private void work() {
        SimpleFTPClient client = new SimpleFTPClient(SERVER_HOST, SERVER_PORT);
        System.out.println("Welcome, dear " + System.getProperty("user.name") + ", enter your commands!");
        showHelp();

        Scanner console = new Scanner(System.in);

        boolean running = true;
        while (running) {
            String command = console.next();

            try {
                switch (command) {
                    case CONNECT_COMMAND:
                        client.connect();
                        System.out.println("Connection was established.");
                        break;

                    case LIST_COMMAND:
                        String directoryName = console.next();
                        List<FileInfo> fileInfos = client.executeList(directoryName);
                        System.out.println(fileInfos.size() + " files in directory '" + directoryName + "':");
                        fileInfos.forEach(System.out::println);
                        break;

                    case GET_COMMAND:
                        String fileName = console.next();
                        byte[] content = client.executeGet(fileName);
                        System.out.println("File '" + fileName + "' was downloaded, total " + content.length + " bytes.");
                        if (SAVING_FILES_IN_TEMPORARY_DIRECTORY) {
                            saveFile(fileName, content);
                        }
                        break;

                    case DISCONNECT_COMMAND:
                        client.disconnect();
                        System.out.println("Disconnected from server.");
                        break;

                    case EXIT_COMMAND:
                        running = false;
                        System.out.println("Good bye!");
                        break;

                    case HELP_COMMAND:
                        showHelp();
                        break;

                    default:
                        System.out.println("Invalid command.");
                }
            } catch (SimpleFTPException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveFile(String fileName, byte[] content) {
        try {
            Path directoryPath = Paths.get(TEMPORARY_DIRECTORY_NAME);
            if (!Files.exists(directoryPath) || !Files.isDirectory(directoryPath)) {
                Files.createDirectory(directoryPath);
            }
        } catch (IOException e) {
            System.out.println("Unable to create temporary folder.");
            return;
        }

        Path prevFilePath = Paths.get(fileName);
        Path filePath = Paths.get(TEMPORARY_DIRECTORY_NAME, prevFilePath.getFileName().toString());
        try {
            Files.write(filePath, content);
            System.out.println("File was saved as '" + filePath.toString() + "'.");
        } catch (IOException e) {
            System.out.println("Unable to save the file.");
        }
    }

    private void showHelp() {
        String help = "List of available commands:\n" +
                "    connect             - connects to the  server\n" +
                "    list DIRECTORY_NAME - lists all files and directories in DIRECTORY_NAME directory on the server\n" +
                "    get FILE_NAME       - downloads FILE_NAME file from the server and saves it to temporary folder if SAVING_FILES_IN_TEMPORARY_FOLDER flag is set\n" +
                "    disconnect          - disconnects from the server\n" +
                "    exit                - exits application\n" +
                "    help                - shows help";
        System.out.println(help);
    }
}
