package ru.spbau.gorokhov.ftp.server.utils;

/**
 * Storing information about server state: if it's running or not.
 */
public class ServerState {
    private boolean running;

    public boolean isRunning() {
        return running;
    }

    public boolean isStopped() {
        return !running;
    }

    public void run() {
        running = true;
    }

    public void stop() {
        running = false;
    }
}
