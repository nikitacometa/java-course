package ru.spbau.gorokhov.ftp.server.runnables;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.gorokhov.ftp.server.utils.ServerState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Class that are waiting for new clients connections in separate thread.
 * When new connection retrieved it's being sent to one more separate thread for handling.
 */
@AllArgsConstructor
public class ClientManager implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ClientManager.class);

    @NotNull
    private final ServerSocket serverSocket;
    @NotNull
    private final ServerState serverState;

    @Override
    public void run() {
        while (serverState.isRunning()) {
            Socket newConnection;
            try {
                newConnection = serverSocket.accept();
            } catch (IOException e) {
                log.error("Failed to establish new connection.", e);
                serverState.stop();
                return;
            }
            new Thread(new ConnectionHandler(newConnection, serverState)).start();
        }
    }
}
