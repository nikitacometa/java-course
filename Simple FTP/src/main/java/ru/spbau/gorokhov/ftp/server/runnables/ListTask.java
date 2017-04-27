package ru.spbau.gorokhov.ftp.server.runnables;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that sends information about specified server directory to client in separate thread.
 */
@AllArgsConstructor
class ListTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ListTask.class);

    @NotNull
    private final DataOutputStream clientInput;
    @NotNull
    private final String directoryName;

    @Override
    public void run() {
        log.info("Trying to send information about directory '" + directoryName + "'.");

        try {
            Path directoryPath = Paths.get(directoryName);
            if (Files.isDirectory(directoryPath)) {
                List<Path> paths = Files.list(directoryPath).collect(Collectors.toList());
                clientInput.writeInt(paths.size());
                for (Path path : paths) {
                    clientInput.writeUTF(path.getFileName().toString());
                    clientInput.writeBoolean(Files.isDirectory(path));
                }
            } else {
                clientInput.writeInt(0);
            }

            log.info("Information about directory '" + directoryName + "' was sent.");
        } catch (IOException e) {
            log.error("Error occurred while trying to send information.");
        }
    }
}
