package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wackloner on 25.09.16 in 12:13.
 */
public interface StreamSerializable {

    void serialize(OutputStream out) throws IOException;

    /**
     * Replace current state with data from input stream
     */
    void deserialize(InputStream in) throws IOException;
}