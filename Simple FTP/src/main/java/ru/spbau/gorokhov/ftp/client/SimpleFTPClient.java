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
                serverOutput.read(buffer, 0, pieceSize);
                content.write(buffer, 0, pieceSize);
                readBytes += pieceSize;
            }

            return content.toByteArray();

//            byte[] content = new byte[(int) totalBytes];
//            serverOutput.read(content, 0, (int) totalBytes);
//
//            return content;
        } catch (IOException e) {
            disconnect();
            throw new SimpleFTPDisconnectedException("Error occurred while getting the file, disconnected from server.", e);
        }
    }

    public boolean connectedToServer() {
        return hasConnection;
    }
}
