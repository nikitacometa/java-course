package ru.spbau.gorokhov.ftp.client.exceptions;


public class SimpleFTPAlreadyConnectedException extends SimpleFTPException {
    public SimpleFTPAlreadyConnectedException() {
        super("Already connected to server.");
    }
}
