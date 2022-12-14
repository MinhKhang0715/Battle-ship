package com.example.minibattleship.Client;

import com.example.minibattleship.Client.Controllers.LoginController;
import com.example.minibattleship.Client.Crypto.AES;
import com.example.minibattleship.Client.Crypto.RSA;
import com.example.minibattleship.Helpers.Frame;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.util.Arrays;
import java.util.Objects;

public class Client extends Application {
    private static Socket socket;
    private String host;
    private int id;
    private int timeout;

    public static Socket getSocket() {
        return socket;
    }

    private void getIP() {
        String apiURL = "https://retoolapi.dev/aEVGGM/data/1";
        try {
            Document doc = Jsoup.connect(apiURL)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .header("Content-Type", "application/json")
                    .method(Connection.Method.GET).execute().parse();
            JSONObject jsonObject = new JSONObject(doc.text());
            this.host = jsonObject.getString("ip");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exchangeKeys() {
        TCPConnection connection = TCPConnection.getInstance(socket);
        Frame frame = (Frame) connection.readMessage();
        id = frame.getId();
        timeout = frame.getTimeout();
        RSA rsa = new RSA(frame.getKeyInBytes());
        AES aes = AES.getInstance();
        Key aesKey = aes.getAesKey();
        System.out.println(Arrays.toString(aesKey.getEncoded()));
        Frame frameToServer = new Frame(id, rsa.encrypt(aesKey.getEncoded()));
        System.out.println("Id: " + id);
        connection.sendMessage(frameToServer);
    }

    @Override
    public void start(Stage stage) {
        getIP();
        int port = 1234;
        try {
            socket = new Socket(host, port);
            exchangeKeys();
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/example/minibattleship/login.fxml")));
            stage.setTitle("Login");
            stage.setScene(new Scene(fxmlLoader.load()));
            ((LoginController) fxmlLoader.getController()).setId(id);
            ((LoginController) fxmlLoader.getController()).setTimeout(timeout);
            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR AT START METHOD");
            throw new RuntimeException(e);
        }
    }
}
