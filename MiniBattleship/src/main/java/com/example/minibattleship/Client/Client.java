package com.example.minibattleship.Client;

import com.example.minibattleship.Client.Controllers.LoginController;
import com.example.minibattleship.Helper.UserMessage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class Client extends Application {
    private static Socket socket;

    public static Socket getSocket() {
        return socket;
    }

    @Override
    public void start(Stage stage) {
        String host = "localhost";
        int port = 1234;
        try {
            socket = new Socket(host, port);
            TCPConnection connection = TCPConnection.getInstance(socket);
            int id = connection.readInt();
            connection.sendMessage(new UserMessage().setUsername("").setGameState("Initial").setMessage("Initial process"));
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/example/minibattleship/login.fxml")));
            stage.setTitle("Login");
            stage.setScene(new Scene(fxmlLoader.load()));
            ((LoginController) fxmlLoader.getController()).setId(id);
            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR AT START METHOD");
            throw new RuntimeException(e);
        }
    }
}
