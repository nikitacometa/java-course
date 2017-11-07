package ru.spbau.gorokhov.ftp.client.exceptions;


public class SimpleFTPException extends Exception {
    public SimpleFTPException(String message) {
        super(message);
    }

    public SimpleFTPException(String message, Throwable t) {
        super(message, t);
    }
}
