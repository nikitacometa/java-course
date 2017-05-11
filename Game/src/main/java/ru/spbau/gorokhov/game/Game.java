package ru.spbau.gorokhov.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game extends Application {
    private final static int SIZE = 2;

    private final static long WAIT_TIME = 1000;

    private final static double CELL_SIZE = 40;


    private Stage primaryStage;

    private final int[][] numbers = new int[SIZE][SIZE];

    private final Button[][] cells = new Button[SIZE][SIZE];

    private final Cell[][] positions = new Cell[SIZE][SIZE];

    private final List<Cell> aliveCells = new ArrayList<>();

    private long startTime;

    private Cell chosenCell = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Game 1.0");

        startNewGame();

        primaryStage.show();
    }

    private void startNewGame() {
        primaryStage.setScene(new Scene(initTableCells()));

        startTime = System.currentTimeMillis();
    }

    @NotNull
    private GridPane initTableCells() {
        initTableNumbers();

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int x = i, y = j;

                cells[x][y] = new Button();
                cells[x][y].setPrefSize(CELL_SIZE, CELL_SIZE);

                cells[x][y].setOnMouseClicked(event -> {
                    cells[x][y].setText(Integer.toString(numbers[x][y]));

                    if (chosenCell == null) {
                        chosenCell = positions[x][y];
                    } else {
                        aliveCells.forEach(pos -> cells[pos.getX()][pos.getY()].setDisable(true));

                        boolean match = numbers[x][y] == numbers[chosenCell.getX()][chosenCell.getY()];
                        long waitTime = match ? 0 : WAIT_TIME;

                        Task<Void> sleeper = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                try {
                                    Thread.sleep(waitTime);
                                } catch (InterruptedException ignored) {
                                }
                                return null;
                            }

                            @Override
                            protected void succeeded() {
                                if (match) {
                                    aliveCells.remove(positions[x][y]);
                                    aliveCells.remove(positions[chosenCell.getX()][chosenCell.getY()]);

                                    if (aliveCells.size() == 0) {
                                        showWinAlert();
                                    }
                                } else {
                                    cells[x][y].setText("");
                                    cells[chosenCell.getX()][chosenCell.getY()].setText("");
                                }

                                aliveCells.forEach(pos -> cells[pos.getX()][pos.getY()].setDisable(false));

                                chosenCell = null;
                            }
                        };

                        new Thread(sleeper).start();
                    }
                });

                gridPane.add(cells[x][y], y, x);

                positions[x][y] = new Cell(x, y);
                aliveCells.add(positions[x][y]);
            }
        }
        return gridPane;
    }

    private void showWinAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(String.format("You've won the game! Your time is %dms.", System.currentTimeMillis() - startTime));
        alert.setContentText("Would you like to play one more time?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            startNewGame();
        } else {
            Platform.exit();
        }
    }

    private void initTableNumbers() {
        List<Integer> gen = new ArrayList<>();
        for (int i = 0; i < SIZE * SIZE / 2; i++) {
            gen.add(i);
            gen.add(i);
        }
        Collections.shuffle(gen);
        Iterator<Integer> iterator = gen.iterator();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                numbers[i][j] = iterator.next();
            }
        }
    }

    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    private class Cell {
        private final int x;
        private final int y;
    }
}
