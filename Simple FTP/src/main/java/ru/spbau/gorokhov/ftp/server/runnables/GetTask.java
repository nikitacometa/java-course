package ru.spbau.gorokhov.ftp.server.runnables;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class that sends specified file from server to client in separate thread.
 */
@AllArgsConstructor
class GetTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GetTask.class);

    @NotNull
    private final DataOutputStream clientInput;
    @NotNull
    private final String fileName;

    private static final int BUFFER_SIZE = 4096;

    @Override
    public void run() {
        log.info("Trying to send file '" + fileName + "'.");

        try {
            Path filePath = Paths.get(fileName);
            if (Files.isDirectory(filePath) || !Files.exists(filePath)) {
                clientInput.writeLong(0);

                log.info("Invalid file, response with size=0 was sent.");
            } else {
                long totalBytes = Files.size(filePath);
                clientInput.writeLong(totalBytes);
                if (totalBytes == 0) {
                    return;
                }

                try (FileInputStream fileStream = new FileInputStream(filePath.toFile())) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    long writtenBytes = 0;

                    while (writtenBytes < totalBytes) {
                        int pieceSize = (int) Math.min(BUFFER_SIZE, totalBytes - writtenBytes);
                        int readBytes = fileStream.read(buffer, 0, pieceSize);
                        clientInput.write(buffer, 0, readBytes);
                        writtenBytes += readBytes;
                    }

                    log.info("File '" + fileName + "' was sent.");
                } catch (IOException e) {
                    throw e;
                }
            }
        } catch (IOException e) {
            log.error("Error occurred while working with client's input.");
        }
    }
}
