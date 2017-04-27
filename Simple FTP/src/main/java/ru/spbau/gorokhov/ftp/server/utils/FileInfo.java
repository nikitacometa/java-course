package ru.spbau.gorokhov.ftp.server.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Class represents short information about file on the server.
 * Stores file name and type (directory or not).
 */
@AllArgsConstructor
@Getter
public class FileInfo {
    @NotNull
    private final String fileName;
    @NotNull
    private final Boolean isDirectory;

    @Override
    public String toString() {
        return "'" + fileName + "', " + (isDirectory ? "directory" : "file");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileInfo) {
            FileInfo that = (FileInfo) o;
            return this.fileName.equals(that.getFileName()) && this.isDirectory.equals(that.getIsDirectory());
        }
        return false;
    }
}
