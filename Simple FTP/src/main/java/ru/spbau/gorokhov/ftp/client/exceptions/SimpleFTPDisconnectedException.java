package ru.spbau.gorokhov.ftp.client.exceptions;


public class SimpleFTPDisconnectedException extends SimpleFTPException {
    private static final String DEFAULT_MESSAGE = "No connection with server.";

    public SimpleFTPDisconnectedException() {
        super(DEFAULT_MESSAGE);
    }

    public SimpleFTPDisconnectedException(String message) {
        super(message);
    }

    public SimpleFTPDisconnectedException(String message, Throwable t) {
        super(message, t);
    }

    public SimpleFTPDisconnectedException(Throwable t) {
        this(DEFAULT_MESSAGE, t);
    }
}
