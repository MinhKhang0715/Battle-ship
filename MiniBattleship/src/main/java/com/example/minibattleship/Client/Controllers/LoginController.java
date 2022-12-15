package com.example.minibattleship.Client.Controllers;

import com.example.minibattleship.Client.Client;
import com.example.minibattleship.Client.TCPConnection;
import com.example.minibattleship.Helpers.UserMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class LoginController {
    @FXML
    public TextField username;
    private final TCPConnection tcpConnection;
    private int id;
    private int timeout;

    public LoginController() {
        Socket socket = Client.getSocket();
        tcpConnection = TCPConnection.getInstance(socket);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void onLoginButtonClicked(ActionEvent actionEvent) {
        String nameOfUser = username.getText();
        if (id >= 3) {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/example/minibattleship/viewer.fxml")));
                Stage stage = new Stage();
                stage.setTitle(nameOfUser);
                stage.setScene(new Scene(loader.load()));
                stage.show();
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                System.out.println("LoginController: ERROR OPENING THE GAME PANEL");
                throw new RuntimeException(e);
            }
        } else {
            if (!nameOfUser.equals("")) {
                UserMessage userMessageToSend = new UserMessage().setUsername(nameOfUser).setGameState("Login").setMessage("Testing");
                tcpConnection.sendSecuredMessage(userMessageToSend);
                System.out.println("Sent " + nameOfUser + " to the server");
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/example/minibattleship/game-panel.fxml")));
                    Stage stage = new Stage();
                    stage.setTitle(nameOfUser);
                    stage.setScene(new Scene(loader.load()));
                    ((GamePanel) loader.getController()).setUsername(nameOfUser);
                    ((GamePanel) loader.getController()).setId(id);
                    ((GamePanel) loader.getController()).setTimeout(timeout);
                    stage.show();
                    ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                    System.out.println("LoginController: ERROR OPENING THE GAME PANEL");
                    throw new RuntimeException(e);
                }
            } else {
                Alert alertPrep = new Alert(Alert.AlertType.INFORMATION);
                alertPrep.setTitle("Error");
                alertPrep.setHeaderText("Results: No username to send");
                alertPrep.setContentText("Please enter an username in the text field!!!");
                alertPrep.showAndWait();
            }
        }
    }
}
