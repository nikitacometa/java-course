package ru.spbau.gorokhov.ftp.client;

import org.jetbrains.annotations.NotNull;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPAlreadyConnectedException;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPDisconnectedException;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPException;
import ru.spbau.gorokhov.ftp.server.utils.FileInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which implements client for communicating with SimpleFTPServer.
 */
public class SimpleFTPClient {
    private static final int LIST_COMMAND_ID = 1;
    private static final int GET_COMMAND_ID = 2;

    private final String serverHost;
    private final int serverPort;

    private Socket serverSocket;
    private DataOutputStream serverInput;
    private DataInputStream serverOutput;

    private boolean hasConnection = false;


    public SimpleFTPClient(@NotNull String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Connects client to the server.
     * @throws SimpleFTPException if already connected or if connection can't be established
     */
    public void connect() throws SimpleFTPException {
        if (hasConnection) {
            throw new SimpleFTPAlreadyConnectedException();
        }

        try {
            serverSocket = new Socket(serverHost, serverPort);
            serverInput = new DataOutputStream(serverSocket.getOutputStream());
            serverOutput = new DataInputStream(serverSocket.getInputStream());
            hasConnection = true;
        } catch (IOException e) {
            throw new SimpleFTPDisconnectedException("Unable to connect to the server.");
        }
    }

    /**
     * Disconnects client from the server.
     * @throws SimpleFTPDisconnectedException if already disconnected of having troubles when disconnecting
     */
    public void disconnect() throws SimpleFTPDisconnectedException {
        if (!hasConnection) {
            throw new SimpleFTPDisconnectedException("Already disconnected from server.");
        }

        try {
            serverInput.close();
            serverOutput.close();
            serverSocket.close();
            hasConnection = false;
        } catch (IOException e) {
            throw new SimpleFTPDisconnectedException(e);
        }
    }

    /**
     * Getting information about all the files in specified directory on the server.
     * Information about file contains fileName and flag is it directory of not.
     * @param directoryName server directory that we want to observe
     * @return list off all files in directory in format <fileName, isDirectory>
     * @throws SimpleFTPDisconnectedException if connection to the server is lost
     */
    @NotNull
    public List<FileInfo> executeList(@NotNull String directoryName) throws SimpleFTPDisconnectedException {
        if (!hasConnection) {
            throw new SimpleFTPDisconnectedException();
        }

        try {
            serverInput.writeInt(LIST_COMMAND_ID);
            serverInput.writeUTF(directoryName);
            serverInput.flush();

            List<FileInfo> fileInfos = new ArrayList<>();
            int filesCount = serverOutput.readInt();
            for (int i = 0; i < filesCount; i++) {
                String fileName = serverOutput.readUTF();
                boolean isDirectory = serverOutput.readBoolean();
                fileInfos.add(new FileInfo(fileName, isDirectory));
            }

            return fileInfos;
        } catch (IOException e) {
            disconnect();
            throw new SimpleFTPDisconnectedException("Error occurred while obtaining information about directory, disconnected from server.", e);
        }
    }

    private static final int BUFFER_SIZE = 4096;

    /**
     * Downloading specified file from the server.
     * @param fileName file to download
     * @return byte content of the file
     * @throws SimpleFTPDisconnectedException if connection to the server is lost
     */
    @NotNull
    public byte[] executeGet(@NotNull String fileName) throws SimpleFTPDisconnectedException {
        if (!hasConnection) {
            throw new SimpleFTPDisconnectedException();
        }

        try {
            serverInput.writeInt(GET_COMMAND_ID);
            serverInput.writeUTF(fileName);
            serverInput.flush();

            long totalBytes = serverOutput.readLong();

            if (totalBytes == 0) {
                return new byte[0];
            }

            ByteArrayOutputStream content = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            long readBytes = 0;
            while (readBytes < totalBytes) {
                int pieceSize = (int) Math.min(BUFFER_SIZE, totalBytes - readBytes);
                int currentBytes = serverOutput.read(buffer, 0, pieceSize);
                content.write(buffer, 0, currentBytes);
                readBytes += currentBytes;
            }

            return content.toByteArray();
        } catch (IOException e) {
            disconnect();
            throw new SimpleFTPDisconnectedException("Error occurred while getting the file, disconnected from server.", e);
        }
    }

    /**
     * Shows if there is connection to the server.
     * @return <tt>true</tt> if connected, <tt>false</tt> otherwise
     */
    public boolean connectedToServer() {
        return hasConnection;
    }
}
