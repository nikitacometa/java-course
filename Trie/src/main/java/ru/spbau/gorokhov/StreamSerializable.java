package ru.spbau.gorokhov;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wackloner on 25.09.16 in 12:13.
 */
public interface StreamSerializable {

    /**
     * Write current state to output stream
     *
     * @param out output stream
     * @throws IOException
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Replace current state with data from input stream
     *
     * @param in input stream
     * @throws IOException
     */
    void deserialize(InputStream in) throws IOException;
}