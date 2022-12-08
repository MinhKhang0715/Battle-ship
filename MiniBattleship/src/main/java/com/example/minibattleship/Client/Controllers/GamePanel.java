package com.example.minibattleship.Client.Controllers;

import com.example.minibattleship.Client.Cell;
import com.example.minibattleship.Client.Client;
import com.example.minibattleship.Client.TCPConnection;
import com.example.minibattleship.Helper.UserMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class GamePanel {
    private final TCPConnection tcpConnection;
    private final int size = 6;

    @FXML public VBox enemyBoard;
    @FXML public VBox myBoard;
    @FXML public Button btnDonePlacingShip;
    @FXML public TextArea messageArea;
    @FXML public TextField messageField;
    @FXML public Button btnSendMessage;
    @FXML public Label lblMyShipQuantity;
    @FXML public Label lblEnemyShipQuantity;
    @FXML public AnchorPane shipStatus;
    @FXML public Label lblEnemyBoard;
    @FXML public Label lblStatus;

    private int prepShips = 7, remains = 7, enemyShips = 7;
    private GameState state;
    private String username;
    private String[] enemyCoordinate;
    private String myCoordinate = "";

    public GamePanel() {
        tcpConnection = TCPConnection.getInstance(Client.getSocket());
    }

    public void setUsername(String name) {
        username = name;
    }

    @FXML
    public void initialize() {
        state = GameState.PlacingShip;
        placeMyShip();
        enemyBoard.setDisable(true);

        messageArea.setVisible(false);
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageField.setVisible(false);
        messageField.setOnKeyPressed(keyEvent -> {
            if (messageField.isFocused() && keyEvent.getCode() == KeyCode.ENTER && messageField.getText() != null) {
                messageArea.appendText(messageField.getText() + "\n");
                tcpConnection.sendMessage(new UserMessage(username, "Battling", "Msg," + messageField.getText()));
                messageField.setText("");
            }
        });
        btnSendMessage.setVisible(false);

        shipStatus.setVisible(false);
        lblEnemyBoard.setVisible(false);
        lblMyShipQuantity.setText(String.valueOf(remains));
        lblEnemyShipQuantity.setText(String.valueOf(enemyShips));
    }

    @FXML
    public void onDonePlacingShip() {
        if (prepShips != 0)
            alert("Attention", "Please place all 7 ships", "Do it!!");
        else {
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
            if (!isEnemyGoFirst) {
                enemyBoard.setDisable(false);
                lblStatus.setText("Your turn");
            } else lblStatus.setText("Waiting for enemy's turn");
            myBoard.setDisable(true);
            populateEnemyBoard();
            lblEnemyBoard.setVisible(true);
            messageArea.setVisible(true);
            messageField.setVisible(true);
            btnSendMessage.setVisible(true);
            shipStatus.setVisible(true);
            battling();
            Thread thread = new Thread(new ServerListener());
            thread.setDaemon(true);
            thread.start();
            btnDonePlacingShip.setDisable(true);
        }
    }

    @FXML
    public void onSendMessageClick() {
        if (messageField.getText() != null) {
            messageArea.appendText(messageField.getText() + "\n");
            tcpConnection.sendMessage(new UserMessage(username, "Battling", "Msg," + messageField.getText()));
            messageField.setText("");
        }
    }

    private void populateEnemyBoard() {
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
                            lblEnemyShipQuantity.setText(String.valueOf(enemyShips));
                            if (enemyShips == 0) {
                                tcpConnection.sendMessage(new UserMessage().setUsername(username).setGameState("Battling").setMessage("Hit," + shotCoordinate));
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
                        lblStatus.setText("Waiting for enemy's turn");
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
                    if (enemyShotCoordinate[0].equals("Hit") || enemyShotCoordinate[0].equals("Missed")) {
                        int xCoordinate = Integer.parseInt(String.valueOf(enemyShotCoordinate[1].charAt(0)));
                        int yCoordinate = Integer.parseInt(String.valueOf(enemyShotCoordinate[1].charAt(1)));
                        Platform.runLater(() -> ((Cell) ((HBox) GamePanel.this.myBoard.getChildren().get(yCoordinate)).getChildren().get(xCoordinate)).setWasShot(true));
                        if (enemyShotCoordinate[0].equals("Hit")) {
                            GamePanel.this.remains--;
                            System.out.println("Ship remains: " + GamePanel.this.remains);
                            if (GamePanel.this.remains == 0) {
                                Platform.runLater(() -> alert("Too bad", "You lost", "All of your ships have been destroyed by the enemy"));
                            }
                            Platform.runLater(() -> {
                                GamePanel.this.enemyBoard.setDisable(false);
                                GamePanel.this.lblMyShipQuantity.setText(String.valueOf(GamePanel.this.remains));
                                GamePanel.this.lblStatus.setText("Your turn");
                            });
                        } else if (enemyShotCoordinate[0].equals("Missed")) {
                            Platform.runLater(() -> {
                                GamePanel.this.enemyBoard.setDisable(false);
                                GamePanel.this.lblStatus.setText("Your turn");
                            });
                        }
                    } else if (enemyShotCoordinate[0].equals("Msg")) {
                        String msg = enemyShotCoordinate[1];
                        String username = messageObject.getUsername();
                        Platform.runLater(() -> {
                            System.out.println(username + msg);
                            GamePanel.this.messageArea.appendText(username + ": " + msg + "\n");
                        });
                    }
                }
            }
        }
    }
}
