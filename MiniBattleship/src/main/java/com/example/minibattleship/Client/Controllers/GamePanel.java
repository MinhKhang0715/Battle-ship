package com.example.minibattleship.Client.Controllers;

import com.example.minibattleship.Client.Cell;
import com.example.minibattleship.Client.ClientTest;
import com.example.minibattleship.Client.TCPConnection;
import com.example.minibattleship.Helper.UserMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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

    public GamePanel() {
        tcpConnection = TCPConnection.getInstance(ClientTest.getSocket());
    }

    public void setUsername(String name) {
        username = name;
    }

    @FXML
    public void initialize() {
        state = GameState.PlacingShip;
        placeMyShip();
        enemyBoard.setDisable(true);
    }

    @FXML
    public void onDonePlacingShip() {
        System.out.println("My coordinate: " + myCoordinate);
        UserMessage userMessage = new UserMessage()
                .setUsername(username)
                .setGameState("PlacingShip")
                .setMessage(myCoordinate);
        tcpConnection.sendMessage(userMessage);
        state = GameState.Battling;
        UserMessage coordinateFromServer = (UserMessage) tcpConnection.readMessage();
        enemyCoordinate = coordinateFromServer.getMessage().split(",");
        boolean isEnemyGoFirst = coordinateFromServer.getIsGoFirst();
        if (!isEnemyGoFirst)
            enemyBoard.setDisable(false);
        myBoard.setDisable(true);
        populateEnemyBoardTest();
        battling();
        Thread thread = new Thread(new ServerListener());
        thread.start();
        btnDonePlacingShip.setDisable(true);
    }

    private void populateEnemyBoardTest() {
        for (int y = 0; y < size; y++) {
            HBox row = new HBox();
            for (int x = 0; x < size; x++) {
                Cell cell = new Cell(x, y);
                for (String coordinate : enemyCoordinate) {
                    if (coordinate.equals(x + Integer.toString(y))) {
                        cell.setContainShip(true);
                        cell.setFill(Color.WHITESMOKE);
                        break;
                    }
                }
                row.getChildren().add(cell);
            }
            enemyBoard.getChildren().add(row);
        }
    }

    private void battling() {
        for (Node node : enemyBoard.getChildren()) {
            HBox row = (HBox) node;
            for (Node nodeCell : row.getChildren()) {
                Cell cell = (Cell) nodeCell;
                cell.setOnMouseClicked(mouseEvent -> {
                    System.out.println("SHOT!!");
                    String shotCoordinate = cell.x + Integer.toString(cell.y);
                    String shotResult = placeShot(cell.x, cell.y);
                    System.out.println(shotResult);
                    if (shotResult.equals("Already shot")) {
                        System.out.println("Already shot");
                        alert("Attention!", "Invalid Shot!!", "The cell had already been shot");
                    } else {
                        if (shotResult.equals("Hit")) {
                            System.out.println("HIT");
                            enemyShips--;
                            if (enemyShips == 0) {
                                alert("Winner", "Congratulation, you won!!", "You have destroyed all enemy's ships");
                                enemyBoard.setDisable(true);
                                myBoard.setDisable(true);
                            } else
                                tcpConnection.sendMessage(new UserMessage().setUsername(username).setGameState("Battling").setMessage("Hit," + shotCoordinate));
                        } else if (shotResult.equals("Missed")) {
                            System.out.println("MISSED");
                            tcpConnection.sendMessage(new UserMessage().setUsername(username).setGameState("Battling").setMessage("Missed," + shotCoordinate));
                        }
                        System.out.println("End of placeShot");
                        enemyBoard.setDisable(true);
                        System.out.println("Waiting for enemy's shot");
                    }
                });
            }
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
            if (!cell.isContainShip()) {
                cell.setFill(Color.YELLOW);
                cell.setContainShip(true);
                return true;
            } else return false;
        } else return false;
    }

    private String placeShot(int x, int y) {
        Cell cell = getCellFromEnemyBoard(x, y);
        if (!cell.getWasShot()) {
            cell.setWasShot(true);
            if (cell.isContainShip()) return "Hit";
            else return "Missed";
        } else return "Already shot";
    }

    private boolean isValidToPlace(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    private Cell getCellFromMyBoard(int x, int y) {
        return (Cell) ((HBox) myBoard.getChildren().get(y)).getChildren().get(x);
    }

    private Cell getCellFromEnemyBoard(int x, int y) {
        return (Cell) ((HBox) enemyBoard.getChildren().get(y)).getChildren().get(x);
    }

    private void alert(String title, String headerText, String contentText) {
        Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
        alertPrep.setTitle(title);
        alertPrep.setHeaderText(headerText);
        alertPrep.setContentText(contentText);
        alertPrep.showAndWait();
    }

    private enum GameState {PlacingShip, Battling, GameOver}

    private class ServerListener implements Runnable {
        private final TCPConnection tcpConnection = GamePanel.this.tcpConnection;

        @Override
        public void run() {
            while (!tcpConnection.isClose()) {
                UserMessage messageObject = (UserMessage) tcpConnection.readMessage();
                System.out.println("Received: User: " + messageObject.getUsername() + " Game state: " + messageObject.getGameState() + " Message: " + messageObject.getMessage());
                String gameState = messageObject.getGameState();
                if ("Battling".equals(gameState)) {
                    String[] enemyShotCoordinate = messageObject.getMessage().split(",");
                    int xCoordinate = Integer.parseInt(String.valueOf(enemyShotCoordinate[1].charAt(0)));
                    int yCoordinate = Integer.parseInt(String.valueOf(enemyShotCoordinate[1].charAt(1)));
                    Platform.runLater(() -> ((Cell) ((HBox) GamePanel.this.myBoard.getChildren().get(yCoordinate)).getChildren().get(xCoordinate)).setWasShot(true));
                    if (enemyShotCoordinate[0].equals("Hit")) {
                        GamePanel.this.remains--;
                        System.out.println("Ship remains: " + GamePanel.this.remains);
                        if (GamePanel.this.remains == 0) {
                            alert("Too bad", "You lost", "All of your ships have been destroyed by the enemy");
                        }
                        Platform.runLater(() -> GamePanel.this.enemyBoard.setDisable(false));
                    } else if (enemyShotCoordinate[0].equals("Missed")) {
                        Platform.runLater(() -> GamePanel.this.enemyBoard.setDisable(false));
                    }
                }
            }
        }
    }
}
