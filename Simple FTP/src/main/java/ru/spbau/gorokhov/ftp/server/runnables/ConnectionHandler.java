package ru.spbau.gorokhov.ftp.server.runnables;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.gorokhov.ftp.server.utils.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


@AllArgsConstructor
class ConnectionHandler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);

    @NotNull
    private final Socket clientSocket;
    @NotNull
    private final ServerState serverState;


    @Override
    public void run() {
        log.info("Started working with new client.");

        DataInputStream clientOutput;
        DataOutputStream clientInput;

        try {
            clientOutput = new DataInputStream(clientSocket.getInputStream());
            clientInput = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            log.error("Failed to obtain client's i/o streams.");
            return;
        }

        try {
            while (serverState.isRunning()) {
                int requestType;
                try {
                    requestType = clientOutput.readInt();
                } catch (Exception e) {
                    break;
                }
                if (requestType == 1) {
                    String directoryName = clientOutput.readUTF();
                    new Thread(new ListTask(clientInput, directoryName)).start();
                } else if (requestType == 2) {
                    String fileName = clientOutput.readUTF();
                    new Thread(new GetTask(clientInput, fileName)).start();
                } else {
                    log.info("Got request with invalid type number: " + requestType);
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while obtaining data from client.", e);
        }

        log.info("Lost connection with client.");
    }
}