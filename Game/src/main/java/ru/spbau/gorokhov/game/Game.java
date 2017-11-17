package ru.spbau.gorokhov.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class implements the game where user has table NxN and
 * wants to much every cell with number X with another cell
 * with number X (there're two cells for each numbers in [0, N*N/2).
 */
public class Game extends Application {
    private final static int DEFAULT_SIZE = 2;

    private final static long WAIT_TIME = 400;

    private final static double CELL_SIZE = 50;


    private Stage primaryStage;

    private int[][] numbers;

    private Button[][] buttons;

    private Cell[][] cells;

    private List<Cell> aliveCells;

    private long startTime;

    @Nullable
    private Cell chosenCell = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Game 1.0");

        showStartDialog("Let's play some!");

        primaryStage.show();
    }

    private void showStartDialog(String message) {
        TextInputDialog dialog = new TextInputDialog(Integer.toString(DEFAULT_SIZE));
        dialog.setTitle("New game");
        dialog.setHeaderText(message);
        dialog.setContentText("Enter size of field (positive even number):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            try {
                int size = Integer.parseInt(result.get());

                if (size <= 0 || size % 2 == 1) {
                    showStartDialog("Number have to be positive and even!");
                } else {
                    startNewGame(size);
                }
            } catch (NumberFormatException e) {
                showStartDialog("Invalid number format!");
            }
        } else {
            Platform.exit();
        }
    }

    private void startNewGame(int size) {
        primaryStage.setScene(new Scene(initTableCells(size)));

        startTime = System.currentTimeMillis();
    }

    @NotNull
    private GridPane initTableCells(int size) {
        initTableNumbers(size);

        aliveCells = new ArrayList<>();

        cells = new Cell[size][size];

        buttons = new Button[size][size];

        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int x = i, y = j;

                buttons[x][y] = new Button();
                buttons[x][y].setMaxSize(CELL_SIZE, CELL_SIZE);
                buttons[x][y].setPrefSize(CELL_SIZE, CELL_SIZE);
                buttons[x][y].setMinSize(CELL_SIZE, CELL_SIZE);

                buttons[x][y].setOnMouseClicked(event -> {
                    buttons[x][y].setText(Integer.toString(numbers[x][y]));

                    if (chosenCell == null) {
                        chosenCell = cells[x][y];
                    } else {
                        aliveCells.forEach(cell -> buttons[cell.getX()][cell.getY()].setDisable(true));

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
                                    aliveCells.remove(cells[x][y]);
                                    aliveCells.remove(cells[chosenCell.getX()][chosenCell.getY()]);

                                    if (aliveCells.size() == 0) {
                                        showWinAlert();
                                    }
                                } else {
                                    buttons[x][y].setText("");
                                    buttons[chosenCell.getX()][chosenCell.getY()].setText("");
                                }

                                aliveCells.forEach(pos -> buttons[pos.getX()][pos.getY()].setDisable(false));

                                chosenCell = null;
                            }
                        };

                        new Thread(sleeper).start();
                    }
                });

                gridPane.add(buttons[x][y], y, x);

                cells[x][y] = new Cell(x, y);
                aliveCells.add(cells[x][y]);
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
            showStartDialog("Let's play again!");
        } else {
            Platform.exit();
        }
    }

    private void initTableNumbers(int size) {
        numbers = new int[size][size];

        List<Integer> gen = new ArrayList<>();

        for (int i = 0; i < size * size / 2; i++) {
            gen.add(i);
            gen.add(i);
        }

        Collections.shuffle(gen);

        Iterator<Integer> iterator = gen.iterator();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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
