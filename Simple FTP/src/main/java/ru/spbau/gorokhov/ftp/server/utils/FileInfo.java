package ru.spbau.gorokhov.ftp.server.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Class represents short information about file on the server.
 * Stores file name and type (directory or not).
 */
@AllArgsConstructor
public class FileInfo {
    @Getter
    @NotNull
    private final String fileName;
    @NotNull
    private final Boolean isDirectory;

    public Boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public String toString() {
        return "'" + fileName + "', " + (isDirectory ? "directory" : "file");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FileInfo) {
            FileInfo that = (FileInfo) o;
            return this.fileName.equals(that.getFileName()) && this.isDirectory.equals(that.isDirectory());
        }
        return false;
    }
}
