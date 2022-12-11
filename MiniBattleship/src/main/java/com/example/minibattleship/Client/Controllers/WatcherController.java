package com.example.minibattleship.Client.Controllers;

import com.example.minibattleship.Client.Cell;
import com.example.minibattleship.Client.Client;
import com.example.minibattleship.Client.TCPConnection;
import com.example.minibattleship.Helpers.MessageType;
import com.example.minibattleship.Helpers.UserMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class WatcherController {
    private final TCPConnection tcpConnection = TCPConnection.getInstance(Client.getSocket());

    @FXML public VBox player2Board;
    @FXML public VBox player1Board;
    @FXML public AnchorPane shipStatus;
    @FXML public Label lblPlayer1ShipQuantity;
    @FXML public Label lblPlayer2ShipQuantity;
    @FXML public Label lblPlayer2Board;
    @FXML public Label lblPlayer1Board;

    @FXML
    public void initialize() {
        shipStatus.setVisible(false);
        Thread thread = new Thread(new ServerListener());
        thread.setDaemon(true);
        thread.start();
    }

    private void populatePlayerBoard(VBox vBox, String[] coordinate) {
        int size = 6;
        for (int y = 0; y < size; y++) {
            HBox row = new HBox();
            for (int x = 0; x < size; x++) {
                Cell cell = new Cell(x, y);
                for (String e : coordinate) {
                    if (e.equals(x + Integer.toString(y))) {
                        cell.setContainShip(true);
                        break;
                    }
                }
                row.getChildren().add(cell);
            }
            vBox.getChildren().add(row);
        }
    }

    private void alert() {
        Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
        alertPrep.setTitle("Game over");
        alertPrep.setHeaderText("The game has ended due to a player has abandoned the game");
        alertPrep.setContentText("");
        alertPrep.showAndWait();
    }

    private class ServerListener implements Runnable {
        private final TCPConnection connection = WatcherController.this.tcpConnection;

        private void populateBoard(int id, UserMessage message) {
            switch (id) {
                case 1 -> Platform.runLater(() -> {
                    WatcherController.this.lblPlayer1Board.setText(message.getUsername());
                    populatePlayerBoard(WatcherController.this.player1Board, message.getMessage().split(","));
                });
                case 2 -> Platform.runLater(() -> {
                    WatcherController.this.lblPlayer2Board.setText(message.getUsername());
                    populatePlayerBoard(WatcherController.this.player2Board, message.getMessage().split(","));
                });
            }
        }

        private void placeShot(UserMessage message) {
            if (message.getMessageType() == MessageType.IN_BATTLE) {
                String coordinate = message.getMessage().split(",")[1];
                int xCoordinate = Integer.parseInt(String.valueOf(coordinate.charAt(0)));
                int yCoordinate = Integer.parseInt(String.valueOf(coordinate.charAt(1)));
                switch (message.getId()) {
                    case 1 ->
                            Platform.runLater(() -> ((Cell) ((HBox) WatcherController.this.player2Board.getChildren().get(yCoordinate)).getChildren().get(xCoordinate)).setWasShot(true));
                    case 2 ->
                            Platform.runLater(() -> ((Cell) ((HBox) WatcherController.this.player1Board.getChildren().get(yCoordinate)).getChildren().get(xCoordinate)).setWasShot(true));
                }
            }
        }

        @Override
        public void run() {
            while (connection.isNotClosed()) {
                UserMessage messageObject = (UserMessage) connection.readSecuredMessage();
                if (messageObject.isAbandonGame()) {
                    Platform.runLater(WatcherController.this::alert);
                }
                String gameState = messageObject.getGameState();
                switch (gameState) {
                    case "PlacingShip" -> populateBoard(messageObject.getId(), messageObject);
                    case "Battling" -> placeShot(messageObject);
                }
            }
        }
    }
}
