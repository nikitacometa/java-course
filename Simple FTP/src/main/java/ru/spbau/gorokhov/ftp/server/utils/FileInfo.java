package ru.spbau.gorokhov.ftp.server.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


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
}
