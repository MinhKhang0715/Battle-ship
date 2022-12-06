package com.example.minibattleship.Client.Controllers;

import com.example.minibattleship.Client.Cell;
import com.example.minibattleship.Client.ClientTest;
import com.example.minibattleship.Client.TCPConnection;
import com.example.minibattleship.Helper.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class GamePanel {
    private final TCPConnection tcpConnection;
    private final int size = 6;
    @FXML
    public VBox enemyBoard;
    @FXML
    public VBox myBoard;
    @FXML
    public Button btnDonePlacingShip;

    private int prepShips = 7, remains = 7, enemyShips = 7;
    private GameState state;
    private String username;
    private String[] enemyCoordinate;
    private String myCoordinate = "";
    private boolean isEnemyGoFirst;

    public void setUsername(String name) {
        username = name;
    }

    public GamePanel() {
        tcpConnection = TCPConnection.getInstance(ClientTest.getSocket());
    }

    @FXML
    public void initialize() {
        state = GameState.PlacingShip;
        placeMyShip();
        populateEnemyBoard();
        enemyBoard.setDisable(true);
    }

    @FXML
    public void onDonePlacingShip(ActionEvent actionEvent) {
        System.out.println("My coordinate: " + myCoordinate);
        User user = new User()
                .setUsername(username)
                .setGameState("PlacingShip")
                .setMessage(myCoordinate);
        tcpConnection.sendMessage(user);
        state = GameState.Battling;
        User coordinateFromServer = (User) tcpConnection.readMessage();
        enemyCoordinate = coordinateFromServer.getMessage().split(",");
        enemyBoard.setDisable(false);
        myBoard.setDisable(true);
        btnDonePlacingShip.setDisable(true);
    }

    private void populateEnemyBoard() {
        for (int y = 0; y < size; y++) {
            HBox row = new HBox();
            for (int x = 0; x < size; x++) {
                Cell cell = new Cell(x, y);
                cell.setOnMouseClicked(mouseEvent -> {
                    cell.wasShot = true;
                    String shotCoordinate = cell.x + Integer.toString(cell.y);
                    if (Arrays.asList(enemyCoordinate).contains(shotCoordinate)) {
                        System.out.println("HIT!!!");
                        cell.setFill(Color.CRIMSON);
                        tcpConnection.sendMessage(new User().setUsername(username).setGameState("Battling").setMessage("Hit," + shotCoordinate));
                        enemyShips--;
                        if (enemyShips == 0) {
                            alert("Winner", "Congratulation, you won!!", "You have destroyed all enemy's ships");
                            enemyBoard.setDisable(true);
                            myBoard.setDisable(true);
                        }
                    } else {
                        System.out.println("MISSED");
                        cell.setFill(Color.GREY);
                        tcpConnection.sendMessage(new User().setUsername(username).setGameState("Battling").setMessage("Missed," + shotCoordinate));
                    }
                    enemyBoard.setDisable(true);
                    System.out.println("Waiting for enemy's shot");
                    User fromServer = (User) tcpConnection.readMessage();
                    String[] enemyShotCoordinate = fromServer.getMessage().split(",");
                    int xCoordinate = Integer.parseInt(String.valueOf(enemyShotCoordinate[1].charAt(0)));
                    int yCoordinate = Integer.parseInt(String.valueOf(enemyShotCoordinate[1].charAt(1)));
                    if (enemyShotCoordinate[0].equals("Hit")) {
                        ((Cell) ((HBox) myBoard.getChildren().get(yCoordinate)).getChildren().get(xCoordinate)).setFill(Color.CRIMSON);
                        remains--;
                        System.out.println("Ship remains: " + remains);
                        if (remains == 0) {
                            alert("Too bad", "You lost", "All of your ships have been destroyed by the enemy");
                        }
                    } else if (enemyShotCoordinate[0].equals("Missed")) {
                        ((Cell) ((HBox) myBoard.getChildren().get(yCoordinate)).getChildren().get(xCoordinate)).setFill(Color.GREY);
                        enemyBoard.setDisable(false);
                    }
                });
                row.getChildren().add(cell);
            }
            enemyBoard.getChildren().add(row);
        }
    }

    private void placeMyShip() {
        for (int y = 0; y < size; y++) {
            HBox row = new HBox();
            for (int x = 0; x < size; x++) {
                Cell cell = new Cell(x, y);
                cell.setOnMouseClicked(mouseEvent -> {
                    if (state == GameState.PlacingShip && prepShips > 0) {
                        if (place(cell.x, cell.y)) {
                            prepShips--;
                            myCoordinate += cell.x + Integer.toString(cell.y) + (prepShips == 0 ? "" : ",");
                        } else {
                            alert("Can't place sea man.",
                                    "Results: Unable to deploy due to space being OCCUPIED.",
                                    "Please select another spot!!!");
                        }
                    } else if (prepShips == 0) {
                        alert("Can't place sea man",
                                "Unable to deploy due to exceeding limited number of sea mans available",
                                "Please click the \"Done placing sea man\" button");
                    }
                });
                row.getChildren().add(cell);
            }
            myBoard.getChildren().add(row);
        }
    }

    private boolean place(int x, int y) {
        if (isValidToPlace(x, y)) {
            Cell cell = getCellFromMyBoard(x, y);
            if (!cell.containShip) {
                cell.setFill(Color.YELLOW);
                cell.containShip = true;
                return true;
            } else return false;
        } else return false;
    }

    private boolean isValidToPlace(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private Cell getCellFromMyBoard(int x, int y) {
        return (Cell) ((HBox) myBoard.getChildren().get(y)).getChildren().get(x);
    }

    private void alert(String title, String headerText, String contentText) {
        Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
        alertPrep.setTitle(title);
        alertPrep.setHeaderText(headerText);
        alertPrep.setContentText(contentText);
        alertPrep.showAndWait();
    }

    private enum GameState {PlacingShip, Battling, GameOver}
}
