package ru.spbau.gorokhov.ftp.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbau.gorokhov.ftp.client.SimpleFTPClient;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPDisconnectedException;
import ru.spbau.gorokhov.ftp.client.exceptions.SimpleFTPException;
import ru.spbau.gorokhov.ftp.server.utils.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * SimpleFTPServer client with graphical interface.
 * Allows walking on server's directory tree and downloading files from there.
 */
public class SimpleFTPGUI extends Application {
    private static final Logger log = LoggerFactory.getLogger(SimpleFTPGUI.class);

    private static final String VERSION = "1.0";
    private static final String TITLE = "Simple FTP GUI Client";

    private static final String DEFAULT_HOSTNAME = "localhost";
    private static final String DEFAULT_PORT = "8080";

    private static final String DEFAULT_DOWNLOADS_DIRECTORY = Paths.get(System.getProperty("user.home"), "Downloads").toString();

    private static final FileInfo BACK_DIRECTORY = new FileInfo("..", true);

    @Nullable
    private Stage primaryStage;

    private final TextField remoteDirectoryNameField = new TextField();
    @NotNull
    private String remoteDirectoryName = "";

    private final ObservableList<FileInfo> directoryFiles = FXCollections.observableArrayList();

    private final TextField serverHostname = new TextField();
    private final TextField serverPort = new TextField();

    private final TextField directoryForDownloads = new TextField();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        primaryStage.setTitle(String.format("%s %s", TITLE, VERSION));

        Scene mainScene = buildMainScene();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    @NotNull
    private Scene buildMainScene() {
        HBox serverPane = buildServerPane();
        HBox downloadsPane = buildDownloadsPane();
        HBox directoryPane = buildDirectoryPane();
        TableView<FileInfo> table = buildFilesTable();

        VBox vbox = new VBox(serverPane, downloadsPane, directoryPane, table);

        return new Scene(vbox);
    }

    @NotNull
    private HBox buildServerPane() {
        Label hostnameLabel = new Label("Server hostname: ");
        Label portLabel = new Label("Server port: ");

        // allowing only digits in the field
        serverPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                serverPort.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        serverHostname.setText(DEFAULT_HOSTNAME);
        serverPort.setText(DEFAULT_PORT);

        return new HBox(hostnameLabel, serverHostname, portLabel, serverPort);
    }


    @NotNull
    private HBox buildDownloadsPane() {
        Label label = new Label("Local directory for downloading files: ");

        directoryForDownloads.setText(DEFAULT_DOWNLOADS_DIRECTORY);

        Button browsing = new Button("Browse");
        browsing.setOnMouseClicked(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                directoryForDownloads.setText(selectedDirectory.toString());
            }
        });

        return new HBox(label, directoryForDownloads, browsing);
    }

    @NotNull
    private HBox buildDirectoryPane() {
        Label label = new Label("Current remote directory: ");

        Button button = new Button("List");
        button.setOnMouseClicked(event -> {
            String directoryName = remoteDirectoryNameField.getText();
            listDirectory(directoryName);
        });

        remoteDirectoryNameField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                String directoryName = remoteDirectoryNameField.getText();
                listDirectory(directoryName);
            }
        });

        return new HBox(label, remoteDirectoryNameField, button);
    }

    @NotNull
    private TableView<FileInfo> buildFilesTable() {
        TableView<FileInfo> filesTable = new TableView<>();

        TableColumn<FileInfo, String> fileNameColumn = new TableColumn<>("Name");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileNameColumn.setPrefWidth(150);

        TableColumn<FileInfo, String> fileTypeColumn = new TableColumn<>("Type");
        fileTypeColumn.setCellValueFactory(cellValue -> {
            FileInfo file = cellValue.getValue();
            String fileType = "";
            if (!file.getFileName().equals("..")) {
                fileType = file.isDirectory() ? "directory" : "file";
            }
            return new SimpleObjectProperty<>(fileType);
        });
        fileTypeColumn.setPrefWidth(70);

        filesTable.setItems(directoryFiles);
        filesTable.getColumns().addAll(fileNameColumn, fileTypeColumn);

        filesTable.setRowFactory(tv -> {
            TableRow<FileInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    FileInfo currentFile = row.getItem();
                    String fileName = currentFile.getFileName();
                    String fullFileName = Paths.get(remoteDirectoryName, fileName).toString();
                    if (currentFile.isDirectory()) {
                        if (fileName.equals("..")) {
                            fullFileName = getParentDirectoryName(remoteDirectoryName);
                        }
                        listDirectory(fullFileName); // if there's '..', then parent isn't null
                    } else {
                        downloadFile(fullFileName);
                    }
                }
            });
            return row;
        });

        return filesTable;
    }

    private void downloadFile(@NotNull String fileName) {
        log.info(String.format("Trying to download remote file '%s'.", fileName));

        String downloadDirectory = directoryForDownloads.getText();
        Path downloadDirectoryPath = Paths.get(downloadDirectory);
        if (!Files.exists(downloadDirectoryPath) || !Files.isDirectory(downloadDirectoryPath)) {
            alertError(String.format("Directory '%s' does not exist.", downloadDirectory));
            return;
        }

        String shortFileName = Paths.get(fileName).getFileName().toString();
        Path localFilePath = Paths.get(downloadDirectory, shortFileName);

        Platform.runLater(() -> {
            SimpleFTPClient client = getClient();

            if (client == null) {
                alertError("Connection to server failed.");
            } else {
                try {
                    Platform.runLater(() -> alertSuccess(String.format("Downloading file '%s'", fileName)));

                    byte[] fileContent = client.executeGet(fileName);

                    Files.write(localFilePath, fileContent);

                    alertSuccess(String.format("File was written to '%s'", localFilePath.toString()));
                } catch (SimpleFTPDisconnectedException e) {
                    alertError(String.format("Failed to download file '%s'.", fileName));
                } catch (IOException e) {
                    alertError(String.format("Failed to write file '%s'.", localFilePath.toString()));
                }

                try {
                    client.disconnect();
                } catch (SimpleFTPDisconnectedException e) {
                }
            }
        });
    }

    private void listDirectory(@NotNull String directoryName) {
        log.info(String.format("Trying to list remote directory '%s'.", directoryName));

        try {
            SimpleFTPClient client = getClient();

            if (client == null) {
                alertError("Connection to server failed.");
                return;
            }

            List<FileInfo> list = client.executeList(directoryName);

            directoryFiles.clear();

            String parentDirectoryName = getParentDirectoryName(directoryName);
            if (parentDirectoryName != null) {
                directoryFiles.add(BACK_DIRECTORY);
            }

            directoryFiles.addAll(list);

            remoteDirectoryName = directoryName;
            remoteDirectoryNameField.setText(directoryName);

            client.disconnect();

            log.info(String.format("Remote directory '%s' was successfully listed.", directoryName));
        } catch (SimpleFTPDisconnectedException e) {
            alertError(String.format("Failed to list remote directory '%s'.", directoryName));
        }
    }

    @Nullable
    private SimpleFTPClient getClient() {
        SimpleFTPClient client = new SimpleFTPClient(serverHostname.getText(), Integer.parseInt(serverPort.getText()));
        try {
            client.connect();
        } catch (SimpleFTPException e) {
            alertError("Connection to server failed.");
            return null;
        }
        return client;
    }

    private static void alertError(@NotNull String message) {
        log.info(message);

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private static void alertSuccess(@NotNull String message) {
        log.info(message);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Success!");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @Nullable
    private static String getParentDirectoryName(String pathName) {
        Path path = Paths.get(pathName);
        Path parentPath = path.getParent();
        return parentPath == null ? null : parentPath.toString();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
