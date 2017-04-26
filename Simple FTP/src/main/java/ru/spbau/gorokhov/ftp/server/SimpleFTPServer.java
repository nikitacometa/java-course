package ru.spbau.gorokhov.ftp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.gorokhov.ftp.server.runnables.ClientManager;
import ru.spbau.gorokhov.ftp.server.utils.ServerState;

import java.io.*;
import java.net.ServerSocket;


public class SimpleFTPServer {
    private static final Logger log = LoggerFactory.getLogger(SimpleFTPServer.class);

    private static final int DEFAULT_PORT_NUMBER = 8080;

    private final int port;

    private final ServerState serverState = new ServerState();

    private ServerSocket serverSocket;

    public SimpleFTPServer(int port) {
        this.port = port;
    }

    public SimpleFTPServer() {
        this(DEFAULT_PORT_NUMBER);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Failed to connect to port.", e);
            return;
        }

        log.info("Running...");

        serverState.run();
        new Thread(new ClientManager(serverSocket, serverState)).start();
    }

    public void stop() {
        serverState.stop();

        log.info("Stopping...");
    }

    public static void main(String[] args) {
        new SimpleFTPServer().start();
    }
}
